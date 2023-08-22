package com.example.travelhana.Dto.Exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ExchangeRateDto implements Serializable {

	ExchangeRateInfo jpy;
	ExchangeRateInfo usd;
	ExchangeRateInfo eur;
	@CreationTimestamp
	LocalDateTime updatedAt=LocalDateTime.now();

}
