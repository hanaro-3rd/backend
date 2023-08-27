package com.example.travelhana.Controller;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Account.*;
import com.example.travelhana.Service.AccountService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

	private final UserService userService;
	private final AccountService accountService;

	@GetMapping(value = "")
	public ResponseEntity<?> getAccountList(
			@RequestHeader(value = "Authorization") String accessToken) throws Exception {
		Users users = userService.getUserByAccessToken(accessToken);
		return accountService.getConnectedAccountList(users);
	}

	@GetMapping(value = "/external")
	public ResponseEntity<?> getExternalAccountList(
			@RequestHeader(value = "Authorization") String accessToken) throws Exception {
		Users users = userService.getUserByAccessToken(accessToken);
		return accountService.findExternalAccountList(users);
	}

	@PostMapping("/{externalAccountId}")
	public ResponseEntity<?> connectExternalAccount(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int externalAccountId, @RequestBody AccountPasswordDto accountPasswordDto)
			throws Exception {
		return accountService.connectExternalAccount(accessToken, externalAccountId,
				accountPasswordDto);
	}



}