package com.example.travelhana.Controller;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Dto.AccountConnectResultDto;
import com.example.travelhana.Service.AccountService;
import com.example.travelhana.mapper.AccountInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@GetMapping("/external-account")
	public ResponseEntity<List<AccountInfoMapper>> getExternalAccountList(@RequestParam Long userId) {
		return accountService.findExternalAccountList(userId);
	}

	@GetMapping("/connect-account")
	public ResponseEntity<AccountConnectResultDto> connectExternalAccount(@RequestParam Long userId, Long externalAccountId) {
		return accountService.connectExternalAccount(userId, externalAccountId);
	}

}
