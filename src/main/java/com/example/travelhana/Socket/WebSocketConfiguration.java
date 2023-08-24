package com.example.travelhana.Socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
		registry.addHandler(signalingSocketHandler(),"/sub")
				.setAllowedOrigins("*"); //클라이언트에서 웹소켓 서버에 요청 시 모든 요청 수용 (CORS)
	}

	@Bean
	public WebSocketHandler signalingSocketHandler(){
		return new WebSocketHandler();
	}
}
