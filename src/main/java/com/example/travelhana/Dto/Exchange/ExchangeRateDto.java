package com.example.travelhana.Dto.Exchange;

import com.example.travelhana.Domain.ExchangeRate;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    LocalDateTime updatedAt;

}
