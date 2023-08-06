package com.example.travelhana.Service;

import com.example.travelhana.Dto.ExchangeRequestDto;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;


public interface ExchangeService {

	ResponseEntity<?> exchange(String accessToken, ExchangeRequestDto request)
			throws URISyntaxException;

}



