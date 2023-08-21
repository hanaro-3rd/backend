package com.example.travelhana.Exception.Handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.travelhana.Dto.ResponseDto;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Response.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@ControllerAdvice
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
	public ResponseEntity<ErrorResponse> refreshTokenVerificationException(JWTVerificationException e) {
		ErrorResponse errorResponse = new ErrorResponse(400, "유효하지 않은 Refresh Token 입니다.");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exception(Exception e) {
		ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BusinessExceptionHandler.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessExceptionHandler e) {
		log.error("에러가 발생했습니다. : {}", e.getMessage(), e);
		ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode().getStatusCode(), e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatusCode()));
	}

}
