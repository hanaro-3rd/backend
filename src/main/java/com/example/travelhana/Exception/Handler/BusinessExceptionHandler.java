package com.example.travelhana.Exception.Handler;

import com.example.travelhana.Exception.Code.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessExceptionHandler extends RuntimeException {

	private ErrorCode errorCode;

	public BusinessExceptionHandler(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

}
