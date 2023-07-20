package com.example.travelhana.Service;

import com.example.travelhana.Dto.ExchangeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExchangeService {

    //1.유효한 계좌인지 확인
    //2.잔액부족한거 체크하기
    //3.공휴일이 아니면 그냥 바로 환전
    //4.공휴일이면 isNow 확인해서 처리
    //4-1.isNow가 True면 수수료를 높게 책정해서 환전해주고 나중에 스케줄링으로 환불
    //4-2.isNow가 False면 다음 영업일 환전 예약
    public void exchange(ExchangeRequestDto request)
    {




    }



}
