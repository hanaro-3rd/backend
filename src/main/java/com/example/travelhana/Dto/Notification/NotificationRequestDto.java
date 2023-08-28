package com.example.travelhana.Dto.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class NotificationRequestDto {

	private String type;
	private String sender;
	private String channelId;
	private String data;
}
