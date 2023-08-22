package com.example.travelhana.Dto.Authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UpdateDeviceRequestDto {
	private String phonenum;
	private String newDeviceId;

}
