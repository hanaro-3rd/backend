package com.example.travelhana.Socket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.travelhana.Config.JwtConstants;
import com.example.travelhana.Domain.Users;
import com.example.travelhana.Service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.travelhana.Config.JwtConstants.TOKEN_HEADER_PREFIX;


@Component
@RequiredArgsConstructor
public class SocketPreHandler implements ChannelInterceptor {
	private final JwtConstants jwtConstants;
	private final UserService userService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		System.out.println("preSend 검사 시작");
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

		String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader("Authorization"));
		System.out.println(authorizationHeader);

		System.out.println(headerAccessor.getCommand());

		if (StompCommand.CONNECT == headerAccessor.getCommand()) {
			if (authorizationHeader == null || authorizationHeader.isEmpty()) {
				// 토큰값이 없거나 정상적이지 않다면 400 오류
				throw new MessageDeliveryException("토큰값 없음");
			}

			if (!authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
				// 토큰값이 유효하지 않다면 400 오류
				throw new MessageDeliveryException("토큰이 유효하지 않음");
			}

			try {
				// Access Token만 꺼내옴
				Users user = userService.getUserByAccessToken(authorizationHeader);

			}catch (MessageDeliveryException e){
				throw new MessageDeliveryException("메세지 에러");
			}catch (MalformedJwtException e){
				throw new MessageDeliveryException("예외3");
			}

		}
		return message;


	}


}