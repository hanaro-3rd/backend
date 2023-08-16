package com.example.travelhana.Service.implement;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.travelhana.Config.JwtConstants;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.Role;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.Account.AccountDummyDto;
import com.example.travelhana.Dto.Authentication.DeviceDto;
import com.example.travelhana.Dto.Authentication.RoleToUserRequestDto;
import com.example.travelhana.Dto.Authentication.SignupRequestDto;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.RoleRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Util.SaltUtil;
import com.example.travelhana.Util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
	private final CryptoUtil cryptoUtil;
	private final ExternalAccountRepository externalAccountRepository;

	//==============회원가입=================
	//최초 접속 시 기기 존재 여부 확인
	public ResponseEntity<?> isExistDevice(String deviceId) {
		Boolean isRegistrate;
		String name;
		try {
			User user = userRepository.findByDeviceId(deviceId)
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_USER));
			DeviceDto deviceDto = DeviceDto.builder()
					.isRegistrate(true)
					.name(user.getName())
					.build();
			ApiResponse apiResponse = ApiResponse.builder()
					.result(deviceDto)
					.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
					.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
					.build();
			return ResponseEntity.ok(apiResponse);
		} catch (BusinessExceptionHandler e) {
			ErrorResponse errorResponse = ErrorResponse.builder()
					.errorCode(ErrorCode.NO_USER.getStatusCode())
					.errorMessage(ErrorCode.NO_USER.getMessage())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


	//회원가입 형식 유효성 검사
	public void isValidUser(SignupRequestDto dto) {
		if (dto.getPassword().length() != 6) {
			throw new IllegalArgumentException("비밀번호는 6자리의 숫자로 구성해주세요.");
		}
		if (!dto.getPassword().matches("\\d+")) {
			throw new IllegalArgumentException("비밀번호는 숫자로만 구성해주세요");
		}
		if (dto.getName().length() > 15) {
			throw new IllegalArgumentException("이름은 15글자 이내로 입력해주세요.");
		}

	}

	private void createDummyExternalAccounts(AccountDummyDto accountDummyDto) throws Exception {
		Random random = new Random();

		// 입력한 정보와 랜덤값으로 더미 외부 계좌 정보 생성
		String accountPassword = accountDummyDto.getAccountPassword();
		List<String> banks = Arrays.asList("한국", "스타", "하늘", "바람", "노을", "여름");

		for (int i = 0; i < 5; i++) {
			String accountSalt = saltUtil.generateSalt();

			String group1 = String.format("%03d", random.nextInt(1000));
			String group2 = String.format("%04d", random.nextInt(10000));
			String group3 = String.format("%04d", random.nextInt(10000));

			String accountNum = group1 + "-" + group2 + "-" + group3;

			ExternalAccount externalAccount = ExternalAccount
					.builder()
					.accountNum(cryptoUtil.encrypt(accountNum))
					.bank(banks.get(random.nextInt(banks.size())))
					.openDate(java.sql.Date.valueOf(LocalDate.now().minusDays(random.nextInt(365))))
					.salt(accountSalt)
					.password(saltUtil.encodePassword(accountSalt, accountPassword))
					.registrationNum(accountDummyDto.getRegistrationNum())
					.balance(1000000L)
					.build();
			externalAccountRepository.save(externalAccount);
		}

	}

	//회원가입 - 계정 저장
	@Override
	public ResponseEntity<?> saveAccount(SignupRequestDto dto) {
		try {
			validateDuplicateUsername(dto);
			isValidUser(dto);
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
			User saveuser = userRepository.save(user);
			AccountDummyDto accountDummyDto = AccountDummyDto
					.builder()
					.userId(saveuser.getId())
					.accountPassword("1234")
					.registrationNum(dto.getRegistrationNum())
					.build();
			createDummyExternalAccounts(accountDummyDto);

			ApiResponse apiResponse = ApiResponse.builder()
					.result("signup success")
					.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
					.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
					.build();
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ErrorResponse errorResponse = ErrorResponse.builder()
					.errorCode(400)
					.errorMessage(e.getMessage())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
		User user = userRepository.findByDeviceId(dto.getDeviceId())
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
		Role role = roleRepository.findById(dto.getRoleId())
				.orElseThrow(() -> new RuntimeException("ROLE을 찾을 수 없습니다."));
		user.getRoles().add(role);
		return user.getId();
	}

	//토큰에서 deviceId 추출해 User 객체 찾기
	public User getUserByAccessToken(String header) {
		String accessToken = header.substring(TOKEN_HEADER_PREFIX.length());

		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtConstants.JWT_SECRET)).build();
		DecodedJWT decodedJWT = verifier.verify(accessToken);

		String deviceId = decodedJWT.getSubject();
		User user = userRepository.findByDeviceId(deviceId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
		return user;

	}

	//==============로그인=================
	//로그인 시 권한 확인
	@Override
	public CustomUserDetailsImpl loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user = userRepository.findByDeviceId(username)
				.orElseThrow(() -> new UsernameNotFoundException(
						"UserDetailsService - loadUserByUsername : 사용자를 찾을 수 없습니다."));

		List<SimpleGrantedAuthority> authorities = user.getRoles()
				.stream().map(role -> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList());

		return new CustomUserDetailsImpl(user.getDeviceId(), user.getPassword(), user.getSalt(),
				authorities, true, true, true, true);
	}

	//==============토큰발급=================
	//로그인 성공 시 refresh token 발급 후 DB에 저장
	@Override
	public void updateRefreshToken(String deviceId, String refreshToken) {
		User user = userRepository.findByDeviceId(deviceId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
		user.updateRefreshToken(refreshToken);
	}

	//Refresh token 재발급
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