package com.example.travelhana.Config;

import com.example.travelhana.Exception.Response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.reflections.Reflections.log;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		log.info("CustomFailureHandler");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");
		ErrorResponse errorResponse = new ErrorResponse(403, "ID 또는 비밀번호가 일치하지 않습니다.");
		new ObjectMapper().writeValue(response.getWriter(), errorResponse);
	}
}
