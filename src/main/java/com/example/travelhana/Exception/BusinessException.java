package com.example.travelhana.Exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;

    public BusinessException(String message,ErrorCode errorCode) {

        super(message);
        this.errorCode=errorCode;

    }


}
