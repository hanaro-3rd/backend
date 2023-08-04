package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.KeyMoney;
import com.example.travelhana.Domain.PaymentHistory;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.*;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.KeyMoneyRepository;
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
	private final KeyMoneyRepository keyMoneyRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;

	@Transactional
	public ResponseEntity<?> payment(String accessToken, PaymentRequestDto paymentRequestDto) {
		try {
			User user = userService.getUserByAccessToken(accessToken);
			int getUserId = user.getId();
			KeyMoney keymoney = keyMoneyRepository.findByUser_IdAndUnit(getUserId, paymentRequestDto.getUnit())
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY));

			Long nowBalance = keymoney.getBalance() - paymentRequestDto.getPrice();
			if (nowBalance < 0) { //잔액부족 에러처리
				throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
			}

			keymoney.updateMinusBalance(paymentRequestDto.getPrice());
			PaymentHistory paymentHistory = PaymentHistory.builder()
					.price(paymentRequestDto.getPrice())
					.unit(paymentRequestDto.getUnit())
					.store(paymentRequestDto.getStore())
					.category(paymentRequestDto.getCategory())
					.address(paymentRequestDto.getAddress())
					.createdAt(LocalDateTime.now())
					.memo(paymentRequestDto.getMemo())
					.lat(paymentRequestDto.getLat())
					.lng(paymentRequestDto.getLng())
					.balance(nowBalance)
					.userId(getUserId)
					.keyMoneyId(keymoney.getId())
					.isSuccess(true)
					.build();

			PaymentHistory responsePaymentHistory = paymentHistoryRepository.save(paymentHistory);
			PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto(responsePaymentHistory);

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
	public ResponseEntity<?> showPaymentHistory(String accessToken, String unit) {
		try {
			User user = userService.getUserByAccessToken(accessToken);
			int getUserId = user.getId();

			KeyMoney keyMoney = keyMoneyRepository.findByUser_IdAndUnit(getUserId, unit)
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY));
			List<PaymentHistory> paymentHistories = paymentHistoryRepository.findAllByKeyMoneyId(keyMoney.getId());
			List<PaymentHistoryDto> paymentListDtos = new ArrayList<>();
			for (PaymentHistory paymentHistory : paymentHistories) {
				PaymentHistoryDto paymentListDto = new PaymentHistoryDto(paymentHistory);
				paymentListDtos.add(paymentListDto);
			}

			ApiResponse apiResponse = ApiResponse.builder()
					.result(paymentListDtos)
					.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
					.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
					.build();

			return new ResponseEntity<>(apiResponse, HttpStatus.OK);
		} catch (BusinessExceptionHandler e) {
			ErrorResponse errorResponse = ErrorResponse.builder()
					.errorMessage(e.getMessage())
					.errorCode(e.getErrorCode().getStatusCode())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}

	@Transactional
	public ResponseEntity<?> updatePaymentHistory(String accessToken, PaymentMemoDto paymentMemoDto) {
		try {
			User user = userService.getUserByAccessToken(accessToken);
			int getUserId = user.getId();
			PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserId(paymentMemoDto.getId(), getUserId);
			if (paymentHistory == null) {
				throw new BusinessExceptionHandler(ErrorCode.INVALID_UPDATE);
			}
			PaymentHistory updatePaymentHistory = PaymentHistory
					.builder()
					.price(paymentHistory.getPrice())
					.balance(paymentHistory.getBalance())
					.unit(paymentHistory.getUnit())
					.store(paymentHistory.getStore())
					.category(paymentMemoDto.getCategory())
					.createdAt(paymentHistory.getCreatedAt())
					.lat(paymentHistory.getLat())
					.lng(paymentHistory.getLng())
					.address(paymentHistory.getAddress())
					.memo(paymentMemoDto.getMemo())
					.userId(paymentHistory.getUserId())
					.keyMoneyId(paymentHistory.getKeyMoneyId())
					.isSuccess(paymentHistory.getIsSuccess())
					.id(paymentHistory.getId())
					.build();

			PaymentHistory responsePaymentHistory = paymentHistoryRepository.save(updatePaymentHistory);
			UpdatePaymentHistoryDto updatePaymentHistoryDto = UpdatePaymentHistoryDto.builder()
					.id(responsePaymentHistory.getId())
					.Category(responsePaymentHistory.getCategory())
					.memo(responsePaymentHistory.getMemo())
					.build();
			ApiResponse apiResponse = ApiResponse.builder()
					.result(updatePaymentHistoryDto)
					.resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
					.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
					.build();
			return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
		} catch (BusinessExceptionHandler e) { //
			return new ResponseEntity<>("서버내부 오류", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 취소되면 승인여부 false
	// 키머니 잔액 update
	@Transactional
	public ResponseEntity<?> deletePaymentHistory(String accessToken, Long payHistoryId) {
		try {
			User user = userService.getUserByAccessToken(accessToken);
			int getUserId = user.getId();
			PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserId(payHistoryId, getUserId);
			if (paymentHistory == null) {
				throw new BusinessExceptionHandler(ErrorCode.INVALID_UPDATE);
			}
			// isSuccess false로 리턴
			PaymentHistory updatePaymentHistory = PaymentHistory
					.builder()
					.price(paymentHistory.getPrice())
					.balance(paymentHistory.getBalance())
					.unit(paymentHistory.getUnit())
					.store(paymentHistory.getStore())
					.category(paymentHistory.getCategory())
					.createdAt(LocalDateTime.now())
					.lat(paymentHistory.getLat())
					.lng(paymentHistory.getLng())
					.address(paymentHistory.getAddress())
					.memo(paymentHistory.getMemo())
					.userId(paymentHistory.getUserId())
					.keyMoneyId(paymentHistory.getKeyMoneyId())
					.isSuccess(false)
					.build();
			KeyMoney keymoney = keyMoneyRepository.findByUser_IdAndUnit(getUserId, updatePaymentHistory.getUnit())
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY));
			keymoney.updatePlusBalance(updatePaymentHistory.getPrice());
			PaymentHistory responsePaymentHistory = paymentHistoryRepository.save(updatePaymentHistory);

			KeymoneyDto keymoneyDto = KeymoneyDto.builder()
					.balance(keymoney.getBalance())
					.id(keymoney.getId())
					.user(keymoney.getUser())
					.unit(keymoney.getUnit())
					.build();
			PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
					.price(responsePaymentHistory.getPrice())
					.balance(responsePaymentHistory.getBalance())
					.unit(responsePaymentHistory.getUnit())
					.store(responsePaymentHistory.getStore())
					.category(responsePaymentHistory.getCategory())
					.createdAt(responsePaymentHistory.getCreatedAt())
					.lat(responsePaymentHistory.getLat())
					.lng(responsePaymentHistory.getLng())
					.address(responsePaymentHistory.getAddress())
					.memo(responsePaymentHistory.getMemo())
					.userId(responsePaymentHistory.getUserId())
					.keyMoneyId(responsePaymentHistory.getKeyMoneyId())
					.isSuccess(responsePaymentHistory.getIsSuccess())
					.id(responsePaymentHistory.getId())
					.build();
			Map<String, Object> responseData = new HashMap<>();
			responseData.put("totalbalance", keymoneyDto.getBalance());
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
