package com.example.travelhana.Service;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.Authentication.RoleToUserRequestDto;
import com.example.travelhana.Dto.Authentication.SignupRequestDto;
import com.example.travelhana.Dto.Authentication.UpdatePasswordDto;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {

	ResponseEntity<?> isExistDevice(String deviceId);

	ResponseEntity<?> saveAccount(SignupRequestDto dto) throws Exception;

	void saveRole(String roleName);

	int addRoleToUser(RoleToUserRequestDto dto);

	void updateRefreshToken(String username, String refreshToken);

	ResponseEntity<?> refresh(String refreshToken);

	User getUserByAccessToken(String header);

	ResponseEntity<?> updatePassword(UpdatePasswordDto dto);
	Optional<User> validateDuplicateUsername(String registrationNum);

}
