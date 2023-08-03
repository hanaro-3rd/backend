package com.example.travelhana.Service;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.ExchangeRequestDto;
import com.example.travelhana.Dto.ExchangeResponseDto;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Object.ExchangeSuccess;
import org.springframework.http.ResponseEntity;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;

import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExchangeHistoryRepository;
import com.example.travelhana.Repository.KeyMoneyRepository;
import com.example.travelhana.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;


public interface ExchangeService {
	ResponseEntity<?> exchange(String accessToken, ExchangeRequestDto request);

}



