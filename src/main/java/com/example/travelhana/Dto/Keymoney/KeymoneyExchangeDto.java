package com.example.travelhana.Dto.Keymoney;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class KeymoneyExchangeDto {

	private Long historyId;
	private String type;
	private String unit;
	private Long exchangeWon;
	private Long exchangeKey;
	private Double exchangeRate;
	private LocalDateTime createdAt;
	private Boolean isDeposit;

}
