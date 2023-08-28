package com.example.travelhana.service;

import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Authentication.DeviceDto;
import com.example.travelhana.Dto.Authentication.SignupRequestDto;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Service.implement.UserServiceImpl;
import com.example.travelhana.TravelhanaApplication;
import com.example.travelhana.Util.CryptoUtil;
import com.example.travelhana.Util.SaltUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.any;

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

	private static String name;
	private static String password;
	private static String pattern;
	private static String phonenum;
	private static String deviceId;
	private static String registrationNum;
	private static Users users;

	@BeforeEach
	public void setUp(){
		// given
		name = "테스트";
		password = "123456";
		pattern = "1234";
		phonenum = "01012345678";
		deviceId = "test-device-1";
		registrationNum = "0001013";

		users = Users.builder()
				.name(name)
				.password(password)
				.phoneNum(phonenum)
				.deviceId(deviceId)
				.salt("salt")
				.registrationNum(registrationNum)
				.build();
	}

	@Test
	public void signUpTest() throws Exception {

		SignupRequestDto signupRequestDto = new SignupRequestDto(name, password, pattern, phonenum, deviceId, registrationNum);

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
	public void isExistDeviceTest(){

		//given
		given(userRepository.save(any(Users.class))).willReturn(users);

		//when
//		ResponseEntity<?> responseEntity = userService.isExistDevice(deviceId);
//
//
//		//then
//		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());





	}

}