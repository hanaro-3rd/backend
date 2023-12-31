package com.example.travelhana.Dto.Authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SignupRequestDto {

	private String name;
	private String password;
	private String pattern;
	private String phonenum;
	private String deviceId;
	private String registrationNum;

	public void encodePassword(String encodedPassword) {
		this.password = encodedPassword;
	}
}
