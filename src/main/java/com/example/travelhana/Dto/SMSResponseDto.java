package com.example.travelhana.Dto;

import java.util.List;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SMSResponseDto {

	//    private List<MessageResponseDto> messages;
	private String requestId;
	private LocalDateTime requestTime;
	private String statusCode;
	private String statusName;

}
