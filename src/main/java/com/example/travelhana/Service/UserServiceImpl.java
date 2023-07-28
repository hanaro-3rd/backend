package com.example.travelhana.Service;

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
import com.example.travelhana.Dto.UserResponseDto;
import com.example.travelhana.Repository.RoleRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Util.SaltUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.travelhana.Config.JwtConstants.*;
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService{


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SaltUtil saltUtil;
    private final JwtConstants jwtConstants;

    public DeviceDto isExistDevice(String deviceId) {
        User user = userRepository.findByDeviceId(deviceId)
             .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService - loadUserByUsername : 사용자를 찾을 수 없습니다."));


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

    public Boolean isValidUser(SignupRequestDto dto) {
        if (dto.getPassword().length() != 6) {
            log.info(dto.getPassword().length());
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


    @Override
    public CustomUserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByDeviceId(username)
             .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService - loadUserByUsername : 사용자를 찾을 수 없습니다."));

        List<SimpleGrantedAuthority> authorities = user.getRoles()
             .stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        return new CustomUserDetailsImpl(user.getDeviceId(), user.getPassword(), user.getSalt(), authorities,true,true,true,true);
    }
    @Override
    public void saveAccount(SignupRequestDto dto) {
        validateDuplicateUsername(dto);
        String salt = saltUtil.generateSalt();
        log.info("회원가입 비밀번호: "+dto.getPassword());
        User user = new User().builder()
             .password(saltUtil.encodePassword(salt, dto.getPassword()))
             .pattern(saltUtil.encodePassword(salt, dto.getPattern()))
             .phoneNum(dto.getPhonenum())
             .deviceId(dto.getDeviceId())
             .salt(salt)
             .isWithdrawl(false) //탈퇴했는지
             .name(dto.getName())
             .registrationNum(dto.getRegistrationNum())
             .build();
        userRepository.save(user);
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

    //토큰발급
    @Override
    public void updateRefreshToken(String deviceId, String refreshToken) {
        User user = userRepository.findByDeviceId(deviceId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.updateRefreshToken(refreshToken);
    }

    @Override
    public Map<String, String> refresh(String refreshToken) {

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
