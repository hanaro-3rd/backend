package com.example.travelhana.Service;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.ExchangeRequestDto;
import com.example.travelhana.Exception.Response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExchangeHistoryRepository;
import com.example.travelhana.Repository.KeyMoneyRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Util.HolidayUtil;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.reflections.Reflections.log;

@Service
@RequiredArgsConstructor
@Transactional
public class ExchangeService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final KeyMoneyRepository keyMoneyRepository;
    private final ExchangeHistoryRepository exchangeHistoryRepository;
    private final HolidayUtil holidayUtil;
    private final UserService userService;


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
//    @Transactional
//    public void exchange(ExchangeRequestDto request) throws URISyntaxException {
//
//
//        if (request.getIsBusinessday()) {
//                exchangeInAccountBusinessDay(request);
//        }
//            else{
//                //영업일이 아닐 때
//                if(request.getIsNow())
//                {
//                    //영업일이 아니지만 지금 환전을 원할 때
//                    //수수료 크게 떼서 환전 -> 기준을 정하기
//                    //10% 뗀다고 가정하기
//                    Double newrate=request.getExchangeRate()-(request.getExchangeRate()*0.01);
//                    request.setExchangeRate(newrate);
//                    saveExchangeThings(keyMoney,account,request);
//
//
//                    //다음 영업일에 적절히 환불
//                    //{... 다음 영업일 체크해서 환불 스케줄러 시작...}
//
//
//                    //1. 다음날이 영업일이 아닐 때를 체크
//                    //2. 다음 영업일을 찾아서 스케줄링 예약 걸어놓기
//                    LocalDate today=LocalDate.now();
//                    log.info(today.toString());
//                    while(!holidayUtil.isBusinessDay(today))
//                    {
//                        today= today.plusDays(1);
//                    }
//
//                    //다음 영업일 저장
//                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/com/example/travelhana/Data/NextBusinessDay.txt"))) {
//                        writer.write(today.toString());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    //이후 알아서 스케줄링 돌아감
//
//
//
//                }
//
//            }
//        }
//
//
//    }



    //**********다음 영업일에 대해************
    //여러 사람이 공휴일에 환전신청을 해도 "다음 영업일"은 모두에게 동일함.
    //그러니까 없으면 다음 영업일을 저장해놓고
    //있으면 그날 실행하기 -> 환전내역에서 쫙 읽어오기
//    @Scheduled(cron = "0 0 0 * * *") // 매일 자정마다 스케줄링
//    public void yourScheduledMethod() {
//        // 입력받은 날짜를 LocalDate로 파싱
//        String dateString="";
//        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/example/travelhana/Data/NextBusinessDay.txt"))) {
//            dateString = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        LocalDate localDate = LocalDate.parse(dateString);
//        // 현재 날짜를 가져오기
//        LocalDate today = LocalDate.now();
//
//        // 현재 날짜와 스케줄링 대상 날짜가 같으면 로직을 실행
//        if (today.isEqual(localDate)) {
//            // 환전 내역을 쭉 읽어서 환전해야할 데이터들 처리하기
//        }
//    }



    //외환계좌 만들기
    @Transactional
    public KeyMoney makeKeyMoney(User user,String unit)
    {
        KeyMoney newKeyMoney = KeyMoney.builder()
             .user(user)
             .unit(unit)
             .balance(0L)
             .build();
        keyMoneyRepository.save(newKeyMoney);
        System.out.println("keymoney Save!");
        return newKeyMoney;
    }

    @Transactional
    public ResponseEntity<?> exchangeInAccountBusinessDay(ExchangeRequestDto dto)
    {
        try{
            Account account=accountRepository.findById(dto.getAccountId()).orElseThrow(
                    ()->new BusinessExceptionHandler(ErrorCode.NO_ACCOUNT)
            );

            //키머니가 존재하지 않는다면 만들어주기
            Optional<KeyMoney> keyMoney = keyMoneyRepository.findByUserIdAndUnit(account.getUser().getId(), dto.getUnit());
            if(!keyMoney.isPresent()) {
                makeKeyMoney(account.getUser(), dto.getUnit());
            }

            Optional<KeyMoney> keyMoney1 = keyMoneyRepository.findByUserIdAndUnit(account.getUser().getId(), dto.getUnit());

            //유효하지 않은 화폐단위 에러
            Currency currency = Currency.getByCode(keyMoney1.get().getUnit());
            if (currency == null) {
                throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGEUNIT);
            }

            //잔액부족 시 에러
            if(dto.getWon()>account.getBalance()) {
                throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
            }

            ExchangeHistory exchangeHistory = saveExchangeThings(keyMoney1.get(),account,dto);

            ApiResponse apiResponse=ApiResponse.builder()
                    .result(exchangeHistory)
                    .resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
                    .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                    .build();

            return ResponseEntity.ok(apiResponse);
            } catch (BusinessExceptionHandler e) {
            ErrorResponse errorResponse=ErrorResponse.builder()
                    .errorCode(e.getErrorCode().getStatusCode())
                    .errorMessage(e.getMessage())
                    .build();

            return ResponseEntity.ok(errorResponse);
            }
    }

    @Transactional
    public ExchangeHistory saveExchangeThings(KeyMoney keymoney,Account account,ExchangeRequestDto dto)
    {
        Long won=dto.getWon();
        Double rate=dto.getExchangeRate();
        if(dto.getIsBought()) {
            // 키머니에 저장시 필요한 것 = won,keymoney,rate
            //환전된 원화는 keymoney에 저장하고
            Long realkey=wonToKey(won,keymoney,account,rate);

        }
        else {
            Long realwon=keyToWon(won,keymoney,account,rate);

        }


        //환전 내역에 저장시 필요한 것 = account,rate,key
        //결제 기록 테이블에 추가하기
        return saveExchangeHistory(account,keymoney,realkey,won,rate);
    }

    //환전내역 저장
    @Transactional
    public ExchangeHistory saveExchangeHistory(Account account,KeyMoney keyMoney,Long key,Long won,Double rate)
    {
        ExchangeHistory exchangeHistory=new ExchangeHistory().builder()
             .accountId(account.getId())
             .exchangeDate(LocalDateTime.now())
             .exchangeRate(rate)
             .keyId(keyMoney.getId())
             .foreignCurrency(key)
             .isBought(true)
             .isBusinessday(true)
             .userId(account.getUser().getId())
             .won(won)
             .build();

        exchangeHistoryRepository.save(exchangeHistory);
        return exchangeHistory;
    }

    //외환계좌 잔액 업데이트
    @Transactional
    public Long keyToWon(Long won, KeyMoney keyMoney,Account account,Double rate)
    {
        Currency currency = Currency.getByCode(keyMoney.getUnit());
        if (currency == null) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGEUNIT);
        }
        Double key=(double)won*(double)currency.getBaseCurrency()/rate;
        Long realkey=Math.round(key);
        keyMoney.updateBalance(realkey);
        account.updateBalance(won*(-1));
        return realkey;

    }

    @Transactional
    public Long wonToKey(Long won, KeyMoney keyMoney,Account account,Double rate)
    {
        Currency currency = Currency.getByCode(unit);
        if (currency == null) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGEUNIT);
        }
        Double key=(double)won*rate/(double)currency.getBaseCurrency();
        Long realwon=Math.round(key);
        keyMoney.updateBalance(key);
        account.updateBalance(realwon);
        return account.getBalance();
    }

    //예금계좌 잔액 업데이트


        //에러 발생 시 특정 액션이 있으면 try-catch
        //히스토리성 테이블은 주기적으로 폐기처리



}
