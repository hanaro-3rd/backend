package com.example.travelhana.Controller;

import com.example.travelhana.Dto.AccountConnectResultDto;
import com.example.travelhana.Dto.AccountConnectDto;
import com.example.travelhana.Dto.AccountDummyDto;
import com.example.travelhana.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

	private final AccountService accountService;

	@PostMapping("/dummy")
	public ResponseEntity<List<AccountConnectResultDto>> createDummyExternalAccounts(@RequestBody AccountDummyDto accountDummyDto) {
		return accountService.createDummyExternalAccounts(accountDummyDto);
	}

	@GetMapping("/external")
	public ResponseEntity<List<AccountConnectResultDto>> getExternalAccountList(@RequestParam Long userId) {
		return accountService.findExternalAccountList(userId);
	}

	@PostMapping("/connect")
	public ResponseEntity<AccountConnectResultDto> connectExternalAccount(@RequestBody AccountConnectDto connectAccountDto) {
		return accountService.connectExternalAccount(connectAccountDto);
	}

}
