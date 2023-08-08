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

	@GetMapping(value = "/{unit}")
	public ResponseEntity<?> getKeymoneyHistory(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable String unit,
			@RequestParam(defaultValue = "all", required = false) String filter) throws Exception {
		return keymoneyService.getKeymoneyHistory(accessToken, unit, filter);
	}

	@GetMapping(value = "/detail")
	public ResponseEntity<?> getDetailKeymoneyHistory(
			@RequestHeader(value = "Authorization") String accessToken,
			@RequestParam Long historyId,
			@RequestParam String type) throws Exception {
		return keymoneyService.getDetailKeymoneyHistory(accessToken, historyId, type);
	}
}