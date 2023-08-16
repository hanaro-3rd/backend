package com.example.travelhana.Dto.Keymoney;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class KeymoneyMarkerDto {

	private Long historyId;
	private String type;
	private Long amount;
	private String unit;
	private String place;
	private LocalDateTime pickDate;
	private Boolean isDeposit;

}
