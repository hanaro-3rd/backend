package com.example.travelhana.Service;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.AccountConnectResultDto;
import com.example.travelhana.Dto.ConnectAccountDto;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Util.SaltUtil;
import com.example.travelhana.Projection.AccountInfoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final SaltUtil saltUtil;

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final ExternalAccountRepository externalAccountRepository;

	private void setSaltAndSaltedPassword(List<ExternalAccount> result) {
		result.stream().forEach(externalAccount -> {
			String salt = saltUtil.generateSalt();

			externalAccount.setSalt(salt);
			externalAccount.setPassword(saltUtil.encodePassword(salt, externalAccount.getPassword()));

			externalAccountRepository.save(externalAccount);
		});
	}

	public ResponseEntity<List<AccountInfoProjection>> findExternalAccountList(Long userId) {
		try {
			Optional<User> user = userRepository.findById(userId);
			List<AccountInfoProjection> result = externalAccountRepository.findAllByRegistrationNum(user.get().getRegistrationNum());

//			salt 생성 및 비밀번호 암호화해서 DB 업데이트
//			setSaltAndSaltedPassword(result);

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
