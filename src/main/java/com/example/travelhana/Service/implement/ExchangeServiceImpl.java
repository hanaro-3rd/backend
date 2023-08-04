package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.ExchangeRateDto;
import com.example.travelhana.Dto.ExchangeRequestDto;
import com.example.travelhana.Dto.ExchangeResponseDto;
import com.example.travelhana.Object.ExchangeSuccess;
import com.example.travelhana.Service.ExchangeService;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Util.ExchangeRateUtil;
import org.springframework.http.ResponseEntity;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;

import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExchangeHistoryRepository;
import com.example.travelhana.Repository.KeyMoneyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExchangeServiceImpl implements ExchangeService {

    private final AccountRepository accountRepository;
    private final KeyMoneyRepository keyMoneyRepository;
    private final ExchangeHistoryRepository exchangeHistoryRepository;
    private final UserService userService;
    private final ExchangeRateUtil exchangeRateUtil;

    //1.유효한 계좌인지 확인 -> 전부 다 유효한 계좌임을 가정 O
    //2.잔액부족한거 체크하기 O
    //환율 호출해서 디비에 업데이트하기
    //3.공휴일이 아니면 그냥 바로 환전
    //3-1.화폐단위 확인
    //3-2.사용자 id로 조회하고 거기서 화폐단위가 request의 unit과 같은 외화계좌 가져오기
    //3-2-1.만약 없으면 해당 외환계좌(key_money) 새로 개설
    //3-3.값 업데이트하기
    //4.공휴일이면 isNow 확인해서 처리
    //4-1.isNow가 True면 수수료를 높게 책정해서 환전해주고 나중에 스케줄링으로 환불
    //4-2.isNow가 False면 다음 영업일 환전 예약
    @Transactional
    public ResponseEntity<?> exchange(String accessToken, ExchangeRequestDto request)
            throws URISyntaxException {
        return exchangeInAccountBusinessDay(accessToken, request);
    }

    //외환계좌 만들기
    @Transactional
    public KeyMoney makeKeyMoney(User user, String unit) {
        KeyMoney newKeyMoney = KeyMoney.builder()
                .user(user)
                .unit(unit)
                .balance(0L)
                .build();
        keyMoneyRepository.save(newKeyMoney);
        return newKeyMoney;
    }

    //거래 성공하고 성송
    @Transactional
    public ResponseEntity<?> exchangeInAccountBusinessDay(String accessToken, ExchangeRequestDto dto)
            throws URISyntaxException {


        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_ACCOUNT));

        // 접속한 유저에 대한 계좌 소유 여부 확인
        User user = userService.getUserByAccessToken(accessToken);
        if (!user.equals(account.getUser())) {
            throw new BusinessExceptionHandler(ErrorCode.UNAUTHORIZED_USER_ACCOUNT);
        }

        //유효하지 않은 화폐단위 에러
        Currency currency = Currency.getByCode(dto.getUnit());
        if (currency == null) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGEUNIT);
        }

        Optional<KeyMoney> keyMoney = keyMoneyRepository.findByUser_IdAndUnit(account.getUser().getId(), dto.getUnit());
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
        ExchangeResponseDto exchangeHistory = saveExchangeThings(keyMoney.get(), account, dto); //키머니, 원화계좌, 처리할요청

        ApiResponse apiResponse = ApiResponse.builder()
                .result(exchangeHistory)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ExchangeResponseDto saveExchangeThings(KeyMoney keymoney, Account account, ExchangeRequestDto dto)
            throws URISyntaxException {

        Long money = dto.getMoney(); //요청 원화
        ExchangeSuccess exchangeResult;
        ExchangeRateDto exchangeRateDto=exchangeRateUtil.getExchangeRateByAPI(dto.getUnit());
        Double exchangeRate=exchangeRateDto.getExchangeRate();

        if (dto.getIsBought()) { //원화 -> 외화
            exchangeResult = wonToKey(money, keymoney, account, exchangeRate); //money=원화
        } else { //외화 -> 원화
            exchangeResult = keyToWon(money, keymoney, account, exchangeRate); //money=외화
        }

        return saveExchangeHistory(account, keymoney, exchangeResult, exchangeRate);
    }

    //환전내역 저장
    @Transactional
    public ExchangeResponseDto saveExchangeHistory(Account account, KeyMoney keyMoney, ExchangeSuccess exchangeSuccess, Double rate) {
        ExchangeHistory exchangeHistory = ExchangeHistory
                .builder()
                .accountId(account.getId())
                .exchangeDate(LocalDateTime.now())
                .exchangeRate(rate)
                .keyId(keyMoney.getId())
                .foreignCurrency(exchangeSuccess.getKey()) //환전한 외화
                .isBought(exchangeSuccess.getIsBought())
                .isBusinessday(true)
                .userId(account.getUser().getId())
                .keymoneyBalance(exchangeSuccess.getKeymoneyBalance())
                .money(exchangeSuccess.getWon()) //환전한 원화
                .build();

        exchangeHistoryRepository.save(exchangeHistory);
        ExchangeResponseDto responseDto = ExchangeResponseDto
                .builder()
                .key(exchangeSuccess.getKey())
                .won(exchangeSuccess.getWon())
                .build();

        return responseDto;
    }

    //원화->외화
    @Transactional
    public ExchangeSuccess wonToKey(Long won, KeyMoney keyMoney, Account account, Double rate) {
        Currency currency = Currency.getByCode(keyMoney.getUnit());
        if (currency == null) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGEUNIT);
        }

        Double key = (double) won * (double) currency.getBaseCurrency() / rate;
        Long realkey = Math.round(key);
        keyMoney.updatePlusBalance(realkey); //키머니 잔액 추가
        account.updateBalance(won * (-1)); //원화 잔액 차감

        return new ExchangeSuccess(won, realkey, keyMoney.getBalance(), true);
    }

    //외화->원화
    @Transactional
    public ExchangeSuccess keyToWon(Long key, KeyMoney keyMoney, Account account, Double rate) {
        Currency currency = Currency.getByCode(keyMoney.getUnit());
        if (currency == null) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGEUNIT);
        }

        Double won = (double) key * rate / (double) currency.getBaseCurrency();
        Long realwon = Math.round(won); //외화에서 환전하고 결과 원화
        keyMoney.updatePlusBalance(key * (-1)); //키머니 잔액 차감
        account.updateBalance(realwon); //원화 잔액 추가

        return new ExchangeSuccess(realwon, key, keyMoney.getBalance(), false);
    }

}



