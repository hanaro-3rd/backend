package com.example.travelhana.Dto;

import com.example.travelhana.Projection.AccountInfoProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ConnectedAccountListDto {

	List<AccountConnectResultDto> accounts;
	Boolean isBusinessDay;
	ExchangeRateDto usd;
	ExchangeRateDto jpy;
	ExchangeRateDto eur;

}
