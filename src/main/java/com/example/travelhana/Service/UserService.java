package com.example.travelhana.Service;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.*;

import java.util.List;
import java.util.Map;

public interface UserService {

    DeviceDto isExistDevice(String deviceId);
    List<UserResponseDto> userExist();
    Boolean isValidUser(SignupRequestDto dto);
    void saveAccount(SignupRequestDto dto);
    String userSalt(User user);
    void saveRole(String roleName);
    int addRoleToUser(RoleToUserRequestDto dto);


    void updateRefreshToken(String username, String refreshToken);

    Map<String, String> refresh(String refreshToken);
}
