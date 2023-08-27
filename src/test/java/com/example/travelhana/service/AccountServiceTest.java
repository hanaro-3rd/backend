package com.example.travelhana.service;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Projection.AccountInfoProjection;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Service.implement.AccountServiceImpl;
import com.example.travelhana.TravelhanaApplication;
import com.example.travelhana.Util.CryptoUtil;
import com.example.travelhana.Util.HolidayUtil;
import com.example.travelhana.Util.SaltUtil;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TravelhanaApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class AccountServiceTest {

	@InjectMocks
	private AccountServiceImpl accountService;

	@Mock
	private SaltUtil saltUtil;

	@Mock
	private CryptoUtil cryptoUtil;

	@Mock
	private HolidayUtil holidayUtil;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private ExternalAccountRepository externalAccountRepository;

	@Mock
	private UserService userService;
	@Autowired
	private UserRepository userRepository;

	@Test
	public void getConnectedAccountListTest() throws Exception {
		// given
		int userId = 1;
		Users users = userRepository.getReferenceById(userId);

		// when
		accountService.getConnectedAccountList(users);

		// then
		verify(accountRepository).findAllByUsers_Id(userId);
		verify(holidayUtil).isBusinessDay(LocalDate.now());
	}

	@Test
	public void findExternalAccountListTest() throws Exception {
		// given
		int userId = 1;
		Users users = userRepository.getReferenceById(userId);

		// when
		ResponseEntity<?> responseEntity = accountService.findExternalAccountList(users);

		// then
		verify(externalAccountRepository).findAllByPhoneNumAndIsConnected(users.getPhoneNum(), false);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);
		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

//	@Test
//	public void connectExternalAccountTest() throws Exception {
//		// given
//		Users users = new Users();
//		users.setId(1);
//		users.setRegistrationNum("1234567890123");
//
//		ExternalAccount externalAccount = new ExternalAccount();
//		externalAccount.setId(2);
//		externalAccount.setRegistrationNum("1234567890123");
//		externalAccount.setAccountNum("12345678901234567");
//		externalAccount.setSalt("salt");
//		externalAccount.setPassword("password");
//		externalAccount.setBank("Bank");
//		externalAccount.setOpenDate(LocalDate.now());
//		externalAccount.setBalance(10000);
//
//		AccountPasswordDto accountPasswordDto = new AccountPasswordDto();
//		accountPasswordDto.setAccountPassword("password");
//
//		when(userService.getUserByAccessToken("accessToken")).thenReturn(users);
//		when(externalAccountRepository.findByIdAndIsConnected(2, false))
//				.thenReturn(Optional.of(externalAccount));
//		when(saltUtil.encodePassword("salt", "password")).thenReturn("password");
//		when(accountRepository.existsAccountByAccountNum("12345678901234567")).thenReturn(false);
//		when(cryptoUtil.decrypt("12345678901234567")).thenReturn("12345678901234567");
//
//		// when
//		ResponseEntity<?> responseEntity = accountService.connectExternalAccount("accessToken", 2, accountPasswordDto);
//
//		// then
//		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//	}
//
//	@Test
//	public void connectExternalAccountUnauthorizedUserAccountTest() {
//		// given
//		Users users = new Users();
//		users.setRegistrationNum("1234567890123");
//
//		ExternalAccount externalAccount = new ExternalAccount();
//		externalAccount.setRegistrationNum("9876543210987");
//
//		AccountPasswordDto accountPasswordDto = new AccountPasswordDto();
//		accountPasswordDto.setAccountPassword("password");
//
//		when(userService.getUserByAccessToken("accessToken")).thenReturn(users);
//		when(externalAccountRepository.findByIdAndIsConnected(2, false))
//				.thenReturn(Optional.of(externalAccount));
//
//		// when
//		Exception exception = assertThrows(BusinessExceptionHandler.class, () ->
//				accountService.connectExternalAccount("accessToken", 2, accountPasswordDto));
//
//		// then
//		assertEquals(ErrorCode.UNAUTHORIZED_USER_ACCOUNT, ((BusinessExceptionHandler) exception).getErrorCode());
//	}

}
