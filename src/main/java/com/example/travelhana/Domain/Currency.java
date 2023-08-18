package com.example.travelhana.Domain;

import lombok.Getter;

@Getter
public enum Currency {
	JPY("JPY", 100, 1000), // 일본 엔 환율
	EUR("EUR", 1, 10), // 유럽 유로 환율
	USD("USD", 1, 10), // 미국 달러 환율
	KRW("KRW", 1000, 10000); // 미국 달러 환율


	private final String code;
	private final int baseCurrency;
	private final int minCurrency;

	Currency(String code, int baseCurrency, int minCurrency) {
		this.code = code;
		this.baseCurrency = baseCurrency;
		this.minCurrency = minCurrency;
	}


	public static Currency getByCode(String code) {
		for (Currency currency : values()) {
			if (currency.getCode().equals(code)) {
				return currency;
			}
		}
		return null;
	}

}
