package com.example.travelhana.Service;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.AccountConnectResultDto;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.mapper.AccountInfoMapper;
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

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final ExternalAccountRepository externalAccountRepository;

	public  ResponseEntity<List<AccountInfoMapper>> findExternalAccountList(Long userId) {
		try {
			Optional<User> user = userRepository.findById(userId);
			List<AccountInfoMapper> result = externalAccountRepository.findAllByRegistrationNum(user.get().getRegistrationNum());
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<AccountConnectResultDto> connectExternalAccount(Long userId, Long externalAccountId) {
		try {
			Optional<User> user = userRepository.findById(userId);
			Optional<ExternalAccount> externalAccount = externalAccountRepository.findById(externalAccountId);

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
