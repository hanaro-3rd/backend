package com.example.travelhana.Controller;

import com.example.travelhana.Dto.Notification.NotificationRequestDto;
import com.example.travelhana.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final NotificationService notificationService;

	@MessageMapping("/alarm")
	@SendTo("/sub/channel/keylog")
	public ResponseEntity<?> message(NotificationRequestDto notification) {
		return notificationService.sendNotification(notification);
	}

}