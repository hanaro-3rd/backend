package com.example.travelhana.Dto;

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
	List<ConnectedAccountDto> accountDtos;
	Boolean isBusinessDay;
	Double exchangeRate;
	Double appreciationRate;
}
