package com.example.travelhana.Dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeRequestDto {
    private int accountId;
    private Long won;
    private Double exchangeRate;
    private Boolean isBusinessday;
    private Boolean isBought;
    private Boolean isNow;
    private String unit;


}
