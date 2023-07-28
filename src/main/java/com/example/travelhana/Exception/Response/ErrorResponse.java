package com.example.travelhana.Exception.Response;

import com.example.travelhana.Exception.Code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final int errorCode;
    private final String errorMessage;

    /**
     * Global Exception 전송 타입-1
     *
     * @param code          ErrorCode
     * @param bindingResult BindingResult
     * @return ErrorResponse
     */


    /**
     * Global Exception 전송 타입-2
     *
     * @param code ErrorCode
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code.getStatusCode(),code.getMessage());
    }

    /**
     * Global Exception 전송 타입-3
     *
     * @param code   ErrorCode
     * @param reason String
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code, final String reason) {
        return new ErrorResponse(code.getStatusCode(),code.getMessage());
    }
}