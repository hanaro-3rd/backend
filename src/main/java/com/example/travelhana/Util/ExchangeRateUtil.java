package com.example.travelhana.Util;

import com.example.travelhana.Domain.ExchangeRate;
import com.example.travelhana.Dto.Exchange.ExchangeRateDto;
import com.example.travelhana.Repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class ExchangeRateUtil {

	private final ExchangeRateRepository exchangeRateRepository;

	public ExchangeRateDto getExchangeRateByAPI(String currencyCode) throws URISyntaxException {

		System.setProperty("https.protocols", "TLSv1.2");

		StringBuilder urlBuilder = new StringBuilder(
				"https://quotation-api-cdn.dunamu.com/v1/forex/recent?codes=FRX.KRW"); /*URL*/
		urlBuilder.append(URLEncoder.encode(currencyCode, StandardCharsets.UTF_8)); /*Service Key*/

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

		RestTemplate restTemplate = new RestTemplate();

		URI uri = new URI(urlBuilder.toString());

		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request,
				String.class);

		JSONArray parser = new JSONArray(response.getBody());
		JSONObject object = parser.getJSONObject(0);

		Double basePrice = object.getDouble("basePrice");
		Double changePrice = object.getDouble("signedChangePrice");

		ExchangeRate exchangeRate = ExchangeRate.builder()
				.exchangeRate(basePrice)
				.changePrice(changePrice)
				.unit(currencyCode)
				.updatedAt(LocalDateTime.now())
				.build();

		exchangeRateRepository.save(exchangeRate);

		return new ExchangeRateDto(basePrice, changePrice);
	}
}
