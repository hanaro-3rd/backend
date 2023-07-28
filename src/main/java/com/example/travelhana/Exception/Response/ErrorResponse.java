package com.example.travelhana.Exception.Response;

import com.example.travelhana.Exception.Code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final int errorCode;
    private final String errorMessage;

    public static ErrorResponse of(final ErrorCode code, final String reason) {
        return new ErrorResponse(code.getStatusCode(), code.getMessage());
    }
}