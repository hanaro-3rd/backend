package com.example.travelhana.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Log4j2
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
	                                            HttpServletResponse response) {
		if (request.getContentType() == null || !request.getContentType()
				.contains(MediaType.APPLICATION_JSON_VALUE)) {
			log.info("CustomAuthenticationFilter");
			return super.attemptAuthentication(request, response);
		}
		String servletPath = request.getServletPath();
		if (servletPath.contains("pattern")) {
			try {
				log.info("CustomAuthenticationFilter - pattern");
				// Request를 JSON으로 변환
				JsonAuthRequestPattern authRequest = objectMapper.readValue(request.getReader(),
						JsonAuthRequestPattern.class);
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
						authRequest.getDeviceId(),
						authRequest.getPattern()
				);
				token.setDetails("pattern");

				return authenticationManager.authenticate(token);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		} else {
			try {
				log.info("CustomAuthenticationFilter - password");
				// Request를 JSON으로 변환
				JsonAuthRequest authRequest = objectMapper.readValue(request.getReader(),
						JsonAuthRequest.class);
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
						authRequest.getDeviceId(),
						authRequest.getPassword()
				);
				token.setDetails("password");

				return authenticationManager.authenticate(token);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Getter
	@Setter
	private static class JsonAuthRequest {

		private String deviceId;
		private String password;

	}

	@Getter
	@Setter
	private static class JsonAuthRequestPattern {

		private String deviceId;
		private String pattern;

	}
}

