package com.example.travelhana.Controller;

import com.example.travelhana.Dto.AccountConnectResultDto;
import com.example.travelhana.Dto.AccountConnectDto;
import com.example.travelhana.Dto.AccountDummyDto;
import com.example.travelhana.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.travelhana.Dto.ConnectedAccountListDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

	private final AccountService accountService;

	@GetMapping(value ="/list/{userId}")
	public ResponseEntity<ConnectedAccountListDto> getAccountList(@PathVariable int userId) {
		return accountService.getConnectedAccountList(userId);
	}

	@PostMapping("/dummy")
	public ResponseEntity<List<AccountConnectResultDto>> createDummyExternalAccounts(@RequestBody AccountDummyDto accountDummyDto) {
		return accountService.createDummyExternalAccounts(accountDummyDto);
	}

	@GetMapping(value = "/external/{userId}")
	public ResponseEntity<List<AccountConnectResultDto>> getExternalAccountList(@PathVariable int userId) {
		return accountService.findExternalAccountList(userId);
	}

	@PostMapping("/connect")
	public ResponseEntity<AccountConnectResultDto> connectExternalAccount(@RequestBody AccountConnectDto connectAccountDto) {
		return accountService.connectExternalAccount(connectAccountDto);
	}

}
