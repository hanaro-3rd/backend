package com.example.travelhana.Controller;

import com.example.travelhana.Service.AccountService;
import com.example.travelhana.mapper.AccountInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@GetMapping("/accountlist")
	public List<AccountInfoMapper> getExternalAccountList(@RequestParam Long userId) {
		return accountService.findExternalAccountList(userId);
	}

}
