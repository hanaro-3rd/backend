package com.example.travelhana.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.travelhana.Config.JwtConstants;
import com.example.travelhana.Dto.Authentication.*;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Service.PhoneAuthService;
import com.example.travelhana.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static com.example.travelhana.Config.JwtConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final PhoneAuthService phoneAuthService;
	private final JwtConstants jwtConstants;

	@GetMapping("/test")
	public String isExistDevice() {
		return "hello test success!test";
	}

	//기기 존재 여부 확인
	@GetMapping("/registration/{deviceId}")
	public ResponseEntity<?> isExistDevice(@PathVariable("deviceId") String deviceId) {
		return userService.isExistDevice(deviceId);
	}

	//로그인 테스트용
	@PostMapping("/signin/password")
	public void signin(@RequestBody LoginRequestDto ignoredDto) {
	}

	//휴대폰 인증코드 전송
	@PostMapping("/verification")
	public ResponseEntity<?> sendMessagewithRest(@RequestBody PhonenumDto dto)
			throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, UnsupportedEncodingException, URISyntaxException {
		return phoneAuthService.sendMessageWithResttemplate(dto.getPhonenum());
	}

	//휴대폰 인증코드 일치여부 확인
	@PostMapping("/verification/auth")
	public ResponseEntity<?> isSuccessAuth(@RequestHeader("Cookie") String ignoredHeaderValue, @RequestBody CodeRequestDto codedto) {
		return phoneAuthService.checkCode(codedto);
	}

	@GetMapping("/user")
	public String getUser(@RequestHeader("Authorization") String headerValue) {
		// Access Token만 꺼내옴
		String accessToken = headerValue.substring(TOKEN_HEADER_PREFIX.length());

		//Access Token 검증
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtConstants.JWT_SECRET)).build();
		DecodedJWT decodedJWT = verifier.verify(accessToken);

		//Access Token 내 Claim에서 Authorities 꺼내 Authentication 객체 생성 & SecurityContext에 저장
		List<String> strAuthorities = decodedJWT.getClaim("roles").asList(String.class);
		String username = decodedJWT.getSubject();
		return username;
	}

	//회원가입
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody SignupRequestDto dto) {
		return userService.saveAccount(dto);
	}

	@PostMapping("/userrole")
	public ResponseEntity<Integer> addRoleToUser(@RequestBody RoleToUserRequestDto dto) {
		return ResponseEntity.ok(userService.addRoleToUser(dto));
	}

	//refresh token 요청
	@GetMapping("/refresh")
	public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request,
	                                                   HttpServletResponse response) {
		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
			throw new RuntimeException("JWT Token이 존재하지 않습니다.");
		}
		String refreshToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
		Map<String, String> tokens = userService.refresh(refreshToken);
		response.setHeader(AT_HEADER, tokens.get(AT_HEADER));
		if (tokens.get(RT_HEADER) != null) {
			response.setHeader(RT_HEADER, tokens.get(RT_HEADER));
		}
		ApiResponse apiResponse = ApiResponse.builder()
				.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
				.resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
				.build();
		return ResponseEntity.ok(tokens);
	}

}
