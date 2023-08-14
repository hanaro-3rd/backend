package com.example.travelhana.Service;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.Authentication.RoleToUserRequestDto;
import com.example.travelhana.Dto.Authentication.SignupRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {

	ResponseEntity<?> isExistDevice(String deviceId);

	ResponseEntity<?> saveAccount(SignupRequestDto dto);

	void saveRole(String roleName);

	int addRoleToUser(RoleToUserRequestDto dto);

	void updateRefreshToken(String username, String refreshToken);

	ResponseEntity<?> refresh(String refreshToken);

	User getUserByAccessToken(String header);

}
