package com.example.travelhana.Exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.travelhana.Exception.Code.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Refresh Token 만료
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> refreshTokenExpiredException() {
        ErrorResponse errorResponse = new ErrorResponse(401, "Refresh Token이 만료되었습니다. 다시 로그인을 진행하여 Token을 갱신해주세요.");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // 잘못된 Refresh Token
    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorResponse> refreshTokenVerificationException() {
        ErrorResponse errorResponse = new ErrorResponse(400, "유효하지 않은 Refresh Token 입니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BusinessExceptionHandler.class)
    public ResponseEntity<ErrorResponse> handleCustomException(BusinessExceptionHandler ex) {

        final ErrorResponse response = new ErrorResponse(ErrorCode.BUSINESS_EXCEPTION_ERROR.getStatusCode(),ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}