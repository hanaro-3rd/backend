package com.example.travelhana.Dto.Keymoney;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KeymoneySimpleDto {

	private String unit;
	private Long balance;

}
