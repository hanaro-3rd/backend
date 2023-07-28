package com.example.travelhana.Exception.Code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode{
    INVALID_PASSWORD(400,"400","비밀번호는 6개의 숫자로 구성해주세요"),
    BUSINESS_EXCEPTION_ERROR(500,"500","TOKEN isn't userId"),
    INSUFFICIENT_BALANCE(500,"500","계좌 잔액이 부족합니다."),
    INVALID_EXCHANGEUNIT(500,"500","유효하지 않은 화폐단위입니다."),
    NO_ACCOUNT(500,"500","계좌가 존재하지 않습니다.");


    private int statusCode; //404
    private String status; //NOT_FOUNT
    private String message;




}
