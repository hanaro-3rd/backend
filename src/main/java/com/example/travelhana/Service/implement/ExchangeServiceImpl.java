package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.Exchange.ExchangeRateDto;
import com.example.travelhana.Dto.Exchange.ExchangeRateInfo;
import com.example.travelhana.Dto.Exchange.ExchangeRequestDto;
import com.example.travelhana.Dto.Exchange.ExchangeResponseDto;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Object.ExchangeSuccess;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExchangeHistoryRepository;
import com.example.travelhana.Repository.ExchangeRateRepository;
import com.example.travelhana.Repository.KeymoneyRepository;
import com.example.travelhana.Service.ExchangeService;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Util.ExchangeRateUtil;
import com.example.travelhana.Util.HolidayUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExchangeServiceImpl implements ExchangeService {

	private final AccountRepository accountRepository;
	private final KeymoneyRepository keymoneyRepository;
	private final ExchangeHistoryRepository exchangeHistoryRepository;
	private final ExchangeRateRepository exchangeRateRepository;

	private final UserService userService;
	private final ExchangeRateUtil exchangeRateUtil;
	private final HolidayUtil holidayUtil;
	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;
	private ListOperations<String, String> stringStringListOperations;

	@PostConstruct
	public void init() {
		stringStringListOperations = redisTemplate.opsForList();
	}

	//배포 서버에 redis 설치 안됐을 시 사용할 테스트 메소드
	@Transactional
	public ResponseEntity<?> getExchangeRate() throws URISyntaxException {
		// OpenAPI로 각 환율 정보 가져오기
		ExchangeRateInfo usdExchangeRate = exchangeRateUtil.getExchangeRateByAPI("USD");
		ExchangeRateInfo jpyExchangeRate = exchangeRateUtil.getExchangeRateByAPI("JPY");
		ExchangeRateInfo eurExchangeRate = exchangeRateUtil.getExchangeRateByAPI("EUR");

		// 각 환율 정보를 dto에 파싱
		ExchangeRateDto result = ExchangeRateDto
				.builder()
				.usd(usdExchangeRate)
				.jpy(jpyExchangeRate)
				.eur(eurExchangeRate)
				.build();

		// ResponseEntity에 묶어서 리턴
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.OPEN_API_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.OPEN_API_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	//1분마다 환율 캐싱
	@Scheduled(cron = "0 * * * * *") // 매 분 0초에 실행
	@Transactional
	public void insertRedis() throws URISyntaxException, JsonProcessingException {
		ExchangeRateInfo usdExchangeRate = exchangeRateUtil.getExchangeRateByAPI("USD");
		ExchangeRateInfo jpyExchangeRate = exchangeRateUtil.getExchangeRateByAPI("JPY");
		ExchangeRateInfo eurExchangeRate = exchangeRateUtil.getExchangeRateByAPI("EUR");
		// 각 환율 정보를 dto에 파싱
		ExchangeRateDto result = ExchangeRateDto
				.builder()
				.usd(usdExchangeRate)
				.jpy(jpyExchangeRate)
				.eur(eurExchangeRate)
				.build();
		System.out.println(result.getUpdatedAt());
		String dtoAsString = objectMapper.writeValueAsString(result);
		stringStringListOperations.leftPush("mystack", dtoAsString);
	}

	//redis에서 환율 읽기
	public ResponseEntity<?> getExchangeRateFromRedis() throws JsonProcessingException {
		String getone = stringStringListOperations.leftPop("mystack");
		ExchangeRateDto result = objectMapper.readValue(getone, ExchangeRateDto.class);
		System.out.println(result.getUpdatedAt());
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	//월-금까지 9시부터 20시까지 2시간 간격으로 실행
	//환율정보 2시간마다 DB에 저장
	@Scheduled(cron = "0 0 9-20/2 * * 1-5")
	public void insertIntoDb() throws JsonProcessingException {
		List<String> arr = stringStringListOperations.range("mystack", 0, -1);

		List<ExchangeRate> savearr = arr.stream()
				.map(getone -> {
					try {
						ExchangeRateDto result = objectMapper.readValue(getone, ExchangeRateDto.class);
						LocalDateTime updatedAt = result.getUpdatedAt();
						List<ExchangeRate> exchangeRates = new ArrayList<>();
						exchangeRates.add(ExchangeRate.builder()
								.unit("EUR")
								.exchangeRate(result.getEur().getExchangeRate())
								.changePrice(result.getEur().getChangePrice())
								.updatedAt(updatedAt)
								.build());
						exchangeRates.add(ExchangeRate.builder()
								.unit("JPY")
								.exchangeRate(result.getJpy().getExchangeRate())
								.changePrice(result.getJpy().getChangePrice())
								.updatedAt(updatedAt)
								.build());
						exchangeRates.add(ExchangeRate.builder()
								.unit("USD")
								.exchangeRate(result.getUsd().getExchangeRate())
								.changePrice(result.getUsd().getChangePrice())
								.updatedAt(updatedAt)
								.build());
						return exchangeRates;
					} catch (IOException e) {
						e.printStackTrace();
						return new ArrayList<ExchangeRate>();
					}
				})
				.flatMap(List::stream) // 하나의 스트림에 여러 개의 ExchangeRate 객체가 포함
				.collect(Collectors.toList());
		exchangeRateRepository.saveAll(savearr);
	}

	@Transactional
	public ResponseEntity<?> exchange(String accessToken, ExchangeRequestDto request) throws URISyntaxException {
		return exchangeInAccountBusinessDay(accessToken, request);
	}

	@Transactional
	public ResponseEntity<?> exchangeInAccountBusinessDay(String accessToken, ExchangeRequestDto dto) throws URISyntaxException {
		Account account = accountRepository.findById(dto.getAccountId())
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_ACCOUNT));

		// 접속한 유저에 대한 계좌 소유 여부 확인
		User user = userService.getUserByAccessToken(accessToken);
		if (!user.equals(account.getUser())) {
			throw new BusinessExceptionHandler(ErrorCode.UNAUTHORIZED_USER_ACCOUNT);
		}

		// 유효하지 않은 화폐단위 에러
		Currency currency = Currency.getByCode(dto.getUnit());
		if (currency == null) {
			throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGE_UNIT);
		}

		if (dto.getMoney() <= 0) { //0이나 음수는 환전불가
			throw new BusinessExceptionHandler(ErrorCode.NO_ZERO_OR_MINUS);
		}

		Optional<Keymoney> keymoney = keymoneyRepository.findByUser_IdAndUnit(
				account.getUser().getId(), dto.getUnit());

		//키머니가 존재하지 않는다면 만들어주기
		if (!keymoney.isPresent()) {
			keymoney = Optional.ofNullable(makeKeyMoney(account.getUser(), dto.getUnit()));
		}

		//잔액부족 시 에러
		if (dto.getIsBought()) { //원화->외화 요청 : 원화계좌 확인
			if (dto.getMoney() > account.getBalance()) {
				throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
			}
		} else { //외화->원화 요청 : 외화계좌 확인
			if (dto.getMoneyToExchange() > keymoney.get().getBalance()) {
				throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
			}
		}

		//환전 시작
		ExchangeResponseDto exchangeHistory = saveExchangeThings(keymoney.get(), account, dto); //키머니, 원화계좌, 처리할요청

		ApiResponse apiResponse = ApiResponse.builder()
				.result(exchangeHistory)
				.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@Transactional
	public ExchangeResponseDto saveExchangeThings(
			Keymoney keymoney, Account account, ExchangeRequestDto dto) throws URISyntaxException {

		ExchangeSuccess exchangeResult;
		Boolean isBusinessDay = holidayUtil.isBusinessDay(LocalDate.now());
		if (!isBusinessDay) {
			//공휴일이면
			if (!dto.getIsBought()) {
				//외화매도는 안됨
				throw new BusinessExceptionHandler(ErrorCode.ONLY_PUCHASE_IN_HOLIDAY);
			}
			dto.updateExchangeRate(20.0); //수수료 20원 추가해서 환전
		}

		if (dto.getIsBought()) { //원화 -> 외화
			exchangeResult = wonToKeyByClient(keymoney, account, dto); //money=원화
		} else { //외화 -> 원화
			exchangeResult = keyToWonByClient(keymoney, account, dto); //money=외화
		}

		return saveExchangeHistory(account, keymoney, exchangeResult, dto, isBusinessDay);
	}

	//환전내역 저장
	@Transactional
	public ExchangeResponseDto saveExchangeHistory(
			Account account, Keymoney keyMoney, ExchangeSuccess exchangeSuccess, ExchangeRequestDto exchangeRateInfo, Boolean isBusinessDay) {
		ExchangeHistory exchangeHistory = ExchangeHistory
				.builder()
				.accountId(account.getId())
				.exchangeRate(exchangeRateInfo.getExchangeRate())
				.keymoneyId(keyMoney.getId())
				.exchangeKey(exchangeSuccess.getExchangeKey()) //환전한 외화
				.isBought(exchangeSuccess.getIsBought())
				.isBusinessday(isBusinessDay)
				.userId(account.getUser().getId())
				.balance(exchangeSuccess.getKeymoneyBalance())
				.exchangeWon(exchangeSuccess.getExchangeWon()) //환전한 원화
				.build();

		exchangeHistoryRepository.save(exchangeHistory);
		ExchangeResponseDto responseDto;
		if (exchangeSuccess.getIsBought()) { //원화->외화
			responseDto = ExchangeResponseDto
					.builder()
					.exchangeFromMoney(exchangeSuccess.getExchangeWon())
					.exchangeFromUnit("KRW")
					.exchangeToMoney(exchangeHistory.getExchangeKey())
					.exchangeToUnit(keyMoney.getUnit())
					.exchangeRate(exchangeRateInfo.getExchangeRate())
					.changePrice(exchangeRateInfo.getChangePrice())
					.build();
		} else { //외화->원화
			responseDto = ExchangeResponseDto
					.builder()
					.exchangeFromMoney(exchangeHistory.getExchangeKey())
					.exchangeFromUnit(keyMoney.getUnit())
					.exchangeToMoney(exchangeHistory.getExchangeWon())
					.exchangeToUnit("KRW")
					.exchangeRate(exchangeRateInfo.getExchangeRate())
					.changePrice(exchangeRateInfo.getChangePrice())
					.build();
		}

		return responseDto;
	}

	//원화->외화
	@Transactional
	public ExchangeSuccess wonToKeyByClient(
			Keymoney keyMoney, Account account, ExchangeRequestDto dto) {
		Currency currency = Currency.getByCode(keyMoney.getUnit());
		if (currency == null) {
			throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGE_UNIT);
		}

		if (dto.getMoney() > 1000000) {
			throw new BusinessExceptionHandler(ErrorCode.TOO_MUCH_PURCHASE);
		}

		if (dto.getMoneyToExchange() < currency.getMinCurrency()) {
			throw new BusinessExceptionHandler(ErrorCode.MIN_CURRENCY);
		}

		//키머니 잔액 200만원 초과 금지
		if (dto.getMoneyToExchange() + keyMoney.getBalance() >= 2000000) {
			throw new BusinessExceptionHandler(ErrorCode.TOO_MUCH_KEYMONEY_BALANCE);
		}

		keyMoney.updatePlusBalance(dto.getMoneyToExchange()); //키머니 잔액 추가
		account.updateBalance(dto.getMoney() * (-1)); //원화 잔액 차감

		ExchangeSuccess exchangeSuccess = ExchangeSuccess.builder()
				.exchangeWon(dto.getMoney())
				.exchangeKey(dto.getMoneyToExchange())
				.keymoneyBalance(keyMoney.getBalance())
				.isBought(true)
				.build();
		return exchangeSuccess;
	}

	//외화->원화
	@Transactional
	public ExchangeSuccess keyToWonByClient(
			Keymoney keyMoney, Account account, ExchangeRequestDto dto) {
		Currency currency = Currency.getByCode(keyMoney.getUnit());
		if (currency == null) {
			throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGE_UNIT);
		}
		if (dto.getMoneyToExchange() < currency.getMinCurrency()) {
			throw new BusinessExceptionHandler(ErrorCode.MIN_CURRENCY);
		}

		keyMoney.updatePlusBalance(dto.getMoneyToExchange() * (-1)); //키머니 잔액 차감
		account.updateBalance(dto.getMoney()); //원화 잔액 추가

		ExchangeSuccess exchangeSuccess = ExchangeSuccess.builder()
				.exchangeWon(dto.getMoney())
				.exchangeKey(dto.getMoneyToExchange())
				.keymoneyBalance(keyMoney.getBalance())
				.isBought(false)
				.build();
		return exchangeSuccess;
	}

	//외환계좌 만들기
	@Transactional
	public Keymoney makeKeyMoney(User user, String unit) {
		Keymoney newKeymoney = Keymoney.builder()
				.user(user)
				.unit(unit)
				.balance(0L)
				.build();
		keymoneyRepository.save(newKeymoney);
		return newKeymoney;
	}

}

