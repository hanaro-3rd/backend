package com.example.travelhana.Dto;

import com.example.travelhana.Domain.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KeymoneyDto {

	private Integer id;
	private User user;
	private String unit;
	private Long balance;

}
