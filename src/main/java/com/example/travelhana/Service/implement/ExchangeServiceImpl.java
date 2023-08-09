package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.Exchange.ExchangeRateDto;
import com.example.travelhana.Dto.Exchange.ExchangeRequestDto;
import com.example.travelhana.Dto.Exchange.ExchangeResponseDto;
import com.example.travelhana.Object.ExchangeSuccess;
import com.example.travelhana.Service.ExchangeService;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Util.ExchangeRateUtil;
import com.example.travelhana.Util.HolidayUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;

import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExchangeHistoryRepository;
import com.example.travelhana.Repository.KeymoneyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExchangeServiceImpl implements ExchangeService {

    private final AccountRepository accountRepository;
    private final KeymoneyRepository keyMoneyRepository;
    private final ExchangeHistoryRepository exchangeHistoryRepository;
    private final UserService userService;
    private final ExchangeRateUtil exchangeRateUtil;
    private final HolidayUtil holidayUtil;

    @Transactional
    public ResponseEntity<?> exchange(String accessToken, ExchangeRequestDto request)
            throws URISyntaxException {
        return exchangeAction(accessToken, request);
    }


    @Transactional
    public ResponseEntity<?> exchangeAction(String accessToken,
                                            ExchangeRequestDto dto)
            throws URISyntaxException {

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_ACCOUNT));

        // 접속한 유저에 대한 계좌 소유 여부 확인
        User user = userService.getUserByAccessToken(accessToken);
        if (!user.equals(account.getUser())) {
            throw new BusinessExceptionHandler(ErrorCode.UNAUTHORIZED_USER_ACCOUNT);
        }

        // 유효하지 않은 화폐단위 에러
        Currency currency = Currency.getByCode(dto.getUnit());
        if (currency == null) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGE_UNIT);
        }

        if (dto.getMoney() <= 0) { //0이나 음수는 환전불가
            throw new BusinessExceptionHandler(ErrorCode.NO_ZERO_OR_MINUS);
        }

        Optional<Keymoney> keyMoney = keyMoneyRepository.findByUser_IdAndUnit(
                account.getUser().getId(), dto.getUnit());
        //키머니가 존재하지 않는다면 만들어주기
        if (!keyMoney.isPresent()) {
            keyMoney = Optional.ofNullable(makeKeyMoney(account.getUser(), dto.getUnit()));
        }

        //잔액부족 시 에러
        if (dto.getIsBought()) { //원화->외화 요청 : 원화계좌 확인
            if (dto.getMoney() > account.getBalance()) {
                throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
            }
        } else { //외화->원화 요청 : 외화계좌 확인
            if (dto.getMoney() > keyMoney.get().getBalance()) {
                throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
            }
        }

        //환전 시작
        ExchangeResponseDto exchangeHistory = saveExchangeThings(keyMoney.get(), account,
                dto); //키머니, 원화계좌, 처리할요청

        ApiResponse apiResponse = ApiResponse.builder()
                .result(exchangeHistory)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Transactional
    public ExchangeResponseDto saveExchangeThings(Keymoney keymoney, Account account,
                                                  ExchangeRequestDto dto)
            throws URISyntaxException {

        Long money = dto.getMoney(); //요청 원화
        ExchangeSuccess exchangeResult;
        ExchangeRateDto exchangeRateDto = exchangeRateUtil.getExchangeRateByAPI(dto.getUnit());
        Boolean isBusinessDay = holidayUtil.isBusinessDay(LocalDate.now());
        if (!isBusinessDay) {
            //공휴일이면
            if (!dto.getIsBought()) {
                //외화매도는 안됨
                throw new BusinessExceptionHandler(ErrorCode.ONLY_PUCHASE_IN_HOLIDAY);
            }
            exchangeRateDto.updateExchangeRate(20.0); //수수료 20원 추가해서 환전
        }

        if (dto.getIsBought()) { //원화 -> 외화
            exchangeResult = wonToKey(money, keymoney, account, exchangeRateDto); //money=원화
        } else { //외화 -> 원화
            exchangeResult = keyToWon(money, keymoney, account, exchangeRateDto); //money=외화
        }

        return saveExchangeHistory(account, keymoney, exchangeResult, exchangeRateDto);
    }

    //환전내역 저장
    @Transactional
    public ExchangeResponseDto saveExchangeHistory(Account account, Keymoney keyMoney,
                                                   ExchangeSuccess exchangeSuccess, ExchangeRateDto exchangeRateDto) {
        ExchangeHistory exchangeHistory = ExchangeHistory
                .builder()
                .accountId(account.getId())
                .exchangeDate(LocalDateTime.now())
                .exchangeRate(exchangeRateDto.getExchangeRate())
                .keymoneyId(keyMoney.getId())
                .exchangeKey(exchangeSuccess.getExchangeKey()) //환전한 외화
                .isBought(exchangeSuccess.getIsBought())
                .isBusinessday(true)
                .userId(account.getUser().getId())
                .balance(exchangeSuccess.getKeymoneyBalance())
                .exchangeWon(exchangeSuccess.getExchangeWon()) //환전한 원화
                .build();

        exchangeHistoryRepository.save(exchangeHistory);
        ExchangeResponseDto responseDto;
        if (exchangeSuccess.getIsBought()) { //원화->외화
            responseDto = ExchangeResponseDto
                    .builder()
                    .exchangeFromMoney(exchangeSuccess.getExchangeWon())
                    .exchangeFromUnit("KRW")
                    .exchangeToMoney(exchangeHistory.getExchangeKey())
                    .exchangeToUnit(keyMoney.getUnit())
                    .exchangeRate(exchangeRateDto.getExchangeRate())
                    .changePrice(exchangeRateDto.getChangePrice())
                    .build();
        } else { //외화->원화
            responseDto = ExchangeResponseDto
                    .builder()
                    .exchangeFromMoney(exchangeHistory.getExchangeKey())
                    .exchangeFromUnit(keyMoney.getUnit())
                    .exchangeToMoney(exchangeHistory.getExchangeWon())
                    .exchangeToUnit("KRW")
                    .exchangeRate(exchangeRateDto.getExchangeRate())
                    .changePrice(exchangeRateDto.getChangePrice())
                    .build();
        }

        return responseDto;
    }

    //원화->외화
    @Transactional

    public ExchangeSuccess wonToKey(Long won, Keymoney keyMoney, Account account,
                                    ExchangeRateDto exchangeRateDto) {
        Currency currency = Currency.getByCode(keyMoney.getUnit());
        if (currency == null) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGE_UNIT);
        }

        Double key = (double) won * (double) currency.getBaseCurrency()
                / exchangeRateDto.getExchangeRate();
        Long realkey = Math.round(key);

        if (realkey + keyMoney.getBalance() >= 2000000) {
            throw new BusinessExceptionHandler(ErrorCode.TOO_MUCH_KEYMONEY_BALANCE);
        }

        keyMoney.updatePlusBalance(realkey); //키머니 잔액 추가
        account.updateBalance(won * (-1)); //원화 잔액 차감

        ExchangeSuccess exchangeSuccess = ExchangeSuccess.builder()
                .exchangeWon(won)
                .exchangeKey(realkey)
                .keymoneyBalance(keyMoney.getBalance())
                .isBought(true)
                .build();
        return exchangeSuccess;
    }

    //외화->원화
    @Transactional
    public ExchangeSuccess keyToWon(Long key, Keymoney keyMoney, Account account,
                                    ExchangeRateDto exchangeRateDto) {
        Currency currency = Currency.getByCode(keyMoney.getUnit());
        if (currency == null) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGE_UNIT);
        }

        Double won = (double) key * exchangeRateDto.getExchangeRate()
                / (double) currency.getBaseCurrency();
        Long realwon = Math.round(won); //외화에서 환전하고 결과 원화

        keyMoney.updatePlusBalance(key * (-1)); //키머니 잔액 차감
        account.updateBalance(realwon); //원화 잔액 추가

        ExchangeSuccess exchangeSuccess = ExchangeSuccess.builder()
                .exchangeWon(realwon)
                .exchangeKey(key)
                .keymoneyBalance(keyMoney.getBalance())
                .isBought(false)
                .build();

        return exchangeSuccess;
    }

    //외환계좌 만들기
    @Transactional
    public Keymoney makeKeyMoney(User user, String unit) {
        Keymoney newKeymoney = Keymoney.builder()
                .user(user)
                .unit(unit)
                .balance(0L)
                .build();
        keyMoneyRepository.save(newKeymoney);
        return newKeymoney;
    }
}


