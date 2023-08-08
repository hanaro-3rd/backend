package com.example.travelhana.Dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeResponseDto {

	private Long exchangeWon;
	private Long exchangeKey;
	private Double exchangeRate;
	private String unit;
	private Double changePrice;

}