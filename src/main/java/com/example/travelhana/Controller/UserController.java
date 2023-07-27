package com.example.travelhana.Controller;

import com.example.travelhana.Dto.*;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Service.PhoneAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static com.example.travelhana.Config.JwtConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PhoneAuthService phoneAuthService;
    private final HttpSession session;

    //기기 존재 여부 확인
    @GetMapping("/registration/{deviceId}")
    public DeviceDto isExistDevice(@PathVariable("deviceId") String deviceId) {
        return userService.isExistDevice(deviceId);
    }

    //휴대폰 인증코드 전송
    @PostMapping("/verification")
    public SMSResponseDto sendMessagewithRest(@RequestBody PhonenumDto dto) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, UnsupportedEncodingException, URISyntaxException {
        SMSAndCodeDto response= phoneAuthService.sendMessageWithResttemplate(dto.getPhonenum());
        session.setAttribute("code",response.getCode());
        return response.getResponse();
    }

    //휴대폰 인증코드 일치여부 확인
    @PostMapping("/verification/auth")
    public PhonenumResponseDto isSusccessAuth(@RequestBody CodeDto codedto) {
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

    //회원가입
    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequestDto dto) {
        userService.saveAccount(dto);
    }

    @PostMapping("/userrole")
    public ResponseEntity<Integer> addRoleToUser(@RequestBody RoleToUserRequestDto dto) {
        return ResponseEntity.ok(userService.addRoleToUser(dto));
    }

    //refresh token 요청
    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            throw new RuntimeException("JWT Token이 존재하지 않습니다.");
        }
        String refreshToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
        Map<String, String> tokens = userService.refresh(refreshToken);
        response.setHeader(AT_HEADER, tokens.get(AT_HEADER));
        if (tokens.get(RT_HEADER) != null) {
            response.setHeader(RT_HEADER, tokens.get(RT_HEADER));
        }
        return ResponseEntity.ok(tokens);
    }


}
