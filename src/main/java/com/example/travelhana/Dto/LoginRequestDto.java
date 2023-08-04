package com.example.travelhana.Dto;

import com.example.travelhana.Domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

	private String deviceId;
	private String password;

}

