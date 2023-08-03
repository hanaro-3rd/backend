package com.example.travelhana.Controller;

import com.example.travelhana.Dto.Account.*;
import com.example.travelhana.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

	private final AccountService accountService;

	@GetMapping(value ="/")
	public ResponseEntity<ConnectedAccountListDto> getAccountList(
			@RequestHeader(value = "Authorization") String accessToken) throws Exception {
		return accountService.getConnectedAccountList(accessToken);
	}

	@GetMapping(value = "/external")
	public ResponseEntity<AccountListDto> getExternalAccountList(
			@RequestHeader(value = "Authorization") String accessToken) throws Exception {
		return accountService.findExternalAccountList(accessToken);
	}

	@PostMapping("/{externalAccountId}")
	public ResponseEntity<AccountConnectResultDto> connectExternalAccount(
			@RequestHeader(value = "Authorization") String accessToken, @PathVariable int externalAccountId, @RequestBody AccountPasswordDto accountPasswordDto) throws Exception {
		return accountService.connectExternalAccount(accessToken, externalAccountId, accountPasswordDto);
	}

	@PostMapping("/dummy")
	public ResponseEntity<AccountListDto> createDummyExternalAccounts(
			@RequestHeader(value = "Authorization") String ignoredAccessToken, @RequestBody AccountDummyDto accountDummyDto) throws Exception {
		return accountService.createDummyExternalAccounts(accountDummyDto);
	}

}