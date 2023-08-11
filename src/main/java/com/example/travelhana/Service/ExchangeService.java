package com.example.travelhana.Service;

import com.example.travelhana.Dto.Exchange.ExchangeRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;


public interface ExchangeService {

	ResponseEntity<?> getExchangeRate() throws URISyntaxException;

	ResponseEntity<?> exchange(String accessToken, ExchangeRequestDto request)
			throws URISyntaxException;

	ResponseEntity<?> getDtoFromRedis() throws JsonProcessingException;

}



