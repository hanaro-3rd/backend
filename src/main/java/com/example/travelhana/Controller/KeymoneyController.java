package com.example.travelhana.Controller;

import com.example.travelhana.Service.KeymoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keymoney")
public class KeymoneyController {

	private final KeymoneyService keymoneyService;

	@GetMapping(value = "")
	public ResponseEntity<?> getMyKeymoney(
			@RequestHeader(value = "Authorization") String accessToken) throws Exception {
		return keymoneyService.getKeymoney(accessToken);
	}

}