package com.example.travelhana.Service.implement;


import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.Authentication.*;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Service.PhoneAuthService;
import com.example.travelhana.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PhoneAuthServiceImpl implements PhoneAuthService {
	@Value("${SMS_ACCESS_KEY}")
	private String accesskey;
	@Value("${SMS_SECRETE_KEY}")
	private String secretkey;
	@Value("${SMS_SERVICE_ID}")
	private String serviceid;
	@Value("${SMS_FROM_NUMBER}")
	private String fromNum;

	private final HttpSession session;
	private final UserService userService;

	public static int generateRandomNumber() {
		return ThreadLocalRandom.current().nextInt(100000, 1000000);
	}

	private String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
		String space = " ";
		String newLine = "\n";
		String method = "POST";
		String url = "/sms/v2/services/" + serviceid + "/messages";
		String timestamp = time.toString();
		String accessKey = accesskey;
		String secretKey = secretkey;

		String message = new StringBuilder()
				.append(method)
				.append(space)
				.append(url)
				.append(newLine)
				.append(timestamp)
				.append(newLine)
				.append(accessKey)
				.toString();

		SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(signingKey);

		byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
		String encodeBase64String = Base64.encodeBase64String(rawHmac);

		return encodeBase64String;
	}

	public ResponseEntity<?> sendMessageWithResttemplate(String phoneNum)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, JsonProcessingException, URISyntaxException {

		Long time = System.currentTimeMillis();

		String code = String.valueOf(generateRandomNumber());

		setCodeIntoSession(code);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-ncp-apigw-timestamp", time.toString());
		headers.set("x-ncp-iam-access-key", accesskey);
		headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

		MessageDto msg = new MessageDto(phoneNum, "휴대폰 인증 코드는 " + code + "입니다.");
		List<MessageDto> messages = new ArrayList<>();
		messages.add(msg);
		SMSRequestDto request = SMSRequestDto.builder()
				.type("SMS")
				.from(fromNum)
				.content(msg.getContent())
				.messages(messages)
				.build();

		ObjectMapper objectMapper = new ObjectMapper();
		String body = objectMapper.writeValueAsString(request);
		HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		try {
			SMSResponseDto response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + serviceid + "/messages"), httpBody, SMSResponseDto.class);

			ApiResponse apiResponse = ApiResponse.builder()
					.result(response)
					.resultCode(SuccessCode.OPEN_API_SUCCESS.getStatusCode())
					.resultMsg(SuccessCode.OPEN_API_SUCCESS.getMessage())
					.build();
			return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			HttpStatus statusCode = e.getStatusCode();
			String statusText = e.getStatusText();

			ApiResponse errorResponse = ApiResponse.builder()
					.result("Fail")
					.resultCode(statusCode.value())
					.resultMsg(statusText)
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}


	private void setCodeIntoSession(String code) {
		session.setAttribute("code", code);
		session.setMaxInactiveInterval(180); //코드 유효기간 3뷴
	}

	public ResponseEntity<?> checkCode(CodeRequestDto codeDto) {
		String code = (String) session.getAttribute("code");
		if (code != null) {
			CodeResponseDto codeResponseDto;

			if (codeDto.getCode().equals(code)) { //코드가 일치하면
				Optional<User> user = userService.validateDuplicateUsername(codeDto.getPhonenum());
				if (user == null) //유저가 존재하지 않으면
				{
					codeResponseDto = CodeResponseDto.builder()
							.isCodeEqual(true)
							.isExistUser(true)
							.build();
				} else { //유저가 존재하면
					codeResponseDto = CodeResponseDto.builder()
							.isCodeEqual(true)
							.isExistUser(false)
							.userResponseDto(UserResponseDto.builder()
									.name(user.get().getName())
									.phoneNum(user.get().getPhoneNum())
									.registrationNum(user.get().getRegistrationNum())
									.createdAt(user.get().getCreatedAt())
									.build())
							.build();
				}
				ApiResponse apiResponse = ApiResponse.builder()
						.result(codeResponseDto)
						.resultCode(SuccessCode.AUTH_SUCCESS.getStatusCode())
						.resultMsg(SuccessCode.AUTH_SUCCESS.getMessage())
						.build();
				session.removeAttribute("code");

				return ResponseEntity.ok(apiResponse);
			} else { //코드가 일치하지 않는다면
				ErrorResponse apiResponse = ErrorResponse.builder()
						.errorCode(ErrorCode.AUTH_FAILURE.getStatusCode())
						.errorMessage(ErrorCode.AUTH_FAILURE.getMessage())
						.build();
				return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

			}
		} else {
			session.removeAttribute("code");
			ErrorResponse errorResponse = ErrorResponse.builder()
					.errorMessage(ErrorCode.SESSION_INVALID.getMessage())
					.errorCode(ErrorCode.SESSION_INVALID.getStatusCode())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}