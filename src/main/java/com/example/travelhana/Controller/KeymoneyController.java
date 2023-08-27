package com.example.travelhana.Controller;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Service.KeymoneyService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keymoney")
public class KeymoneyController {

	private final UserService userService;
	private final KeymoneyService keymoneyService;

	@GetMapping(value = "")
	public ResponseEntity<?> getMyKeymoney(
			@RequestHeader(value = "Authorization") String accessToken) throws Exception {
		Users users = userService.getUserByAccessToken(accessToken);
		return keymoneyService.getKeymoney(users);
	}

	@GetMapping(value = "/{unit}")
	public ResponseEntity<?> getKeymoneyHistory(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable String unit,
			@RequestParam(defaultValue = "all", required = false) String filter) throws Exception {
		Users users = userService.getUserByAccessToken(accessToken);
		return keymoneyService.getKeymoneyHistory(users, unit, filter);
	}

	@GetMapping(value = "/detail")
	public ResponseEntity<?> getDetailKeymoneyHistory(
			@RequestHeader(value = "Authorization") String accessToken,
			@RequestParam Long historyId,
			@RequestParam String type) throws Exception {
		Users users = userService.getUserByAccessToken(accessToken);
		return keymoneyService.getDetailKeymoneyHistory(users, historyId, type);
	}

}