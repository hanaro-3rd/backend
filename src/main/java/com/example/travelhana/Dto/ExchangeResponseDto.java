package com.example.travelhana.Dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeResponseDto {

    private Long won;
    private Long key;
    private Double exchangeRage;
}