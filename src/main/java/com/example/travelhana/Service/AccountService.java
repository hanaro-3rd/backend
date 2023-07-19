package com.example.travelhana.Service;

import com.example.travelhana.Dto.ConnectedAccountDto;
import com.example.travelhana.Dto.ConnectedAccountListDto;
import com.example.travelhana.Dto.ExchangeRateDto;
import com.example.travelhana.Util.ExchangeRateUtil;
import com.example.travelhana.Util.HolidayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final HolidayUtil holidayUtil;
	private final ExchangeRateUtil exchangeRateUtil;

	public ConnectedAccountListDto getConnectedAccountList(String currencyCode) throws URISyntaxException, IOException {

		List<ConnectedAccountDto> connectedAccounts = new ArrayList<>();

		Boolean isBusinessdDay = holidayUtil.isBusinessDay();
		ExchangeRateDto exchangeRateDto = exchangeRateUtil.getExchangeRateByAPI(currencyCode);

		ConnectedAccountListDto connectedAccountListDto = new ConnectedAccountListDto(connectedAccounts, isBusinessdDay, exchangeRateDto.getExchangeRate(), exchangeRateDto.getAppreciationRate());

		return connectedAccountListDto;
	}
}
