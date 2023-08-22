package com.example.travelhana.Dto.Authentication;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SMSResponseDto {

	private String requestId;
	private LocalDateTime requestTime;
	private String statusCode;
	private String statusName;

}
