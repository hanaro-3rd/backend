package com.example.travelhana.Controller;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Dto.AccountConnectResultDto;
import com.example.travelhana.Dto.ConnectAccountDto;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Service.AccountService;
import com.example.travelhana.mapper.AccountInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

	private final AccountService accountService;

	@GetMapping("/external")
	public ResponseEntity<List<ExternalAccount>> getExternalAccountList(@RequestParam Long userId) {
		return accountService.findExternalAccountList(userId);
	}

	@PostMapping("/connect")
	public ResponseEntity<AccountConnectResultDto> connectExternalAccount(@RequestBody ConnectAccountDto connectAccountDto) {
		return accountService.connectExternalAccount(connectAccountDto);
	}


}
