package com.example.travelhana.Controller;

import com.example.travelhana.Dto.DeviceDto;
import com.example.travelhana.Dto.PhonenumDto;
import com.example.travelhana.Dto.SMSResponseDto;
import com.example.travelhana.Dto.UserResponseDto;
import com.example.travelhana.Service.PhoneAuthService;
import com.example.travelhana.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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


    @PostMapping("/verification")
    public SMSResponseDto sendMessagewithRest(@RequestBody PhonenumDto dto) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, UnsupportedEncodingException, URISyntaxException {
        return phoneAuthService.sendMessageWithResttemplate(dto.getPhonenum());
    }





}
