package com.example.travelhana.Controller;

import com.example.travelhana.Dto.Account.*;
import com.example.travelhana.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

	private final AccountService accountService;

	@GetMapping(value ="/list/{userId}")
	public ResponseEntity<ConnectedAccountListDto> getAccountList(@PathVariable int userId) throws Exception {
		return accountService.getConnectedAccountList(userId);
	}

	@PostMapping("/dummy")
	public ResponseEntity<AccountListDto> createDummyExternalAccounts(@RequestBody AccountDummyDto accountDummyDto) throws Exception {
		return accountService.createDummyExternalAccounts(accountDummyDto);
	}

	@GetMapping(value = "/external/{userId}")
	public ResponseEntity<AccountListDto> getExternalAccountList(@PathVariable int userId) throws Exception {
		return accountService.findExternalAccountList(userId);
	}

	@PostMapping("/connect")
	public ResponseEntity<AccountConnectResultDto> connectExternalAccount(@RequestBody AccountConnectDto connectAccountDto) throws Exception {
		return accountService.connectExternalAccount(connectAccountDto);
	}

}