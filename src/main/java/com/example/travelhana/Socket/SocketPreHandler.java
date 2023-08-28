package com.example.travelhana.Socket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.travelhana.Config.JwtConstants;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.LoggerFactory;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.example.travelhana.Config.JwtConstants.TOKEN_HEADER_PREFIX;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Component
@RequiredArgsConstructor
public class SocketPreHandler implements ChannelInterceptor {

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		System.out.println("preSend 검사 시작");
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

		String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader("Authorization"));
		System.out.println(authorizationHeader);

		System.out.println(headerAccessor.getCommand());
		if (StompCommand.CONNECT == headerAccessor.getCommand() || StompCommand.SUBSCRIBE == headerAccessor.getCommand()) {
			if (authorizationHeader == null || authorizationHeader.isEmpty()) {
				// 토큰값이 없거나 정상적이지 않다면 400 오류
				throw new MessageDeliveryException("토큰값 없음");
			}

			if (!authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
				// 토큰값이 유효하지 않다면 400 오류
				throw new MessageDeliveryException("토큰이 유효하지 않음");
			}

				// Access Token만 꺼내옴
				String accessToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());

				//Access Token 검증
				JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JwtConstants.JWT_SECRET))
						.build();
				DecodedJWT decodedJWT = verifier.verify(accessToken);

				//Access Token 내 Claim에서 Authorities 꺼내 Authentication 객체 생성 & SecurityContext에 저장
				List<String> strAuthorities = decodedJWT.getClaim("roles").asList(String.class);
				List<SimpleGrantedAuthority> authorities = strAuthorities.stream()
						.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
				String username = decodedJWT.getSubject();
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						username, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);


		}
		return message;
	}

//
//
//	@Override
//	public void postSend(Message message, MessageChannel channel, boolean sent) {
//		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//		String sessionId = accessor.getSessionId();
//
//		switch ((accessor.getCommand())) {
//			case CONNECT:
//				// 유저가 connect()를 한 뒤 호출됨
//				System.out.println("세션 들어옴 " + sessionId);
//				break;
//
//			case DISCONNECT:
//
//				// 유저가 disconnect() 를 한 뒤 호출됨 or 세션이 끊어졌을 때 발생
//				System.out.println("세션 끊음 "+ sessionId);
//				break;
//
//			default:
//
//				break;
//		}
//
//	}

}
