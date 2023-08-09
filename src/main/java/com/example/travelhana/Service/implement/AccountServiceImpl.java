package com.example.travelhana.Service.implement;

import com.example.travelhana.Dto.Account.*;
import com.example.travelhana.Dto.Exchange.ExchangeRateDto;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Service.AccountService;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Util.ExchangeRateUtil;
import com.example.travelhana.Util.HolidayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Util.CryptoUtil;
import com.example.travelhana.Util.SaltUtil;
import com.example.travelhana.Projection.AccountInfoProjection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

	private final SaltUtil saltUtil;
	private final CryptoUtil cryptoUtil;
	private final HolidayUtil holidayUtil;
	private final ExchangeRateUtil exchangeRateUtil;

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final ExternalAccountRepository externalAccountRepository;

	private final UserService userService;

	private List<AccountInformation> decryptAccountNum(int userId,
	                                                   List<AccountInfoProjection> projections) throws Exception {
		List<AccountInformation> result = new ArrayList<>();
		for (AccountInfoProjection projection : projections) {
			AccountInformation account = AccountInformation.builder()
					.userId(userId)
					.accountId(projection.getId())
					.accountNum(cryptoUtil.decrypt(projection.getAccountNum()))
					.bank(projection.getBank())
					.balance(projection.getBalance())
					.build();
			result.add(account);
		}
		return result;
	}

	@Override
	public ResponseEntity<?> getConnectedAccountList(String accessToken) throws Exception {
		// access token으로 유저 가져오기
		User user = userService.getUserByAccessToken(accessToken);

		// userId에 대한 유저의 연결된 계좌 목록 가져오기
		int userId = user.getId();
		List<AccountInfoProjection> connectedAccounts = accountRepository.findAllByUser_Id(userId);

		// 휴일 여부 가져오기
		Boolean isBusinessDay = holidayUtil.isBusinessDay(LocalDate.now());

		// 연결된 계좌, 휴일 여부를 DTO에 파싱 후 리턴
		ConnectedAccountListDto result = ConnectedAccountListDto
				.builder()
				.accounts(decryptAccountNum(userId, connectedAccounts))
				.isBusinessDay(isBusinessDay)
				.build();
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> createDummyExternalAccounts(AccountDummyDto accountDummyDto)
			throws Exception {
		Random random = new Random();

		// 입력한 정보와 랜덤값으로 유저 정보 생성
		User user = userRepository.findById(accountDummyDto.getUserId())
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

		String registrationNum = user.getRegistrationNum();

		// 입력한 정보와 랜덤값으로 더미 외부 계좌 정보 생성
		String accountPassword = accountDummyDto.getAccountPassword();
		List<String> banks = Arrays.asList("신한", "국민", "하나", "우리", "토스", "카카오");

		for (int i = 0; i < 10; i++) {
			String accountSalt = saltUtil.generateSalt();

			String group1 = String.format("%03d", random.nextInt(1000));
			String group2 = String.format("%04d", random.nextInt(10000));
			String group3 = String.format("%04d", random.nextInt(10000));

			String accountNum = group1 + "-" + group2 + "-" + group3;

			ExternalAccount externalAccount = ExternalAccount
					.builder()
					.accountNum(cryptoUtil.encrypt(accountNum))
					.bank(banks.get(random.nextInt(banks.size())))
					.openDate(java.sql.Date.valueOf(LocalDate.now().minusDays(random.nextInt(365))))
					.salt(accountSalt)
					.password(saltUtil.encodePassword(accountSalt, accountPassword))
					.registrationNum(registrationNum)
					.balance(1000000L)
					.build();
			externalAccountRepository.save(externalAccount);
		}

		// 유저의 주민번호에 해당하는 외부 계좌 목록을 불러옴
		List<AccountInfoProjection> projections = externalAccountRepository.findAllByRegistrationNum(registrationNum);

		// 계좌번호를 복호화하여 AccountListDto에 파싱 후 리턴
		AccountListDto result = AccountListDto
				.builder()
				.externalAccounts(decryptAccountNum(0, projections))
				.build();
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> findExternalAccountList(String accessToken) throws Exception {
		// access token으로 유저 가져오기
		User user = userService.getUserByAccessToken(accessToken);

		// 유저의 주민번호에 해당하는 외부 계좌 목록을 불러옴
		List<AccountInfoProjection> projections = externalAccountRepository.findAllByRegistrationNum(
				user.getRegistrationNum());

		// 계좌번호를 복호화하여 AccountListDto에 파싱 후 리턴
		AccountListDto result = AccountListDto
				.builder()
				.externalAccounts(decryptAccountNum(0, projections))
				.build();
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> connectExternalAccount(String accessToken, int externalAccountId,
	                                                AccountPasswordDto accountPasswordDto) throws Exception {
		// access token으로 유저 가져오기
		User user = userService.getUserByAccessToken(accessToken);

		// externalAccountId에 대한 외부 계좌 존재 여부 확인
		ExternalAccount externalAccount = externalAccountRepository.findById(externalAccountId)
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND));

		// 해당 유저의 계좌 소유 여부 확인
		if (!user.getRegistrationNum().equals(externalAccount.getRegistrationNum())) {
			throw new BusinessExceptionHandler(ErrorCode.UNAUTHORIZED_USER_ACCOUNT);
		}

		// 비밀번호 확인
		String storedSalt = externalAccount.getSalt();
		String storedPassword = externalAccount.getPassword();
		String encodedPassword = saltUtil.encodePassword(storedSalt,
				accountPasswordDto.getAccountPassword());
		if (!storedPassword.equals(encodedPassword)) {
			throw new BusinessExceptionHandler(ErrorCode.UNAUTHORIZED_PASSWORD);
		}

		// 이미 연결된 계좌 여부 확인
		String accountNum = externalAccount.getAccountNum();
		Boolean existAccount = accountRepository.existsAccountByAccountNum(accountNum);
		if (existAccount) {
			throw new BusinessExceptionHandler(ErrorCode.ALREADY_EXIST_ACCOUNT);
		}

		// 연결된 계좌 레코드 생성
		Account account = Account
				.builder()
				.user(user)
				.accountNum(accountNum)
				.bank(externalAccount.getBank())
				.openDate(externalAccount.getOpenDate())
				.password(externalAccount.getPassword())
				.salt(externalAccount.getSalt())
				.balance(externalAccount.getBalance())
				.build();
		accountRepository.save(account);

		// 계좌번호를 복호화하여 AccountConnectResultDto 파싱 후 리턴
		AccountInformation result = AccountInformation
				.builder()
				.userId(user.getId())
				.accountId(account.getId())
				.accountNum(cryptoUtil.decrypt(accountNum))
				.bank(externalAccount.getBank())
				.balance(externalAccount.getBalance())
				.build();
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

}