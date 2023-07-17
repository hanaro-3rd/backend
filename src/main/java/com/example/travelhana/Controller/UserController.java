package com.example.travelhana.Controller;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.DeviceDto;
import com.example.travelhana.Dto.DeviceIdResponseDto;
import com.example.travelhana.Dto.PhonenumDto;
import com.example.travelhana.Dto.UserResponseDto;
import com.example.travelhana.Service.PhoneAuthService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PhoneAuthService phoneAuthService;

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

    @PostMapping("/phoneauth")
    public void sendMessage(@RequestBody PhonenumDto dto)
    {
        phoneAuthService.sendSMS(dto.getPhonenum());
    }
}
