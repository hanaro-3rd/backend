package com.example.travelhana.Dto.Account;

import com.example.travelhana.Dto.ExchangeRateDto;
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
