package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.Keymoney.*;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.ExchangeHistoryRepository;
import com.example.travelhana.Repository.KeymoneyRepository;
import com.example.travelhana.Repository.PaymentHistoryRepository;
import com.example.travelhana.Service.KeymoneyService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeymoneyServiceImpl implements KeymoneyService {

	private final UserService userService;
	private final KeymoneyRepository keymoneyRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final ExchangeHistoryRepository exchangeHistoryRepository;

	private String makeExchangeTitle(Boolean isBought, String unit) {
		String title = "";
		if (isBought) {
			if (unit.equals("JPY")) {
				title += "엔화 환전";
			} else if (unit.equals("USD")) {
				title += "달러 환전";
			} else {
				title += "유로 환전";
			}
		} else {
			title += "원화 환전";
		}
		return title;
	}

	@Override
	public ResponseEntity<?> getKeymoney(String accessToken) {
		// access token으로 유저 가져오기
		User user = userService.getUserByAccessToken(accessToken);

		// userId로 유저가 보유한 모든 키머니 불러오기
		List<Keymoney> userKeymoney = keymoneyRepository.findByUser_Id(user.getId());

		// 엔티티로 불러와진 객체를 dto로 파싱
		List<KeymoneySimpleDto> result = new ArrayList<>();
		for (Keymoney keymoney : userKeymoney) {
			KeymoneySimpleDto keymoneySimpleDto = KeymoneySimpleDto
					.builder()
					.unit(keymoney.getUnit())
					.balance(keymoney.getBalance())
					.build();
			result.add(keymoneySimpleDto);
		}

		// ResponseEntity 묶어서 리턴
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getKeymoneyHistory(String accessToken, String unit, String filter) {
		// access token으로 유저 가져오기
		User user = userService.getUserByAccessToken(accessToken);

		// 유효하지 않은 화폐단위 에러
		Currency currency = Currency.getByCode(unit);
		if (currency == null) {
			throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGE_UNIT);
		}

		// 유효하지 않은 필터 에러
		if (!filter.equals("all") && !filter.equals("payment") && !filter.equals("exchange")) {
			throw new BusinessExceptionHandler(ErrorCode.INVALID_HISTORY_FILTER);
		}

		// userId로 유저가 가진 unit에 해당하는 키머니 불러오기
		Keymoney keymoney = keymoneyRepository.findByUser_IdAndUnit(user.getId(), unit)
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY));
		int keymoneyId = keymoney.getId();

		// 해당 키머니에 대한 결제 내역 가져오기
		List<KeymoneyDto> keymoneyDtos = new ArrayList<>();
		if (filter.equals("all") || filter.equals("payment")) {
			List<PaymentHistory> paymentHistories = paymentHistoryRepository.findAllByKeymoneyId(keymoneyId);
			// 엔티티에서 dto 파싱
			for (PaymentHistory paymentHistory : paymentHistories) {
				KeymoneyDto keymoneyDto = KeymoneyDto
						.builder()
						.historyId(paymentHistory.getId())
						.subject(paymentHistory.getStore())
						.keymoney(paymentHistory.getPrice())
						.type("payment")
						.category(paymentHistory.getCategory())
						.createdAt(paymentHistory.getCreatedAt())
						.balance(paymentHistory.getBalance())
						.unit(unit)
						.isDeposit(!paymentHistory.getIsPayment())
						.build();
				keymoneyDtos.add(keymoneyDto);
			}
		}

		// 해당 키머니에 대한 환전 내역 가져오기
		if (filter.equals("all") || filter.equals("exchange")) {
			List<ExchangeHistory> exchangeHistories = exchangeHistoryRepository.findAllByKeymoneyId(keymoneyId);
			// 엔티티에서 dto 파싱
			for (ExchangeHistory exchangeHistory : exchangeHistories) {
				KeymoneyDto keymoneyDto = KeymoneyDto
						.builder()
						.historyId(exchangeHistory.getId())
						.subject(makeExchangeTitle(exchangeHistory.getIsBought(), unit))
						.keymoney(exchangeHistory.getExchangeKey())
						.type("exchange")
						.category("환전")
						.createdAt(exchangeHistory.getExchangeDate())
						.balance(exchangeHistory.getBalance())
						.unit(unit)
						.isDeposit(exchangeHistory.getIsBought()) // 원화 키머니에 대한 분기 처리 필요
						.build();
				keymoneyDtos.add(keymoneyDto);
			}
		}

		// 환전 내역과 결제 내역을 합친 배열을 시간 내림차순으로 정렬
		keymoneyDtos.sort(Comparator.comparing(KeymoneyDto::getCreatedAt).reversed());

		// 키머니의 id와 잔액들과 함께 파싱
		KeymoneyHistoryDto result = KeymoneyHistoryDto.builder()
				.keymoneyId(keymoney.getId())
				.totalBalance(keymoney.getBalance())
				.keymoneyHistory(keymoneyDtos)
				.build();

		// ResponseEntity 묶어서 리턴
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getDetailKeymoneyHistory(String accessToken, Long historyId, String type) {
		// access token으로 유저 가져오기
		User user = userService.getUserByAccessToken(accessToken);

		// type이 결제(payment)나 환전(exchange)가 아니라면 에러
		if (!type.equals("payment") && !type.equals("exchange")) {
			throw new BusinessExceptionHandler(ErrorCode.INVALID_HISTORY_TYPE);
		}

		// type이 결제(payment)라면 historyId로 결제내역 테이블을 검색
		Object result;
		if (type.equals("payment")) {
			PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserId(historyId, user.getId())
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PAYMENT_HISTORY_NOT_FOUND));
			Keymoney keymoney = keymoneyRepository.findById(paymentHistory.getKeymoneyId())
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY));
			result = KeymoneyPaymentDto
					.builder()
					.historyId(paymentHistory.getId())
					.store(paymentHistory.getStore())
					.price(paymentHistory.getPrice())
					.isDeposit(!paymentHistory.getIsPayment())
					.memo(paymentHistory.getMemo())
					.type(type)
					.category(paymentHistory.getCategory())
					.createdAt(paymentHistory.getCreatedAt())
					.unit(keymoney.getUnit())
					.build();
		}
		// type이 환전(exchange)이라면 historyId로 결제내역 테이블을 검색
		else if (type.equals("exchange")) {
			ExchangeHistory exchangeHistory = exchangeHistoryRepository.findByIdAndUserId(historyId, user.getId())
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.EXCHANGE_HISTORY_NOT_FOUND));
			Keymoney keymoney = keymoneyRepository.findById(exchangeHistory.getKeymoneyId())
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY));
			result = KeymoneyExchangeDto
					.builder()
					.historyId(exchangeHistory.getId())
					.exchangeKey(exchangeHistory.getExchangeKey())
					.exchangeWon(exchangeHistory.getExchangeWon())
					.exchangeRate(exchangeHistory.getExchangeRate())
					.type(type)
					.unit(keymoney.getUnit())
					.createdAt(exchangeHistory.getExchangeDate())
					.isDeposit(exchangeHistory.getIsBought())
					.build();
		}
		// type이 둘 다 아니라면 에러
		else {
			throw new BusinessExceptionHandler(ErrorCode.INVALID_HISTORY_TYPE);
		}

		// ResponseEntity 묶어서 리턴
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

}
