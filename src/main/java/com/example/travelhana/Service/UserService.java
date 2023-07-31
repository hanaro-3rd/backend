package com.example.travelhana.Service;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.*;

import java.util.Map;

public interface UserService {

    DeviceDto isExistDevice(String deviceId);

    void saveAccount(SignupRequestDto dto);

    void saveRole(String roleName);

    int addRoleToUser(RoleToUserRequestDto dto);

    void updateRefreshToken(String username, String refreshToken);

    Map<String, String> refresh(String refreshToken);
    User getUser(String header);
}
