package com.example.travelhana.Dto.Authentication;

import com.example.travelhana.Domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponseDto {

	private String name;
	private String phoneNum;
	private String registrateNum;
	private LocalDateTime createdAt;
}
