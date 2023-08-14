package com.example.travelhana.Dto.Authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FindPasswordRequestDto {
	private String name;
	private String registrateNum;
	private String phoneNum;
	private String deviceId;

}
