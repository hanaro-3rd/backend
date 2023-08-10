package com.example.travelhana.Dto.Exchange;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ExchangeRateDto implements Serializable {

    ExchangeRateInfo jpy;
    ExchangeRateInfo usd;
    ExchangeRateInfo eur;

}
