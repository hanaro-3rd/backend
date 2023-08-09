package com.example.travelhana.Controller;

import com.example.travelhana.Dto.Exchange.ExchangeRequestDto;
import com.example.travelhana.Service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @PostMapping("/exchange")
    public ResponseEntity<?> exchange(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestBody ExchangeRequestDto exchangeRequestDto) throws URISyntaxException {
        return exchangeService.exchange(accessToken, exchangeRequestDto);
    }
	@GetMapping("")
	public ResponseEntity<?> getExchangeRate(
			@RequestHeader(value = "Authorization") String ignoredAccessToken) throws URISyntaxException {
		return exchangeService.getExchangeRate();
	}


//    @PostMapping("/redistest")
//    public void insertRedis(@RequestBody RedisTestDto dto) {
//        exchangeService.inseretRedis(dto);
//    }

//    @GetMapping("/exchangeInformation")
//    public ResponseEntity<?> exchangeInfo(){
//        return exchangeService.getRedisData();
//    }
}