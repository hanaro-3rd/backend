package com.example.travelhana.Dto.Notification;

import com.example.travelhana.Domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class NotificationResponseDto {

	private String type;
	private String sender;
	private String channelId;
	private String data;
	private int id;
	private LocalDateTime createdAt;

}
