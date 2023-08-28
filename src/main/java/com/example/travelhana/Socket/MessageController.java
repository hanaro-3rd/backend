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

	// 새로운 사용자가 웹 소켓을 연결할 때 실행됨
//	@EventListener
//	public void handleWebSocketConnectListener(SessionConnectEvent event) {
//		System.out.println("새로운 소켓 연결");
//	}
//
//	// 사용자가 웹 소켓 연결을 끊으면 실행됨
//	@EventListener
//	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//		StompHeaderAccessor headerAccesor = StompHeaderAccessor.wrap(event.getMessage());
//		String sessionId = headerAccesor.getSessionId();
//		System.out.println("세션 끊김 아이디 : " + sessionId);
//	}
}