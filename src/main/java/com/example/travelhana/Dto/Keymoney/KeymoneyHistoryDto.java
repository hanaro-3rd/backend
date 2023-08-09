package com.example.travelhana.Dto.Keymoney;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class KeymoneyHistoryDto {

	private int keymoneyId;
	private Long totalBalance;
	private List<KeymoneyDto> keymoneyHistory;

}
