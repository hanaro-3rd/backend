package com.example.travelhana.Controller;

import com.example.travelhana.Dto.Authentication.*;
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

import static com.example.travelhana.Config.JwtConstants.TOKEN_HEADER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final PhoneAuthService phoneAuthService;

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
	public ResponseEntity<?> isSuccessAuth(@RequestBody CodeRequestDto codedto) {
		return phoneAuthService.checkCode(codedto);
	}

	//회원가입
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody SignupRequestDto dto) throws Exception {
		return userService.saveAccount(dto);
	}

	//refresh token 요청
	@GetMapping("/refresh")
	public ResponseEntity<?> refresh(
			HttpServletRequest request, HttpServletResponse response) {
		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
			throw new RuntimeException("JWT Token이 존재하지 않습니다.");
		}

		String refreshToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
		return userService.refresh(refreshToken);
	}

	//비밀번호 수정
	@PatchMapping("/updatePassword")
	public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDto dto) {
		return userService.updatePassword(dto);
	}

	//기기 변경 시 업데이트
	@PatchMapping("/updateDevice")
	public ResponseEntity<?> updateDevice(@RequestBody UpdateDeviceRequestDto dto) {
		return userService.updateDevice(dto);
	}


}
