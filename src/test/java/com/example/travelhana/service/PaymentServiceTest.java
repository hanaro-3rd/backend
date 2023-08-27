package com.example.travelhana.service;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.Payment.PaymentHistoryDto;
import com.example.travelhana.Dto.Payment.PaymentMemoDto;
import com.example.travelhana.Dto.Payment.PaymentRequestDto;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.*;
import com.example.travelhana.Service.implement.PaymentServiceImpl;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.any;

@Transactional
@DisplayName("결제 단위 테스트")
@SpringBootTest(classes = TravelhanaApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class PaymentServiceTest {

	@InjectMocks
	private PaymentServiceImpl paymentService;

	@Mock
	private KeymoneyRepository keymoneyRepository;

	@Mock
	private PaymentHistoryRepository paymentHistoryRepository;

	@Test
	@DisplayName("결제 테스트")
	public void paymentTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		PaymentRequestDto paymentRequestDto = PaymentRequestDto
				.builder()
				.price(600L)
				.store("test store")
				.address("test address")
				.unit("JPY")
				.build();

		Keymoney keymoney = Keymoney
				.builder()
				.id(1)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		PaymentHistory paymentHistory = PaymentHistory
				.builder()
				.id(1L)
				.isPayment(true)
				.price(paymentRequestDto.getPrice())
				.balance(keymoney.getBalance()-paymentRequestDto.getPrice())
				.createdAt(LocalDateTime.now())
				.address(paymentRequestDto.getAddress())
				.store(paymentRequestDto.getStore())
				.keymoneyId(keymoney.getId())
				.build();

		// stub
		given(keymoneyRepository.findByUsers_IdAndUnit(users.getId(), paymentRequestDto.getUnit())).willReturn(Optional.ofNullable(keymoney));
		given(paymentHistoryRepository.save(any(PaymentHistory.class))).willReturn(paymentHistory);

		// when
		ResponseEntity<?> responseEntity = paymentService.payment(users, paymentRequestDto);

		// then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		PaymentHistoryDto paymentHistoryDto = (PaymentHistoryDto) apiResponse.getResult();

		assertEquals(paymentHistoryDto.getIsPayment(), true);
		assertEquals(paymentHistoryDto.getKeymoneyId(), 1);
		assertEquals(paymentHistoryDto.getBalance(), 400L);
		assertEquals(paymentHistoryDto.getStore(), "test store");
		assertEquals(paymentHistoryDto.getAddress(), "test address");

		assertEquals(SuccessCode.INSERT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.INSERT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("결제 정보 수정 테스트")
	public void updatePaymentHistoryTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		Long paymentId = 1L;

		PaymentMemoDto paymentMemoDto = PaymentMemoDto
				.builder()
				.category("updated category")
				.memo("updated memo")
				.build();

		PaymentHistory paymentHistory = PaymentHistory
				.builder()
				.id(1L)
				.memo("test memo")
				.category("test category")
				.build();

		// stub
		given(paymentHistoryRepository.findByIdAndUserId(paymentId, users.getId())).willReturn(Optional.ofNullable(paymentHistory));

		// when
		ResponseEntity<?> responseEntity = paymentService.updatePaymentHistory(users, paymentId, paymentMemoDto);

		// then
		assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		PaymentHistoryDto paymentHistoryDto = (PaymentHistoryDto) apiResponse.getResult();

		assertEquals(paymentHistoryDto.getMemo(), "updated memo");
		assertEquals(paymentHistoryDto.getCategory(), "updated category");

		assertEquals(SuccessCode.UPDATE_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.UPDATE_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("결제 취소 테스트")
	public void cancelPaymentTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		Long paymentId = 1L;

		Keymoney keymoney = Keymoney
				.builder()
				.id(1)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		PaymentHistory paymentHistory = PaymentHistory
				.builder()
				.id(1L)
				.memo("test memo")
				.category("test category")
				.isPayment(true)
				.price(300L)
				.keymoneyId(keymoney.getId())
				.build();

		PaymentHistory paymentCancelHistory = PaymentHistory
				.builder()
				.id(2L)
				.memo("test memo")
				.category("test category")
				.isPayment(false)
				.price(paymentHistory.getPrice())
				.balance(keymoney.getBalance()+paymentHistory.getPrice())
				.keymoneyId(keymoney.getId())
				.build();

		// stub
		given(paymentHistoryRepository.findByIdAndUserId(paymentId, users.getId())).willReturn(Optional.ofNullable(paymentHistory));
		given(keymoneyRepository.findById(paymentHistory.getKeymoneyId())).willReturn(Optional.of(keymoney));
		given(paymentHistoryRepository.save(any(PaymentHistory.class))).willReturn(paymentCancelHistory);

		// when
		ResponseEntity<?> responseEntity = paymentService.cancelPayment(users, paymentId);

		// then
		assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		Map<String, Object> responseData = (Map<String, Object>) apiResponse.getResult();
		assertEquals(responseData.get("totalbalance"), 1300L);

		PaymentHistoryDto paymentHistoryDto = (PaymentHistoryDto) responseData.get("paymentHistory");
		assertEquals(paymentHistoryDto.getIsPayment(), false);

		assertEquals(SuccessCode.UPDATE_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.UPDATE_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

}
