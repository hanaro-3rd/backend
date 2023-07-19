package com.example.travelhana.Controller;

import com.example.travelhana.Dto.ConnectedAccountListDto;
import com.example.travelhana.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@GetMapping("/accountlist/{currencyCode}")
	public ConnectedAccountListDto getAccountList(@PathVariable String currencyCode) throws URISyntaxException, IOException {
		return accountService.getConnectedAccountList(currencyCode);
	}
}