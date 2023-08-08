package com.example.travelhana.Dto.Exchange;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeResponseDto {

	private Long won;
	private Long key;
	private Double exchangeRate;
	private String unit;
	private Double changePrice;

}