package com.example.travelhana.Socket;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
@RequiredArgsConstructor
public class MessageController {
	private final SimpMessageSendingOperations simpMessageSendingOperations;

	@MessageMapping("/alarm")
	public void message(Message message){
		simpMessageSendingOperations.convertAndSend("/sub/channel/"+message.getChannelId(),message);
	}

}