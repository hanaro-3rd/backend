package com.example.travelhana.Dto.Authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserResponseDto {

	private String name;
	private String phoneNum;
	private String registrationNum;
	private LocalDateTime createdAt;
}
