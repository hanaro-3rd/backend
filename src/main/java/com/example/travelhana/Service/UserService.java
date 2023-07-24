package com.example.travelhana.Service;

import com.example.travelhana.Config.JwtUtil;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Domain.UserRole;
import com.example.travelhana.Dto.*;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Util.SaltUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService{
    private final UserRepository userRepository;
    private final SaltUtil saltUtil;
    private final JwtUtil jwtUtil;
    @Value("${jwt.admin_token}")
    private String ADMIN_TOKEN;


    public DeviceDto isExistDevice(String deviceId) {
        User user = userRepository.findUserByDeviceId(deviceId);

        Boolean isRegistrate;
        String name;
        if (user == null) {
            isRegistrate = false;
            name = "none";
        } else {
            isRegistrate = true;
            name = user.getName();
        }
        return new DeviceDto(isRegistrate, name);

    }

    public List<UserResponseDto> userExist() {
        List<UserResponseDto> dtos = new ArrayList<>();
        List<User> user = userRepository.findAll();
        for (int i = 0; i < user.size(); i++) {
            UserResponseDto dto = new UserResponseDto(user.get(i));
            dtos.add(dto);
        }
        return dtos;
    }

    public void signup(SignupRequestDto dto) {
        if (isValidUser(dto)) {
            String salt = saltUtil.generateSalt();
            User user = new User().builder()
                    .password(saltUtil.encodePassword(salt, dto.getPassword()))
                    .pattern(saltUtil.encodePassword(salt, dto.getPattern()))
                    .phoneNum(dto.getPhonenum())
                    .deviceId(dto.getDeviceId())
                    .salt(salt)
                    .isWithdrawl(false) //탈퇴했는지
                    .name(dto.getName())
                    .role(UserRole.valueOf("USER"))
                    .build();
            userRepository.save(user);
        }

    }

    public Boolean isValidUser(SignupRequestDto dto) {
        if (dto.getPassword().length() != 6) {
            System.out.println(dto.getPassword().length());
            throw new IllegalArgumentException("비밀번호는 6자리의 숫자로 구성해주세요.");
        }
        if (!dto.getPassword().matches("\\d+")) {
            throw new IllegalArgumentException("숫자로만 구성해주세요");
        }
        if (dto.getName().length() > 15) {
            throw new IllegalArgumentException("이름은 15글자 이내로 입력해주세요.");
        }

        return true;
    }

    //유저가 존재하는지 -> 비밀번호가 일치하는지
    public User isExistUser(String deviceId, String inputPassword) {
        User user = userRepository.findUserByDeviceId(deviceId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        String salt=user.getSalt();
        String userpassword=user.getPassword();
        String inputsaltpw=saltUtil.encodePassword(salt,inputPassword);


        if(!userpassword.equals(inputsaltpw))
        {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }



    @Transactional
    public  ResponseEntity<?> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getDeviceId();
        String password = loginRequestDto.getPassword(); //입력받은 비밀번호

        // 사용자 확인
        User user = userRepository.findUserByDeviceId(username);
        String salt=user.getSalt();
        String userpassword=user.getPassword();
        String inputsaltpw=saltUtil.encodePassword(salt,password);

        if (user == null) {
            throw new IllegalArgumentException("등록된 사용자가 없습니다.");
        }
        // 비밀번호 확인
        if (!userpassword.equals(inputsaltpw)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 액세스 토큰과 리프레시 토큰 발급
        String accessToken = jwtUtil.createAccessToken(user.getName(), user.getRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getName(), user.getRole());

        // 액세스 토큰을 HTTP 응답 헤더에 추가
//        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, accessToken);

        // 리프레시 토큰을 HTTP Only 쿠키에 추가 (보안상의 이유로 쿠키에 저장)
        ResponseCookie refreshTokenCookie = ResponseCookie.from(JwtUtil.REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/") // 전체 애플리케이션에서 쿠키 접근 가능하도록 설정
                .maxAge((int) (JwtUtil.REFRESH_TOKEN_EXPIRATION / 1000))// 리프레시 토큰의 만료 시간 설정
                .build();

        return ResponseEntity.ok()
                .headers(headers)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body("success");

    }
}