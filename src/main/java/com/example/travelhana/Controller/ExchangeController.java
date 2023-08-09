package com.example.travelhana.Controller;

//import com.example.travelhana.Dto.Exchange.ExchangeRateDto;
import com.example.travelhana.Dto.Exchange.ExchangeRateDto;
import com.example.travelhana.Dto.Exchange.ExchangeRateInfo;
import com.example.travelhana.Dto.Exchange.ExchangeRequestDto;
import com.example.travelhana.Service.ExchangeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
	@GetMapping("/inserttoredis")
	public ExchangeRateDto getExchangeRate(
			@RequestHeader(value = "Authorization") String ignoredAccessToken) throws URISyntaxException {
        System.out.println("컽트롤러 진입");

        String key= LocalDate.now().toString();
        return exchangeService.insertRedis(key);
	}

    @GetMapping("/getfromredis")
    public ExchangeRateDto getfromredis() throws JsonProcessingException {
        return exchangeService.getDtoFromRedis();
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