package com.example.travelhana.Controller;


import com.example.travelhana.Dto.Exchange.ExchangeRequestDto;
import com.example.travelhana.Service.ExchangeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange")
public class ExchangeController {

	private final ExchangeService exchangeService;

	@PostMapping("")
	public ResponseEntity<?> exchange(
			@RequestHeader(value = "Authorization") String accessToken,
			@RequestBody ExchangeRequestDto exchangeRequestDto) throws URISyntaxException {
		return exchangeService.exchange(accessToken, exchangeRequestDto);
	}


	@GetMapping("/getFromRedis")
<<<<<<< HEAD
	public ResponseEntity<?> getFromRedis(
			@RequestHeader(value = "Authorization") String ignoredAccessToken) throws JsonProcessingException {
=======
	public ResponseEntity<?> getFromRedis() throws JsonProcessingException {
>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3
		return exchangeService.getExchangeRateFromRedis();
	}

	@GetMapping("/getFromApi")
	public ResponseEntity<?> getExchangeRate(
			@RequestHeader(value = "Authorization") String ignoredAccessToken) throws URISyntaxException {
		return exchangeService.getExchangeRate();
	}

}