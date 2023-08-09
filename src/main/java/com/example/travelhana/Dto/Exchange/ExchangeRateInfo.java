package com.example.travelhana.Dto.Exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ExchangeRateInfo {

	Double exchangeRate;
	Double changePrice;
	public void updateExchangeRate(Double charge) {
		this.exchangeRate += charge;
	}

}
