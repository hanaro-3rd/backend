package com.example.travelhana.Config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static com.example.travelhana.Config.JwtConstants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@RequiredArgsConstructor
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	private final UserService userService;
	private final JwtConstants jwtConstants;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		UserDetails user = (UserDetails) authentication.getPrincipal();
		log.info("CustomSuccessHandler");

		String accessToken = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + AT_EXP_TIME))
				.withClaim("roles",
						user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.sign(Algorithm.HMAC256("7ZWt7ZW0OTntmZTsnbTtjIXtlZzqta3snYTrhIjrqLjshLjqs4TroZzrgpjslYTqsIDsnpDtm4zrpa3tlZzqsJzrsJzsnpDrpbzrp4zrk6TslrTqsIDsnpA="));
		String refreshToken = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + RT_EXP_TIME))
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.sign(Algorithm.HMAC256("7ZWt7ZW0OTntmZTsnbTtjIXtlZzqta3snYTrhIjrqLjshLjqs4TroZzrgpjslYTqsIDsnpDtm4zrpa3tlZzqsJzrsJzsnpDrpbzrp4zrk6TslrTqsIDsnpA="));

		// Refresh Token DB에 저장
		userService.updateRefreshToken(user.getUsername(), refreshToken);

		// Access Token , Refresh Token 프론트 단에 Response Header로 전달
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");
		response.setHeader(AT_HEADER, accessToken);
		response.setHeader(RT_HEADER, refreshToken);

		ApiResponse apiResponse = ApiResponse.builder()
				.result("Success")
				.resultCode(SuccessCode.AUTH_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.AUTH_SUCCESS.getMessage())
				.build();
		new ObjectMapper().writeValue(response.getWriter(), apiResponse);

	}
}