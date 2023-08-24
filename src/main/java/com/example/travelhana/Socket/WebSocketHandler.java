package com.example.travelhana.Socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.reflections.util.Utils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.swing.text.Utilities;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

	private final Map<String, WebSocketSession> sessions=new ConcurrentMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session)throws Exception{
		var sessionId=session.getId();
		sessions.put(sessionId,session);

		Message message=Message.builder()
				.sender(sessionId)
				.receiver("all")
				.build();
		message.newConnect();

		sessions.values().forEach(s->{
			try {
				if(!s.getId().equals(sessionId)){
					s.sendMessage(new TextMessage(Utils.getFieldFromString(message)));
				}
			}
		});
	}
}
