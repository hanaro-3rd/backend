package com.example.travelhana.Exception.Response;

import com.example.travelhana.Exception.Code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {

	private final int errorCode;
	private final String errorMessage;

}