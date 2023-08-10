package com.example.travelhana.Service;

//import com.example.travelhana.Dto.Exchange.ExchangeRateDto;
import com.example.travelhana.Dto.Exchange.ExchangeRateDto;
import com.example.travelhana.Dto.Exchange.ExchangeRateInfo;
import com.example.travelhana.Dto.Exchange.ExchangeRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;


public interface ExchangeService {

    ResponseEntity<?> getExchangeRate() throws URISyntaxException;

    ResponseEntity<?> exchange(String accessToken, ExchangeRequestDto request)
            throws URISyntaxException;

    //	void inseretRedis(RedisTestDto dto);
//	ResponseEntity<?> getRedisData();
    ExchangeRateDto insertRedis(String rateinfo) throws URISyntaxException;
    ExchangeRateDto getDtoFromRedis() throws JsonProcessingException;
}



