package com.example.travelhana.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ExchangeRequestDto {
    private String accountId;
    private Long won;
    private Double exchangeRate;
    private Boolean isBusinessday;
    private Boolean isBought;
    private Boolean isNow;


}
