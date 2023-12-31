package com.example.travelhana.Service;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Authentication.RoleToUserRequestDto;
import com.example.travelhana.Dto.Authentication.SignupRequestDto;
import com.example.travelhana.Dto.Authentication.UpdateDeviceRequestDto;
import com.example.travelhana.Dto.Authentication.UpdatePasswordDto;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {

	ResponseEntity<?> isExistDevice(String deviceId);

	ResponseEntity<?> signUp(SignupRequestDto dto) throws Exception;

	void saveRole(String roleName);

	int addRoleToUser(RoleToUserRequestDto dto);

	void updateRefreshToken(String username, String refreshToken);

	ResponseEntity<?> refresh(String refreshToken);

	Users getUserByAccessToken(String header);

	ResponseEntity<?> updatePassword(UpdatePasswordDto dto);

	Optional<Users> validateDuplicateUsername(String registrationNum);

	ResponseEntity<?> updateDevice(UpdateDeviceRequestDto dto);

}
