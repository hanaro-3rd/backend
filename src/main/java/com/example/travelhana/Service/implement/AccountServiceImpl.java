package com.example.travelhana.Service.implement;

import com.example.travelhana.Dto.Account.*;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Service.AccountService;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Util.HolidayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.Users;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExternalAccountRepository;
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

	private final AccountRepository accountRepository;
	private final ExternalAccountRepository externalAccountRepository;

	private final UserService userService;

	private List<AccountInformation> decryptAccountNum(
			int userId, List<AccountInfoProjection> projections) throws Exception {
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
	public ResponseEntity<?> getConnectedAccountList(Users users) throws Exception {
		// userId에 대한 유저의 연결된 계좌 목록 가져오기
		int userId = users.getId();
		List<AccountInfoProjection> connectedAccounts = accountRepository.findAllByUsers_Id(userId);

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
	public ResponseEntity<?> findExternalAccountList(Users users) throws Exception {
		// 유저의 주민번호에 해당하는 연결되지 않은 외부 계좌 목록을 불러옴
		List<AccountInfoProjection> projections =
				externalAccountRepository.findAllByPhoneNumAndIsConnected(users.getPhoneNum(), false);

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
	public ResponseEntity<?> connectExternalAccount(
			String accessToken, int externalAccountId, AccountPasswordDto accountPasswordDto) throws Exception {
		// access token으로 유저 가져오기
		Users users = userService.getUserByAccessToken(accessToken);

		// externalAccountId에 대한 연결되지 않은 외부 계좌 존재 여부 확인
		ExternalAccount externalAccount = externalAccountRepository.findByIdAndIsConnected(externalAccountId, false)
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND));

		// 해당 유저의 계좌 소유 여부 확인
		if (!users.getRegistrationNum().equals(externalAccount.getRegistrationNum())) {
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
				.users(users)
				.accountNum(accountNum)
				.bank(externalAccount.getBank())
				.openDate(externalAccount.getOpenDate())
				.password(externalAccount.getPassword())
				.salt(externalAccount.getSalt())
				.balance(externalAccount.getBalance())
				.build();
		accountRepository.save(account);

		// 외부 계좌의 연결 상태 변경
		externalAccount.changeConnectionStatus();

		// 계좌번호를 복호화하여 AccountConnectResultDto 파싱 후 리턴
		AccountInformation result = AccountInformation
				.builder()
				.userId(users.getId())
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