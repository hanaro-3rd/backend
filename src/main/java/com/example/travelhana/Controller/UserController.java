package com.example.travelhana.Controller;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.DeviceDto;
import com.example.travelhana.Dto.DeviceIdResponseDto;
import com.example.travelhana.Dto.UserResponseDto;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/registration/{deviceId}")
    public DeviceDto isExistDevice(@PathVariable("deviceId") String deviceId)
    {
        return userService.isExistDevice(deviceId);
    }

    @GetMapping("/user")
    public List<UserResponseDto> userIdexists()
    {
        return userService.userExist();
    }
}
