package com.example.travelhana.Dto.Authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleToUserRequestDto {

	private String deviceId;
	private Long roleId;

}
