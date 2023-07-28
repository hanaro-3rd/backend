package com.example.travelhana.Exception.Code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {

    //비밀번호 형식 에러
    INVALID_PASSWORD(400, "INVALID_PASSWORD", "비밀번호는 6개의 숫자로 구성해주세요"),
    //환전 시 원화 잔액 부족 에러
    INSUFFICIENT_BALANCE(500, "INSUFFICIENT_BALANCE", "계좌 잔액이 부족합니다."),
    //환전 시 화폐단위 입력 에러
    INVALID_EXCHANGEUNIT(500, "INVALID_EXCHANGEUNIT", "유효하지 않은 화폐단위입니다."),
    //존재하지 않는 계좌 에러
    NO_ACCOUNT(500, "NO_ACCOUNT", "계좌가 존재하지 않습니다.");

    private int statusCode;
    private String status;
    private String message;

}
