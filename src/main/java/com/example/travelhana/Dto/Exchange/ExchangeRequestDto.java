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
	private Boolean isBought;
	private Boolean isNow;
	private String unit;

}