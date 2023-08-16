package com.example.travelhana.Dto.Exchange;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeRequestDto {

	private int accountId;
	private Long money;
	private Double exchangeRate;
	private Boolean isBought;
	private String unit;
	private Long moneyToExchange;

}