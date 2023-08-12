package com.example.travelhana.Dto.Keymoney;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class KeymoneyDto {

	private Long historyId;
	private String subject;
	private String type;
	private String category;
	private LocalDateTime createdAt;
	private Long keymoney;
	private Long balance;
	private String unit;
	private Boolean isDeposit;

}
