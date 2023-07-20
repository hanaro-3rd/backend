package com.example.travelhana.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode{
    INVALID_PASSWORD(400,"Internal Server Error","비밀번호는 6개의 숫자로 구성해주세요"),
    BUSINESS_EXCEPTION_ERROR(500,"TOKEN isn't userId","TOKEN isn't userId");




    private int statusCode; //404
    private String status; //NOT_FOUNT
    private String message;




}
