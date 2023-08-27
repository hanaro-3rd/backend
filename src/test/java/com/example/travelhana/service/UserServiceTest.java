package com.example.travelhana.service;

import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Authentication.SignupRequestDto;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Service.implement.AccountServiceImpl;
import com.example.travelhana.Service.implement.UserServiceImpl;
import com.example.travelhana.TravelhanaApplication;
import com.example.travelhana.Util.CryptoUtil;
import com.example.travelhana.Util.SaltUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = TravelhanaApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private SaltUtil saltUtil;

	@Mock
	private CryptoUtil cryptoUtil;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ExternalAccountRepository externalAccountRepository;

	@Test
	public void signUpTest() throws Exception {
		// given
		String name = "테스트";
		String password = "123456";
		String phonenum = "01012345678";
		String deviceId = "test-device-1";
		String registrationNum = "0001013";

		Users users = Users.builder()
				.name(name)
				.password(password)
				.phoneNum(phonenum)
				.deviceId(deviceId)
				.salt("salt")
				.registrationNum(registrationNum)
				.build();
		SignupRequestDto signupRequestDto = new SignupRequestDto(name, password, phonenum, deviceId, registrationNum);

		ExternalAccount externalAccount = new ExternalAccount();

		// stub
		given(saltUtil.encodePassword("salt", password)).willReturn("salted"+password);
		given(cryptoUtil.encrypt("text")).willReturn("encryptedText");
		given(userRepository.save(any(Users.class))).willReturn(users);
		given(externalAccountRepository.save(externalAccount)).willReturn(externalAccount);

		// when
		ResponseEntity<?> responseEntity = userService.saveAccount(signupRequestDto);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);
		assertEquals(apiResponse.getResult(), name);
		assertEquals(SuccessCode.INSERT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.INSERT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	public void duplicateSignUpTest() throws Exception {
		// given
		String name = "테스트";
		String password = "123456";
		String phonenum = "01012345678";
		String deviceId = "test-device-1";
		String registrationNum = "0001013";

		Users users = Users.builder()
				.name(name)
				.password(password)
				.phoneNum(phonenum)
				.deviceId(deviceId)
				.salt("salt")
				.registrationNum(registrationNum)
				.build();
		SignupRequestDto signupRequestDto = new SignupRequestDto(name, password, phonenum, deviceId, registrationNum);

		ExternalAccount externalAccount = new ExternalAccount();

		// stub
		given(saltUtil.encodePassword("salt", password)).willReturn("salted"+password);
		given(cryptoUtil.encrypt("text")).willReturn("encryptedText");
		given(userRepository.save(any(Users.class))).willReturn(users);
		given(externalAccountRepository.save(externalAccount)).willReturn(externalAccount);
		given(userService.validateDuplicateUsername(phonenum)).willReturn(Optional.empty()).willReturn(Optional.of(users));

		// when
		ResponseEntity<?> responseEntity1 = userService.saveAccount(signupRequestDto);
		ResponseEntity<?> responseEntity2 = userService.saveAccount(signupRequestDto);

		// then
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity2.getStatusCode());

		ErrorResponse errorResponse = (ErrorResponse) responseEntity2.getBody();
		assertNotNull(errorResponse);
		assertEquals(ErrorCode.USER_ALREADY_EXIST.getStatusCode(), errorResponse.getErrorCode());
		assertEquals(ErrorCode.USER_ALREADY_EXIST.getMessage(), errorResponse.getErrorMessage());
	}

}
