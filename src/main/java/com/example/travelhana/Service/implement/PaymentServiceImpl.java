package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.Keymoney;
import com.example.travelhana.Domain.PaymentHistory;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.Payment.PaymentHistoryDto;
import com.example.travelhana.Dto.Payment.PaymentMemoDto;
import com.example.travelhana.Dto.Payment.PaymentRequestDto;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.KeymoneyRepository;
import com.example.travelhana.Repository.PaymentHistoryRepository;
import com.example.travelhana.Service.PaymentService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final UserService userService;
	private final KeymoneyRepository keyMoneyRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;

	@Transactional
	public ResponseEntity<?> payment(String accessToken, PaymentRequestDto paymentRequestDto) {
		try {
			// access token으로 유저 가져오기
			User user = userService.getUserByAccessToken(accessToken);
			int userId = user.getId();

			// userId로 유저가 가진 unit에 해당하는 키머니 불러오기
			Keymoney keymoney = keyMoneyRepository.findByUser_IdAndUnit(userId, paymentRequestDto.getUnit())
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY));

			// 잔액보다 결재 금액이 높을 시 에러, 아닐 시 잔액 업데이트
			Long nowBalance = keymoney.getBalance() - paymentRequestDto.getPrice();
			if (nowBalance < 0) {
				throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
			}
			keymoney.updateMinusBalance(paymentRequestDto.getPrice());

			// 결제 내역 추가
			PaymentHistory paymentHistory = PaymentHistory.builder()
					.price(paymentRequestDto.getPrice())
					.store(paymentRequestDto.getStore())
					.category(paymentRequestDto.getCategory())
					.address(paymentRequestDto.getAddress())
					.createdAt(LocalDateTime.now())
					.memo(paymentRequestDto.getMemo())
					.lat(paymentRequestDto.getLat())
					.lng(paymentRequestDto.getLng())
					.balance(nowBalance)
					.userId(userId)
					.keymoneyId(keymoney.getId())
					.isPayment(true)
					.build();
			PaymentHistory responsePaymentHistory = paymentHistoryRepository.save(paymentHistory);

			// 엔티티를 dto에 파싱
			PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
					.id(paymentHistory.getId())
					.price(responsePaymentHistory.getPrice())
					.store(responsePaymentHistory.getStore())
					.category(responsePaymentHistory.getCategory())
					.address(responsePaymentHistory.getAddress())
					.createdAt(responsePaymentHistory.getCreatedAt())
					.memo(responsePaymentHistory.getMemo())
					.lat(responsePaymentHistory.getLat())
					.lng(responsePaymentHistory.getLng())
					.balance(responsePaymentHistory.getBalance())
					.userId(responsePaymentHistory.getUserId())
					.keymoneyId(responsePaymentHistory.getKeymoneyId())
					.isPayment(responsePaymentHistory.getIsPayment())
					.build();

			// ResponseEntity 객체에 묶어서 리턴
			ApiResponse apiResponse = ApiResponse.builder()
					.result(paymentHistoryDto)
					.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
					.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
					.build();
			return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
		} catch (BusinessExceptionHandler e) {
			ErrorResponse errorResponse = ErrorResponse.builder()
					.errorMessage(e.getMessage())
					.errorCode(e.getErrorCode().getStatusCode())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public ResponseEntity<?> updatePaymentHistory(String accessToken, Long paymentId, PaymentMemoDto paymentMemoDto) {
		try {
			// access token으로 유저 가져오기
			User user = userService.getUserByAccessToken(accessToken);
			int userId = user.getId();

			// 결제 id와 유저 id에 대한 내역이 있는지 확인
			PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserId(paymentId, userId)
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_UPDATE));

			// 메모와 카테고리를 수정
			paymentHistory.updateCategoryAndMemo(paymentMemoDto.getMemo(), paymentMemoDto.getCategory());

			// 엔티티를 dto에 파싱
			PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
					.id(paymentHistory.getId())
					.price(paymentHistory.getPrice())
					.store(paymentHistory.getStore())
					.category(paymentHistory.getCategory())
					.address(paymentHistory.getAddress())
					.createdAt(paymentHistory.getCreatedAt())
					.memo(paymentHistory.getMemo())
					.lat(paymentHistory.getLat())
					.lng(paymentHistory.getLng())
					.balance(paymentHistory.getBalance())
					.userId(paymentHistory.getUserId())
					.keymoneyId(paymentHistory.getKeymoneyId())
					.isPayment(paymentHistory.getIsPayment())
					.build();

			// ResponseEntity 객체에 묶어서 리턴
			ApiResponse apiResponse = ApiResponse.builder()
					.result(paymentHistoryDto)
					.resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
					.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
					.build();
			return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
		} catch (BusinessExceptionHandler e) {
			ErrorResponse errorResponse = ErrorResponse.builder()
					.errorMessage(e.getMessage())
					.errorCode(e.getErrorCode().getStatusCode())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 취소되면 승인여부 false
	// 키머니 잔액 update
	@Transactional
	public ResponseEntity<?> deletePaymentHistory(String accessToken, Long paymentId) {
		try {
			// access token으로 유저 가져오기
			User user = userService.getUserByAccessToken(accessToken);
			int userId = user.getId();

			// 결제 id와 유저 id에 대한 내역이 있는지 확인
			PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserId(paymentId, userId)
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_UPDATE));

			// 결제 내역의 키머니 id에 대한 키머니가 있는지 확인
			Keymoney keymoney = keyMoneyRepository.findById(paymentHistory.getKeymoneyId())
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY));

			// 잔액을 가격만큼 추가
			keymoney.updatePlusBalance(paymentHistory.getPrice());

			// isPayment를 false (결제 취소)로 해서 결제 내역의 레코드 추가
			PaymentHistory updatePaymentHistory = PaymentHistory.builder()
					.price(paymentHistory.getPrice())
					.balance(paymentHistory.getBalance())
					.store(paymentHistory.getStore())
					.category(paymentHistory.getCategory())
					.createdAt(LocalDateTime.now())
					.lat(paymentHistory.getLat())
					.lng(paymentHistory.getLng())
					.address(paymentHistory.getAddress())
					.memo(paymentHistory.getMemo())
					.userId(paymentHistory.getUserId())
					.keymoneyId(paymentHistory.getKeymoneyId())
					.isPayment(false)
					.build();
			PaymentHistory responsePaymentHistory = paymentHistoryRepository.save(updatePaymentHistory);

			// 엔티티를 dto로 파싱
			PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
					.price(responsePaymentHistory.getPrice())
					.balance(responsePaymentHistory.getBalance())
					.store(responsePaymentHistory.getStore())
					.category(responsePaymentHistory.getCategory())
					.createdAt(responsePaymentHistory.getCreatedAt())
					.lat(responsePaymentHistory.getLat())
					.lng(responsePaymentHistory.getLng())
					.address(responsePaymentHistory.getAddress())
					.memo(responsePaymentHistory.getMemo())
					.userId(responsePaymentHistory.getUserId())
					.keymoneyId(responsePaymentHistory.getKeymoneyId())
					.isPayment(responsePaymentHistory.getIsPayment())
					.id(responsePaymentHistory.getId())
					.build();

			// ResponseEntity 객체에 묶어서 리턴
			Map<String, Object> responseData = new HashMap<>();
			responseData.put("totalbalance", keymoney.getBalance());
			responseData.put("paymentHistory", paymentHistoryDto);
			ApiResponse apiResponse = ApiResponse.builder()
					.result(responseData)
					.resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
					.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
					.build();
			return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
		} catch (BusinessExceptionHandler e) {
			ErrorResponse errorResponse = ErrorResponse.builder()
					.errorMessage(e.getMessage())
					.errorCode(e.getErrorCode().getStatusCode())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
