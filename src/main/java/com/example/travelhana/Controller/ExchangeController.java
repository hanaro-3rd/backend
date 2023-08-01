package com.example.travelhana.Controller;

import com.example.travelhana.Dto.ExchangeRequestDto;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Service.ExchangeService;
import io.swagger.models.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeService exchangeService;

    @PostMapping("/exchange")
    public ResponseEntity<?> exchange(@RequestBody ExchangeRequestDto dto) throws URISyntaxException {
        return exchangeService.exchangeInAccountBusinessDay(dto);
    }

}
