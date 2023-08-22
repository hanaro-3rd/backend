package com.example.travelhana.Service.implement;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.travelhana.Config.JwtConstants;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.Role;
import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Account.AccountDummyDto;
import com.example.travelhana.Dto.Authentication.*;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.RoleRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Util.CryptoUtil;
import com.example.travelhana.Util.SaltUtil;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
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
		try {
			Users users = userRepository.findByDeviceId(deviceId)
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_USER));
			DeviceDto deviceDto = DeviceDto.builder()
					.isRegistrate(true)
					.name(users.getName())
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
					.phoneNum(accountDummyDto.getPhoneNum())
					.balance(1000000L)
					.build();
			externalAccountRepository.save(externalAccount);
		}
	}

	//회원가입 - 계정 저장
	@Override
	public ResponseEntity<?> saveAccount(SignupRequestDto dto) throws Exception {
		if (validateDuplicateUsername(dto.getRegistrationNum()).isPresent()) {
			throw new RuntimeException("이미 존재하는 유저입니다.");
		}
		isValidUser(dto);
		String salt = saltUtil.generateSalt();
		Users users = new Users().builder()
				.password(saltUtil.encodePassword(salt, dto.getPassword()))
				.pattern(saltUtil.encodePassword(salt, dto.getPattern()))
				.phoneNum(dto.getPhonenum())
				.deviceId(dto.getDeviceId())
				.salt(salt)
				.name(dto.getName())
				.registrationNum(dto.getRegistrationNum())
				.build();
		userRepository.save(users);

		AccountDummyDto accountDummyDto = AccountDummyDto
				.builder()
				.userId(users.getId())
				.accountPassword("1234")
				.registrationNum(dto.getRegistrationNum())
				.phoneNum(dto.getPhonenum())
				.build();
		createDummyExternalAccounts(accountDummyDto);

		ApiResponse apiResponse = ApiResponse.builder()
				.result(users.getName())
				.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
				.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
				.build();
		return ResponseEntity.ok(apiResponse);
	}

	//회원가입 형식 유효성 검사
	public void isValidUser(SignupRequestDto dto) {
		if (dto.getPassword().length() != 6) {
			throw new IllegalArgumentException("비밀번호는 6자리의 숫자로 구성해주세요.");
		}
		if (!dto.getPassword().matches("\\d+")) {
			throw new IllegalArgumentException("숫자로만 구성해주세요");
		}
		if (dto.getName().length() > 15) {
			throw new IllegalArgumentException("이름은 15글자 이내로 입력해주세요.");
		}
		if (dto.getRegistrationNum().length() != 7) {
			throw new IllegalArgumentException("주민번호는 생년월일 6자리+성별 1자리 총 7자리로 구성해주세요.");
		}
		if (Integer.parseInt(dto.getRegistrationNum().substring(2, 4)) > 12) {
			throw new IllegalArgumentException("탄생 월은 1월~12월에서 선택해주세요.");
		}
		if (Integer.parseInt(dto.getRegistrationNum().substring(4, 6)) > 31) {
			throw new IllegalArgumentException("탄생 일은 1일~31일에서 선택해주세요.");
		}
		char lastChar = dto.getRegistrationNum().charAt(dto.getRegistrationNum().length() - 1);
		if (lastChar != '1' && lastChar != '2' && lastChar != '3' && lastChar != '4') {
			throw new IllegalArgumentException("뒷자리는 1,2,3,4 중 하나로 입력해주세요.");
		}
	}

	public Optional<Users> validateDuplicateUsername(String phoneNum) {
		Optional<Users> userOptional = userRepository.findByPhoneNum(phoneNum);
		Users users = userOptional.orElse(null);
		return Optional.ofNullable(users);
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
		Users users = userRepository.findByDeviceId(dto.getDeviceId())
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
		Role role = roleRepository.findById(dto.getRoleId())
				.orElseThrow(() -> new RuntimeException("ROLE을 찾을 수 없습니다."));
		users.getRoles().add(role);
		return users.getId();
	}

	//토큰에서 deviceId 추출해 User 객체 찾기
	public Users getUserByAccessToken(String header) {
		String accessToken = header.substring(TOKEN_HEADER_PREFIX.length());

		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtConstants.JWT_SECRET)).build();
		DecodedJWT decodedJWT = verifier.verify(accessToken);

		String deviceId = decodedJWT.getSubject();
		Users users = userRepository.findByDeviceId(deviceId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
		return users;
	}

	//==============로그인=================
	//로그인 시 권한 확인
	@Override
	public CustomUserDetailsImpl loadUserByUsername(String username)
			throws UsernameNotFoundException {
		Users users = userRepository.findByDeviceId(username)
				.orElseThrow(() -> new UsernameNotFoundException(
						"UserDetailsService - loadUserByUsername : 사용자를 찾을 수 없습니다."));

		List<SimpleGrantedAuthority> authorities = users.getRoles()
				.stream().map(role -> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList());

		return new CustomUserDetailsImpl(users.getDeviceId(), users.getPassword(), users.getPattern(), users.getSalt(),
				authorities, true, true, true, true);
	}

	//==============비밀번호 찾기==============
	//휴대폰 인증이 끝난다음 진행할 로직
	private void findPassword(UpdatePasswordDto dto) {
		//디바이스 아이디로 조회
		Users users = userRepository.findByDeviceId(dto.getDeviceId())
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_USER));
		//주민번호가 일치하지 않을 때
		if (!dto.getRegistrateNum().equals(users.getRegistrationNum())) {
			throw new BusinessExceptionHandler(ErrorCode.NO_USER);
		}
		//이름이 일치하지 않을 때
		if (!dto.getName().equals(users.getName())) {
			throw new BusinessExceptionHandler(ErrorCode.NO_USER);
		}
	}

	@Transactional
	public ResponseEntity<?> updatePassword(UpdatePasswordDto dto) {
		findPassword(dto);
		Users users = userRepository.findByDeviceId(dto.getDeviceId())
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NO_USER));

		if (dto.getNewPassword().length() != 6) {
			throw new IllegalArgumentException("비밀번호는 6자리의 숫자로 구성해주세요.");
		}
		if (!dto.getNewPassword().matches("\\d+")) {
			throw new IllegalArgumentException("숫자로만 구성해주세요");
		}
		if (saltUtil.encodePassword(users.getSalt(), dto.getNewPassword()).equals(users.getPassword())) {
			throw new IllegalArgumentException("이전 비밀번호와 다른 비밀번호로 설정해주세요.");
		}

		String newPassword = saltUtil.encodePassword(users.getSalt(), dto.getNewPassword());
		users.updatePassword(newPassword);
		ApiResponse apiResponse = ApiResponse.builder()
				.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
				.resultCode(SuccessCode.OPEN_API_SUCCESS.getStatusCode())
				.result("success")
				.build();
		return new ResponseEntity(apiResponse, HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<?> updateDevice(UpdateDeviceRequestDto dto) {
		Optional<Users> user = validateDuplicateUsername(dto.getPhonenum());
		if (user.isEmpty()) {
			throw new BusinessExceptionHandler(ErrorCode.NO_USER);
		}
		user.get().updateDeviceId(dto.getNewDeviceId());
		ApiResponse apiResponse = ApiResponse.builder()
				.result(user.get().getDeviceId())
				.resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
	}

	//==============토큰발급=================
	//로그인 성공 시 refresh token 발급 후 DB에 저장
	@Override
	public void updateRefreshToken(String deviceId, String refreshToken) {
		Users users = userRepository.findByDeviceId(deviceId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
		users.updateRefreshToken(refreshToken);
	}

	//Refresh token 재발급
	@Transactional
	public ResponseEntity<?> refresh(String refreshToken) {

		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtConstants.JWT_SECRET)).build();
		DecodedJWT decodedJWT = verifier.verify(refreshToken);
		String deviceId = decodedJWT.getSubject();
		Users users = userRepository.findByDeviceId(deviceId)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		long now = System.currentTimeMillis();
		try {
			if (!users.getRefreshToken().equals(refreshToken)) {
				users.updateRefreshToken(null);
				throw new JWTVerificationException("유효하지 않은 Refresh Token 입니다. Refresh Token을 삭제합니다. 재로그인하세요.");
			}
			String newaccessToken = JWT.create()
					.withSubject(users.getDeviceId())
					.withExpiresAt(new Date(now + AT_EXP_TIME))
					.withClaim("roles", users.getRoles().stream().map(Role::getName)
							.collect(Collectors.toList()))
					.sign(Algorithm.HMAC256(jwtConstants.JWT_SECRET));

			String newRefreshToken = JWT.create()
					.withSubject(users.getDeviceId())
					.withExpiresAt(new Date(now + RT_EXP_TIME))
					.sign(Algorithm.HMAC256(jwtConstants.JWT_SECRET));
			users.updateRefreshToken(newRefreshToken);

			HttpHeaders headers = new HttpHeaders();
			headers.add(AT_HEADER, newaccessToken);
			headers.add(RT_HEADER, newRefreshToken);

			ApiResponse apiResponse = ApiResponse.builder()
					.result("success")
					.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
					.resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
					.build();
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.headers(headers)
					.body(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = ApiResponse.builder()
					.result("success")
					.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
					.resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
					.build();
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(apiResponse);
		}
	}

}