package com.example.travelhana.Controller;

import com.example.travelhana.Dto.ExchangeRequestDto;
import com.example.travelhana.Service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeService exchangeService;

    @PostMapping("/exchange")
    public void exchange(@RequestBody ExchangeRequestDto dto) throws URISyntaxException {
        exchangeService.exchange(dto);
    }

}
