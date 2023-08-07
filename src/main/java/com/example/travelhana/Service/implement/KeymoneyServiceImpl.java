package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.Keymoney;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.KeymoneyDto;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.KeymoneyRepository;
import com.example.travelhana.Service.KeymoneyService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeymoneyServiceImpl implements KeymoneyService {

	private final KeymoneyRepository keymoneyRepository;
	private final UserService userService;

	@Override
	public ResponseEntity<?> getKeymoney(String accessToken) {
		User user = userService.getUserByAccessToken(accessToken);

		List<Keymoney> userKeymoney = keymoneyRepository.findByUser_Id(user.getId());
		List<KeymoneyDto> result = new ArrayList<>();

		for (Keymoney keymoney : userKeymoney) {
			KeymoneyDto keymoneyDto = KeymoneyDto
					.builder()
					.unit(keymoney.getUnit())
					.balance(keymoney.getBalance())
					.build();
			result.add(keymoneyDto);
		}

		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
