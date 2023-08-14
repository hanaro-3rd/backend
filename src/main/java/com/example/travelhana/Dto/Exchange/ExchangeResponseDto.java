package com.example.travelhana.Dto.Exchange;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeResponseDto {

	private Long exchangeToMoney;
	private Long exchangeFromMoney;
	private String exchangeToUnit;
	private String exchangeFromUnit;
	private Double exchangeRate;
	private Double changePrice;

}