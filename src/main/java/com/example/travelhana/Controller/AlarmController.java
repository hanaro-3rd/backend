package com.example.travelhana.Controller;

import com.example.travelhana.Dto.AlarmRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequiredArgsConstructor
public class AlarmController {

	private final SimpMessageSendingOperations messagingTemplate;


	//"/endpoint"로 소켓을 연결하면
	//클라이언트는 /sub/{userId} 구독
	//구독중인 클라이언트에게 메세지전송
	@MessageMapping("/{userId}")
	public void message(@DestinationVariable("userId") Long userId, AlarmRequestDto dto, @RequestHeader(value = "Authorization") String accessToken) {
		messagingTemplate.convertAndSend("/sub/" + userId, "alarm socket connection completed.");
	}
}