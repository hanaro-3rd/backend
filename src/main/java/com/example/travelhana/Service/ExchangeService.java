package com.example.travelhana.Service;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.ExchangeRequestDto;
import com.example.travelhana.Dto.UserResponseDto;
import com.example.travelhana.Exception.BusinessException;
import com.example.travelhana.Exception.ErrorCode;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExchangeHistoryRepository;
import com.example.travelhana.Repository.KeyMoneyRepository;
import com.example.travelhana.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ExchangeService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    private final KeyMoneyRepository keyMoneyRepository;
    private final ExchangeHistoryRepository exchangeHistoryRepository;


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
    public void exchange(ExchangeRequestDto request) {
        Account account = validateAccount(request.getAccountId(), request.getWon());

        if (account != null) {
            Boolean isBusinessDay = request.getIsBusinessday();
            if (isBusinessDay) {
                System.out.println(account.getUser().getId()+request.getUnit());
                KeyMoney keyMoney = keyMoneyRepository.findByUserIdAndUnit(account.getUser().getId(), request.getUnit());
                if (keyMoney == null) {
                    //해당 화폐단위의 외환계좌가 존재하지 않으면 새로만들기
                    exchangeInAccount(account,makeKeyMoney(account.getUser(), request.getUnit()),request.getWon(),request.getExchangeRate());
                }
                exchangeInAccount(account,keyMoney,request.getWon(),request.getExchangeRate());

            }


        }


    }

    @Transactional
    public KeyMoney makeKeyMoney(User user,String unit)
    {
        KeyMoney newKeyMoney = KeyMoney.builder()
             .user(user)
             .unit(unit)
             .balance(0L)
             .build();
        return newKeyMoney;
    }

    @Transactional
    public void exchangeInAccount(Account account,KeyMoney keyMoney,Long won,Double rate)
    {
        //환전하기
        //엔화 기준 환율이 915.32다 = 100엔을 사는데 915.32원

        //원화 -> 외화
        Currency currency = Currency.getByCode(keyMoney.getUnit());
        if (currency == null) {
            throw new BusinessException("유효하지 않은 화폐단위입니다.",ErrorCode.INVALID_EXCHANGEUNIT);
            //response 500으로 던져주기
        }
        Double key=(double)won*(double)currency.getBaseCurrency()/rate;
        Long realkey=Math.round(key);
        //환전된 원화는 keymoney에 저장하고
        keyMoney.updateBalance(realkey);
        keyMoneyRepository.save(keyMoney);
        //원화는 account에서 차감하기
        account.updateBalance(won*(-1));
        accountRepository.save(account);
        //결제 기록 테이블에 추가하기
        ExchangeHistory exchangeHistory=new ExchangeHistory().builder()
             .accountId(account.getId())
             .exchangeDate(LocalDateTime.now())
             .exchangeRate(rate)
             .keyId(keyMoney.getId())
             .foreignCurrency(realkey)
             .isBought(true)
             .isBusinessday(true)
             .userId(account.getUser().getId())
             .won(account.getBalance())
             .build();
        exchangeHistoryRepository.save(exchangeHistory);


    }



    //계좌의 여러 유효성검사
    public Account validateAccount(int accountId,Long won)
    {
        Account account=accountRepository.findById(accountId).orElseThrow(
             ()-> new BusinessException("계좌가 존재하지 않습니다.", ErrorCode.NO_ACCOUNT)
        );

        if(account.getBalance()< won){
            throw new BusinessException("잔액이 부족합니다.",ErrorCode.INSUFFICIENT_BALANCE);
        }
        return account;
    }



}
