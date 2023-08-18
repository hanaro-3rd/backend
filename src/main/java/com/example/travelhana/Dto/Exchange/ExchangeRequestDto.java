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
	private Double changePrice;
	private Boolean isBought;
	private String unit;
	private Long moneyToExchange;

	public void updateExchangeRate(Double rate){
		this.exchangeRate+=rate;
	}

}