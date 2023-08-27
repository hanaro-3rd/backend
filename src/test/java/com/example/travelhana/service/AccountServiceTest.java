package com.example.travelhana.service;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Account.AccountInformation;
import com.example.travelhana.Dto.Account.AccountListDto;
import com.example.travelhana.Dto.Account.AccountPasswordDto;
import com.example.travelhana.Dto.Account.ConnectedAccountListDto;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Projection.AccountInfoProjection;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Service.implement.AccountServiceImpl;
import com.example.travelhana.TravelhanaApplication;
import com.example.travelhana.Util.CryptoUtil;
import com.example.travelhana.Util.HolidayUtil;
import com.example.travelhana.Util.SaltUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Transactional
@DisplayName("계좌 단위 테스트")
@SpringBootTest(classes = TravelhanaApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
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

	@Test
	@DisplayName("연결된 계좌 목록 불러오기 테스트")
	public void getConnectedAccountListTest() throws Exception {
		// given
		int userId = 1;
		Users users = Users
				.builder()
				.id(userId)
				.build();

		List<AccountInfoProjection> connectedAccounts = new ArrayList<>();

		// mock AccountInfoProjection
		AccountInfoProjection mockProjection = mock(AccountInfoProjection.class);
		given(mockProjection.getId()).willReturn(1);
		given(mockProjection.getAccountNum()).willReturn("encrypted test accountNum");
		given(mockProjection.getBank()).willReturn("test bank");
		given(mockProjection.getBalance()).willReturn(1000L);
		connectedAccounts.add(mockProjection);

		// stub
		given(accountRepository.findAllByUsers_Id(userId)).willReturn(connectedAccounts);
		given(holidayUtil.isBusinessDay(LocalDate.now())).willReturn(true);
		given(cryptoUtil.decrypt("encrypted test accountNum")).willReturn("test accountNum");

		// when
		ResponseEntity<?> responseEntity = accountService.getConnectedAccountList(users);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		ConnectedAccountListDto connectedAccountListDto = (ConnectedAccountListDto) apiResponse.getResult();
		AccountInformation accountInformation = connectedAccountListDto.getAccounts().get(0);

		assertEquals(accountInformation.getAccountId(), 1);
		assertEquals(accountInformation.getAccountNum(), "test accountNum");
		assertEquals(accountInformation.getBank(), "test bank");
		assertEquals(accountInformation.getBalance(), 1000L);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("외부 계좌 목록 불러오기 테스트")
	public void findExternalAccountListTest() throws Exception {
		// given
		int userId = 1;
		Users users = Users
				.builder()
				.id(userId)
				.phoneNum("01012345678")
				.build();

		List<AccountInfoProjection> externalAccounts = new ArrayList<>();

		// mock AccountInfoProjection
		AccountInfoProjection mockProjection = mock(AccountInfoProjection.class);
		given(mockProjection.getId()).willReturn(1);
		given(mockProjection.getAccountNum()).willReturn("encrypted test accountNum");
		given(mockProjection.getBank()).willReturn("test bank");
		given(mockProjection.getBalance()).willReturn(1000L);
		externalAccounts.add(mockProjection);

		// stub
		given(externalAccountRepository.findAllByPhoneNumAndIsConnected(users.getPhoneNum(), false)).willReturn(externalAccounts);
		given(cryptoUtil.decrypt("encrypted test accountNum")).willReturn("test accountNum");

		// when
		ResponseEntity<?> responseEntity = accountService.findExternalAccountList(users);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		AccountListDto accountListDto = (AccountListDto) apiResponse.getResult();
		AccountInformation accountInformation = accountListDto.getExternalAccounts().get(0);

		assertEquals(accountInformation.getAccountId(), 1);
		assertEquals(accountInformation.getAccountNum(), "test accountNum");
		assertEquals(accountInformation.getBank(), "test bank");
		assertEquals(accountInformation.getBalance(), 1000L);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("외부 계좌 연결하기 테스트")
	public void connectExternalAccountTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.phoneNum("01012345678")
				.build();

		ExternalAccount externalAccount = ExternalAccount
				.builder()
				.id(2)
				.isConnected(false)
				.accountNum("encrypted accountNum")
				.password("salted password")
				.phoneNum("01012345678")
				.bank("test bank")
				.balance(1000L)
				.salt("salt")
				.build();

		Account account = Account
				.builder()
				.build();

		// stub
		given(externalAccountRepository.findByIdAndIsConnected(1, false)).willReturn(Optional.ofNullable(externalAccount));
		given(saltUtil.encodePassword("salt", "test password")).willReturn("salted password");
		given(accountRepository.existsAccountByAccountNum("test accountNum")).willReturn(false);
		given(accountRepository.save(account)).willReturn(account);
		given(cryptoUtil.decrypt("encrypted password")).willReturn("test password");
		given(cryptoUtil.decrypt("encrypted accountNum")).willReturn("test accountNum");

		// when
		ResponseEntity<?> responseEntity = accountService.connectExternalAccount(users, 1, new AccountPasswordDto("test password"));

		// then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		AccountInformation accountInformation = (AccountInformation) apiResponse.getResult();

		assertEquals(accountInformation.getUserId(), 1);
		assertEquals(accountInformation.getAccountNum(), "test accountNum");
		assertEquals(accountInformation.getBank(), "test bank");
		assertEquals(accountInformation.getBalance(), 1000L);

		assertEquals(SuccessCode.INSERT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.INSERT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

}
