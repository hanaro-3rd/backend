package com.example.travelhana.Controller;

import com.example.travelhana.Service.ExternalAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExternalAccountController {

	private final ExternalAccountService externalAccountService;

	@GetMapping("/accountlist")
	public Boolean getAccountList() {
		Boolean isSuccessed = externalAccountService;

		return isSuccessed;
	}

}
