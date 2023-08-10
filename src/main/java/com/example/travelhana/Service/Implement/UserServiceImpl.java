package com.example.travelhana.Service.Implement;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.travelhana.Config.JwtConstants;
import com.example.travelhana.Domain.Role;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.DeviceDto;
import com.example.travelhana.Dto.RoleToUserRequestDto;
import com.example.travelhana.Dto.SignupRequestDto;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.RoleRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Service.Implement.CustomUserDetailsImpl;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Util.SaltUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.travelhana.Config.JwtConstants.*;

@Log4j2
@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SaltUtil saltUtil;
    private final JwtConstants jwtConstants;

    
    //==============회원가입=================
    //최초 접속 시 기기 존재 여부 확인
    public ResponseEntity<?> isExistDevice(String deviceId) {
        Boolean isRegistrate;
        String name;
        try{
            User user = userRepository.findByDeviceId(deviceId)
                    .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_USER));
            DeviceDto deviceDto=DeviceDto.builder()
                    .isRegistrate(true)
                    .name(user.getName())
                    .build();
            ApiResponse apiResponse= ApiResponse.builder()
                    .result(deviceDto)
                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build();
            return ResponseEntity.ok(apiResponse);
        }
        catch (BusinessExceptionHandler e)
        {
            ErrorResponse errorResponse=ErrorResponse.builder()
                    .errorCode(ErrorCode.NO_USER.getStatusCode())
                    .errorMessage(ErrorCode.NO_USER.getMessage())
                    .build();
            return ResponseEntity.ok(errorResponse);
        }

    }

    //회원가입 형식 유효성 검사
    public Boolean isValidUser(SignupRequestDto dto) {
        if (dto.getPassword().length() != 6) {
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

    //회원가입 - 계정 저장
    @Override
    public ResponseEntity<?> saveAccount(SignupRequestDto dto) {

        try{
            validateDuplicateUsername(dto);
            if(isValidUser(dto))
            {
                String salt = saltUtil.generateSalt();
                User user = new User().builder()
                        .password(saltUtil.encodePassword(salt, dto.getPassword()))
                        .pattern(saltUtil.encodePassword(salt, dto.getPattern()))
                        .phoneNum(dto.getPhonenum())
                        .deviceId(dto.getDeviceId())
                        .salt(salt)
                        .isWithdrawal(false) //탈퇴했는지
                        .name(dto.getName())
                        .registrationNum(dto.getRegistrationNum())
                        .build();
                userRepository.save(user);
            }

            ApiResponse apiResponse=ApiResponse.builder()
                    .result("save Success")
                    .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                    .resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
                    .build();
            return ResponseEntity.ok(apiResponse);

        }catch (Exception e)
        {
            ErrorResponse errorResponse=ErrorResponse.builder()
                    .errorMessage(e.getMessage())
                    .build();
            return ResponseEntity.ok(errorResponse);
        }

    }

    private void validateDuplicateUsername(SignupRequestDto dto) {
        if (userRepository.existsByDeviceId(dto.getDeviceId())) {
            throw new RuntimeException("이미 존재하는 ID입니다.");
        }
    }

    @Override
    public void saveRole(String roleName) {
        validateDuplicateRoleName(roleName);
        roleRepository.save(new Role(roleName)).getId();
    }

    private void validateDuplicateRoleName(String roleName) {
        if (roleRepository.existsByName(roleName)) {
            throw new RuntimeException("이미 존재하는 Role입니다.");
        }
    }

    @Override
    public int addRoleToUser(RoleToUserRequestDto dto) {
        User user = userRepository.findByDeviceId(dto.getDeviceId()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Role role = roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("ROLE을 찾을 수 없습니다."));
        user.getRoles().add(role);
        return user.getId();
    }

    //토큰에서 deviceId 추출해 User 객체 찾기
    public User getUserByAccessToken(String header)
    {
        String accessToken = header.substring(TOKEN_HEADER_PREFIX.length());

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtConstants.JWT_SECRET)).build();
        DecodedJWT decodedJWT = verifier.verify(accessToken);

        String deviceId = decodedJWT.getSubject();
        User user = userRepository.findByDeviceId(deviceId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return user;

    }

    //==============로그인=================
    //로그인 시 권한 확인
    @Override
    public CustomUserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByDeviceId(username)
                .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService - loadUserByUsername : 사용자를 찾을 수 없습니다."));

        List<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        return new CustomUserDetailsImpl(user.getDeviceId(), user.getPassword(), user.getSalt(), authorities, true, true, true, true);
    }

    //==============토큰발급=================
    //로그인 성공 시 refresh token 발급 후 DB에 저장
    @Override
    public void updateRefreshToken(String deviceId, String refreshToken) {
        User user = userRepository.findByDeviceId(deviceId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.updateRefreshToken(refreshToken);
    }

    //Refresh token 재발급
    @Override
    public Map<String,String> refresh(String refreshToken) {

        //Refresh Token 유효성 검사
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtConstants.JWT_SECRET)).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);

        //Access Token 재발급
        long now = System.currentTimeMillis();
        String username = decodedJWT.getSubject();
        User user = userRepository.findByDeviceId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        if (!user.getRefreshToken().equals(refreshToken)) {
            throw new JWTVerificationException("유효하지 않은 Refresh Token 입니다.");
        }
        String accessToken = JWT.create()
                .withSubject(user.getDeviceId())
                .withExpiresAt(new Date(now + AT_EXP_TIME))
                .withClaim("roles", user.getRoles().stream().map(Role::getName)
                        .collect(Collectors.toList()))
                .sign(Algorithm.HMAC256(jwtConstants.JWT_SECRET));
        Map<String, String> accessTokenResponseMap = new HashMap<>();

        //현재시간과 Refresh Token 만료날짜를 통해 남은 만료기간 계산
        //Refresh Token 만료시간 계산해 1개월 미만일 시 refresh token도 발급
        long refreshExpireTime = decodedJWT.getClaim("exp").asLong() * 1000;
        long diffDays = (refreshExpireTime - now) / 1000 / (24 * 3600);
        long diffMin = (refreshExpireTime - now) / 1000 / 60;
        if (diffMin < 5) {
            String newRefreshToken = JWT.create()
                    .withSubject(user.getDeviceId())
                    .withExpiresAt(new Date(now + RT_EXP_TIME))
                    .sign(Algorithm.HMAC256(jwtConstants.JWT_SECRET));
            accessTokenResponseMap.put(RT_HEADER, newRefreshToken);
            user.updateRefreshToken(newRefreshToken);
        }

        accessTokenResponseMap.put(AT_HEADER, accessToken);
        return accessTokenResponseMap;

    }
    
}
