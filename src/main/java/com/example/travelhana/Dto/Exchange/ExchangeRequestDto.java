package com.example.travelhana.Dto.Exchange;

import com.example.travelhana.Domain.ExchangeReservation;
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


	public ExchangeReservation toEntity(){
		return ExchangeReservation.builder()
				.accountId(accountId)
				.money(money)
				.isBought(isBought)
				.isNow(isNow)
				.unit(unit)
				.build();

	}

}