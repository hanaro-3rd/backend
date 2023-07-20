package com.example.travelhana.Controller;

import com.example.travelhana.Config.JwtUtil;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.*;
import com.example.travelhana.Service.PhoneAuthService;
import com.example.travelhana.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    private final HttpSession session;
    private final JwtUtil jwtUtil;

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
        SMSAndCodeDto response= phoneAuthService.sendMessageWithResttemplate(dto.getPhonenum());
        session.setAttribute("code",response.getCode());
        return response.getResponse();
    }

    @PostMapping("/verification/auth")
    public PhonenumResponseDto isSusccessAuth(@RequestBody CodeDto codedto)
    {
        String code= (String) session.getAttribute("code");
        if(codedto.getCode().equals(code))
        {
            session.removeAttribute("code");
            return PhonenumResponseDto.builder()
                    .statusCode("200")
                    .statusMessage("Success")
                    .build();
        }
        else
        {
            session.removeAttribute("code");
            return PhonenumResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Fail")
                    .build();
        }
    }

    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequestDto dto)
    {
        userService.signup(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        return userService.login(loginRequest,response);
    }

}
