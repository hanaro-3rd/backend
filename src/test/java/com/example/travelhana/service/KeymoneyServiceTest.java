package com.example.travelhana.service;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.Keymoney.*;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.*;
import com.example.travelhana.Service.implement.KeymoneyServiceImpl;
import com.example.travelhana.TravelhanaApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@Transactional
@DisplayName("키머니 단위 테스트")
@SpringBootTest(classes = TravelhanaApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class KeymoneyServiceTest {

	@InjectMocks
	private KeymoneyServiceImpl keymoneyService;

	@Mock
	private KeymoneyRepository keymoneyRepository;

	@Mock
	private PaymentHistoryRepository paymentHistoryRepository;

	@Mock
	private ExchangeHistoryRepository exchangeHistoryRepository;

	@Mock
	private MarkerHistoryRepository markerHistoryRepository;

	@Test
	@DisplayName("키머니 불러오기 테스트")
	public void getKeymoneyTest() throws Exception {
		// given
		int userId = 1;
		Users users = Users
				.builder()
				.id(userId)
				.build();

		List<Keymoney> keymoneyList = new ArrayList<>();
		keymoneyList.add(new Keymoney(1, users, "JPY", 1L));
		keymoneyList.add(new Keymoney(2, users, "USD", 2L));
		keymoneyList.add(new Keymoney(3, users, "EUR", 3L));

		// stub
		given(keymoneyRepository.findByUsers_Id(userId)).willReturn(keymoneyList);

		// when
		ResponseEntity<?> responseEntity = keymoneyService.getKeymoney(users);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		List<KeymoneySimpleDto> keymoneySimpleDtos = (List<KeymoneySimpleDto>) apiResponse.getResult();

		assertEquals(keymoneySimpleDtos.get(0).getBalance(), 1L);
		assertEquals(keymoneySimpleDtos.get(1).getBalance(), 2L);
		assertEquals(keymoneySimpleDtos.get(2).getBalance(), 3L);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("키머니 내역 불러오기 테스트 (filter=all)")
	public void getKeymoneyHistoryFilterAllTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		String unit = "JPY";
		String filter = "all";

		Keymoney keymoney = Keymoney
				.builder()
				.id(1)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		List<PaymentHistory> paymentHistories = new ArrayList<>();
		PaymentHistory paymentHistory = PaymentHistory
				.builder()
				.id(1L)
				.isPayment(true)
				.createdAt(LocalDateTime.now().minusDays(2L))
				.keymoneyId(keymoney.getId())
				.build();
		paymentHistories.add(paymentHistory);

		List<ExchangeHistory> exchangeHistories = new ArrayList<>();
		ExchangeHistory exchangeHistory = ExchangeHistory
				.builder()
				.id(2L)
				.isBought(true)
				.keymoneyId(keymoney.getId())
				.build();
		exchangeHistories.add(exchangeHistory);

		List<MarkerHistory> markerHistories = new ArrayList<>();
		MarkerHistory markerHistory = MarkerHistory
				.builder()
				.id(3L)
				.pickDate(LocalDateTime.now().minusDays(1L))
				.keymoneyId(keymoney.getId())
				.build();
		markerHistories.add(markerHistory);

		// stub
		given(keymoneyRepository.findByUsers_IdAndUnit(users.getId(), unit)).willReturn(Optional.ofNullable(keymoney));
		given(paymentHistoryRepository.findAllByKeymoneyId(keymoney.getId())).willReturn(paymentHistories);
		given(exchangeHistoryRepository.findAllByKeymoneyId(keymoney.getId())).willReturn(exchangeHistories);
		given(markerHistoryRepository.findAllByKeymoneyId(keymoney.getId())).willReturn(markerHistories);

		// when
		ResponseEntity<?> responseEntity = keymoneyService.getKeymoneyHistory(users, unit, filter);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		KeymoneyHistoryDto keymoneyHistoryDto = (KeymoneyHistoryDto) apiResponse.getResult();
		assertEquals(keymoneyHistoryDto.getKeymoneyId(), 1);
		assertEquals(keymoneyHistoryDto.getTotalBalance(), 1000L);

		List<KeymoneyDto> keymoneyDtos = keymoneyHistoryDto.getKeymoneyHistory();

		assertEquals(keymoneyDtos.get(0).getHistoryId(), 2L);
		assertEquals(keymoneyDtos.get(1).getHistoryId(), 3L);
		assertEquals(keymoneyDtos.get(2).getHistoryId(), 1L);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("키머니 내역 불러오기 테스트 (filter=deposit)")
	public void getKeymoneyHistoryFilterDepositTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		String unit = "JPY";
		String filter = "deposit";

		Keymoney keymoney = Keymoney
				.builder()
				.id(1)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		List<PaymentHistory> paymentHistories = new ArrayList<>();
		PaymentHistory paymentHistory = PaymentHistory
				.builder()
				.id(1L)
				.isPayment(false)
				.createdAt(LocalDateTime.now().minusDays(2L))
				.keymoneyId(keymoney.getId())
				.build();
		paymentHistories.add(paymentHistory);

		List<ExchangeHistory> exchangeHistories = new ArrayList<>();
		ExchangeHistory exchangeHistory = ExchangeHistory
				.builder()
				.id(2L)
				.isBought(true)
				.keymoneyId(keymoney.getId())
				.build();
		exchangeHistories.add(exchangeHistory);

		List<MarkerHistory> markerHistories = new ArrayList<>();
		MarkerHistory markerHistory = MarkerHistory
				.builder()
				.id(3L)
				.pickDate(LocalDateTime.now().minusDays(1L))
				.keymoneyId(keymoney.getId())
				.build();
		markerHistories.add(markerHistory);

		// stub
		given(keymoneyRepository.findByUsers_IdAndUnit(users.getId(), unit)).willReturn(Optional.ofNullable(keymoney));
		given(paymentHistoryRepository.findAllByKeymoneyIdAndIsPayment(keymoney.getId(), false)).willReturn(paymentHistories);
		given(exchangeHistoryRepository.findAllByKeymoneyIdAndIsBought(keymoney.getId(), true)).willReturn(exchangeHistories);
		given(markerHistoryRepository.findAllByKeymoneyId(keymoney.getId())).willReturn(markerHistories);

		// when
		ResponseEntity<?> responseEntity = keymoneyService.getKeymoneyHistory(users, unit, filter);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		KeymoneyHistoryDto keymoneyHistoryDto = (KeymoneyHistoryDto) apiResponse.getResult();
		assertEquals(keymoneyHistoryDto.getKeymoneyId(), 1);
		assertEquals(keymoneyHistoryDto.getTotalBalance(), 1000L);

		List<KeymoneyDto> keymoneyDtos = keymoneyHistoryDto.getKeymoneyHistory();

		assertEquals(keymoneyDtos.get(0).getIsDeposit(), true);
		assertEquals(keymoneyDtos.get(1).getIsDeposit(), true);
		assertEquals(keymoneyDtos.get(2).getIsDeposit(), true);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("키머니 내역 불러오기 테스트 (filter=withdrawal)")
	public void getKeymoneyHistoryFilterWithdrawalTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		String unit = "JPY";
		String filter = "withdrawal";

		Keymoney keymoney = Keymoney
				.builder()
				.id(1)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		List<PaymentHistory> paymentHistories = new ArrayList<>();
		PaymentHistory paymentHistory = PaymentHistory
				.builder()
				.id(1L)
				.isPayment(true)
				.createdAt(LocalDateTime.now().minusDays(2L))
				.keymoneyId(keymoney.getId())
				.build();
		paymentHistories.add(paymentHistory);

		List<ExchangeHistory> exchangeHistories = new ArrayList<>();
		ExchangeHistory exchangeHistory = ExchangeHistory
				.builder()
				.id(2L)
				.isBought(false)
				.keymoneyId(keymoney.getId())
				.build();
		exchangeHistories.add(exchangeHistory);

		// stub
		given(keymoneyRepository.findByUsers_IdAndUnit(users.getId(), unit)).willReturn(Optional.ofNullable(keymoney));
		given(paymentHistoryRepository.findAllByKeymoneyIdAndIsPayment(keymoney.getId(), true)).willReturn(paymentHistories);
		given(exchangeHistoryRepository.findAllByKeymoneyIdAndIsBought(keymoney.getId(), false)).willReturn(exchangeHistories);

		// when
		ResponseEntity<?> responseEntity = keymoneyService.getKeymoneyHistory(users, unit, filter);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		KeymoneyHistoryDto keymoneyHistoryDto = (KeymoneyHistoryDto) apiResponse.getResult();
		assertEquals(keymoneyHistoryDto.getKeymoneyId(), 1);
		assertEquals(keymoneyHistoryDto.getTotalBalance(), 1000L);

		List<KeymoneyDto> keymoneyDtos = keymoneyHistoryDto.getKeymoneyHistory();

		assertEquals(keymoneyDtos.get(0).getIsDeposit(), false);
		assertEquals(keymoneyDtos.get(1).getIsDeposit(), false);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("키머니 상세 내역 불러오기 테스트 (type=payment)")
	public void getDetailKeymoneyPaymentHistoryTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		Long historyId = 1L;
		String type = "payment";

		PaymentHistory paymentHistory = PaymentHistory
				.builder()
				.id(1L)
				.isPayment(true)
				.keymoneyId(2)
				.createdAt(LocalDateTime.now().minusDays(2L))
				.build();

		Keymoney keymoney = Keymoney
				.builder()
				.id(2)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		// stub
		given(paymentHistoryRepository.findByIdAndUserId(historyId, users.getId())).willReturn(Optional.ofNullable(paymentHistory));
		given(keymoneyRepository.findById(paymentHistory.getKeymoneyId())).willReturn(Optional.ofNullable(keymoney));

		// when
		ResponseEntity<?> responseEntity = keymoneyService.getDetailKeymoneyHistory(users, historyId, type);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		KeymoneyPaymentDto keymoneyPaymentDto = (KeymoneyPaymentDto) apiResponse.getResult();
		assertEquals(keymoneyPaymentDto.getHistoryId(), 1L);
		assertEquals(keymoneyPaymentDto.getIsDeposit(), false);
		assertEquals(keymoneyPaymentDto.getType(), "payment");

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("키머니 상세 내역 불러오기 테스트 (type=exchange)")
	public void getDetailKeymoneyExchangeHistoryTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		Long historyId = 2L;
		String type = "exchange";

		ExchangeHistory exchangeHistory = ExchangeHistory
				.builder()
				.id(2L)
				.isBought(true)
				.keymoneyId(2)
				.build();

		Keymoney keymoney = Keymoney
				.builder()
				.id(2)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		// stub
		given(exchangeHistoryRepository.findByIdAndUserId(historyId, users.getId())).willReturn(Optional.ofNullable(exchangeHistory));
		given(keymoneyRepository.findById(exchangeHistory.getKeymoneyId())).willReturn(Optional.ofNullable(keymoney));

		// when
		ResponseEntity<?> responseEntity = keymoneyService.getDetailKeymoneyHistory(users, historyId, type);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		KeymoneyExchangeDto keymoneyExchangeDto = (KeymoneyExchangeDto) apiResponse.getResult();
		assertEquals(keymoneyExchangeDto.getHistoryId(), 2L);
		assertEquals(keymoneyExchangeDto.getIsDeposit(), true);
		assertEquals(keymoneyExchangeDto.getType(), "exchange");

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("키머니 상세 내역 불러오기 테스트 (type=marker)")
	public void getDetailKeymoneyMarkerHistoryTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.phoneNum("01012345678")
				.build();

		Long historyId = 3L;
		String type = "marker";

		MarkerHistory markerHistory = MarkerHistory
				.builder()
				.id(3L)
				.pickDate(LocalDateTime.now().minusDays(1L))
				.keymoneyId(2)
				.build();

		Keymoney keymoney = Keymoney
				.builder()
				.id(2)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		// stub
		given(markerHistoryRepository.findByIdAndUserId(historyId, users.getId())).willReturn(Optional.ofNullable(markerHistory));
		given(keymoneyRepository.findById(markerHistory.getKeymoneyId())).willReturn(Optional.ofNullable(keymoney));

		// when
		ResponseEntity<?> responseEntity = keymoneyService.getDetailKeymoneyHistory(users, historyId, type);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		KeymoneyMarkerDto keymoneyMarkerDto = (KeymoneyMarkerDto) apiResponse.getResult();
		assertEquals(keymoneyMarkerDto.getHistoryId(), 3L);
		assertEquals(keymoneyMarkerDto.getType(), "marker");

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

}
