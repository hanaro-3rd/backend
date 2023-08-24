package com.example.travelhana.Socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry){
		registry.addEndpoint("/ws")
				.setAllowedOrigins("*"); //클라이언트에서 웹소켓 서버에 요청 시 모든 요청 수용 (CORS)
	}

	//클라이언트는 구독 경로 '/sub/channel/{채널아이디}'
	//서버가 발행할때는 "/pub/alarm"로 메세지 보냄. 메세지 보낼떄는 채널아이디 포함
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry){
		registry.enableSimpleBroker("/sub");
		registry.setApplicationDestinationPrefixes("/pub");
	}

}
