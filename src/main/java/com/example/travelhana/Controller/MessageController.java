package com.example.travelhana.Controller;

import com.example.travelhana.Domain.Notification;
import com.example.travelhana.Dto.Notification.NotificationRequestDto;
import com.example.travelhana.Dto.Notification.NotificationResponseDto;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final NotificationService notificationService;
	private final SimpMessageSendingOperations simpMessageSendingOperations;

//	@MessageMapping("/alarm")
//	@SendTo("/sub/channel/keylog")
//	public ResponseEntity<?> message(NotificationRequestDto notification) {
//		return notificationService.sendNotification(notification);
//	}

	@MessageMapping("/alarm")
	public void message(NotificationResponseDto message){
		simpMessageSendingOperations.convertAndSend("/sub/channel/"+message.getChannelId(),message);
	}

	@PostMapping("/notification")
	public ResponseEntity<?> sendNotification(@RequestBody NotificationRequestDto dto){
		ResponseEntity response=notificationService.sendNotification(dto);
		ApiResponse apiResponse=(ApiResponse)response.getBody();
		NotificationResponseDto responseDto= (NotificationResponseDto) apiResponse.getResult();
		message(responseDto);
		return notificationService.sendNotification(dto);
	}
}