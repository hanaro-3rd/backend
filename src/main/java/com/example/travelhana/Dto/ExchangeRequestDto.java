package com.example.travelhana.Dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeRequestDto {

	private int accountId;
	private Long money;
	private Boolean isBusinessday;
	private Boolean isBought;
	private Boolean isNow;
	private String unit;

}