package com.example.travelhana.Service;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.AccountConnectResultDto;
import com.example.travelhana.Dto.ConnectAccountDto;
import com.example.travelhana.Dto.DummyAccountDto;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Util.SaltUtil;
import com.example.travelhana.Projection.AccountInfoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final SaltUtil saltUtil;

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final ExternalAccountRepository externalAccountRepository;

	public ResponseEntity<List<AccountInfoProjection>> createDummyExternalAccounts(DummyAccountDto dummyAccountDto) {
		try {
			String userName = dummyAccountDto.getUserName();
			String registrationNum = dummyAccountDto.getRegistrationNum();
			String accountPassword = dummyAccountDto.getAccountPassword();

			List<String> banks = Arrays.asList("신한", "국민", "하나", "우리", "토스", "카카오");

			Random random = new Random();

			String userSalt = saltUtil.generateSalt();

			User user = new User();
			user.setDeviceId("1234");
			user.setIsWithdrawal(false);
			user.setName(userName);
			user.setPassword(saltUtil.encodePassword(userSalt, "1234"));
			user.setPattern(saltUtil.encodePassword(userSalt, "1234"));
			user.setPhoneNum("010-1234-1234");
			user.setRegistrationNum(registrationNum);
			user.setSalt(userSalt);
			userRepository.save(user);

			for (int i = 0; i < 10; i++) {
				String accountSalt = saltUtil.generateSalt();

				String group1 = String.format("%03d", random.nextInt(1000));
				String group2 = String.format("%04d", random.nextInt(10000));
				String group3 = String.format("%04d", random.nextInt(10000));

				String accountNum = group1 + "-" + group2 + "-" + group3;

				ExternalAccount externalAccount = new ExternalAccount();
				externalAccount.setAccountNum(accountNum);
				externalAccount.setBank(banks.get(random.nextInt(banks.size())));
				externalAccount.setOpenDate(java.sql.Date.valueOf(LocalDate.now().minusDays(random.nextInt(365))));
				externalAccount.setSalt(accountSalt);
				externalAccount.setPassword(saltUtil.encodePassword(accountSalt, accountPassword));
				externalAccount.setRegistrationNum(registrationNum);
				externalAccount.setBalance(0L);
				externalAccountRepository.save(externalAccount);
			}
			List<AccountInfoProjection> result = externalAccountRepository.findAllByRegistrationNum(registrationNum);

			return new ResponseEntity<>(result, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<List<AccountInfoProjection>> findExternalAccountList(Long userId) {
		try {
			Optional<User> user = userRepository.findById(userId);
			List<AccountInfoProjection> result = externalAccountRepository.findAllByRegistrationNum(user.get().getRegistrationNum());

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<AccountConnectResultDto> connectExternalAccount(ConnectAccountDto connectAccountDto) {
		try {
			Long userId = connectAccountDto.getUserId();
			Long externalAccountId = connectAccountDto.getExternalAccountId();
			String accountPassword = connectAccountDto.getAccountPassword();

			Optional<User> user = userRepository.findById(userId);
			Optional<ExternalAccount> externalAccount = externalAccountRepository.findById(externalAccountId);

			// 계좌 소유 여부 확인
			if (!user.get().getRegistrationNum().equals(externalAccount.get().getRegistrationNum())) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			// 비밀번호 확인
			String storedSalt = externalAccount.get().getSalt();
			String storedPassword = externalAccount.get().getPassword();
			String encodedPassword = saltUtil.encodePassword(storedSalt, accountPassword);
			if (!storedPassword.equals(encodedPassword)) {
				return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
			}

			// 이미 연결된 계좌 여부 확인
			String accountNum = externalAccount.get().getAccountNum();
			Boolean existAccount = accountRepository.existsAccountByAccountNum(accountNum);
			if (existAccount) {
				return new ResponseEntity<>(null, HttpStatus.CONFLICT);
			}

			String bank = externalAccount.get().getBank();
			Date openDate = externalAccount.get().getOpenDate();
			String password = externalAccount.get().getPassword();
			String salt = externalAccount.get().getSalt();
			Long balance = externalAccount.get().getBalance();

			Account account = new Account(user.get(), accountNum, bank, openDate, password, salt, balance);
			accountRepository.save(account);

			AccountConnectResultDto result = new AccountConnectResultDto(userId, account.getId(), accountNum, bank, balance);

			return new ResponseEntity<>(result, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
