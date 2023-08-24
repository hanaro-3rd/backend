package com.example.travelhana.Socket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@Controller
@RequiredArgsConstructor
public class MessageController {
	private final SimpMessageSendingOperations simpMessageSendingOperations;

	@MessageMapping("/alarm")
	public void message(Message message){
		simpMessageSendingOperations.convertAndSend("/sub/channel/"+message.getChannelId(),message);
	}
}
