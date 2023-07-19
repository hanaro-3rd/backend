package com.example.travelhana.Controller;

import com.example.travelhana.Dto.AccountListDto;
import com.example.travelhana.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@GetMapping("/accountlist")
	public String getAccountList() throws URISyntaxException, IOException {
		Boolean isBusinessDay = accountService.isBusinessDay();

		if (isBusinessDay) {
			return "영업일입니다.";
		} else {
			return "영업일이 아닙니다.";
		}
	}
}