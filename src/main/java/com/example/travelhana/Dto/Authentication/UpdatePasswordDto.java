package com.example.travelhana.Dto.Authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UpdatePasswordDto {
	private String deviceId;
	private String newPassword;
	private String name;
	private String registrateNum;
	private String phoneNum;
}
