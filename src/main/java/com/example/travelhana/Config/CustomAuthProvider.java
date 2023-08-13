package com.example.travelhana.Config;

import com.example.travelhana.Service.CustomUserDetails;
import com.example.travelhana.Util.SaltUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class CustomAuthProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;
	private final SaltUtil saltUtil;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		log.info("CustomAuthProvider");

		String username = authentication.getName();
		String inputpassword = (String) authentication.getCredentials();
		String isPassword = (String) authentication.getDetails();


		CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(
				username);
		String salt = userDetails.getSalt();
		String userpattern = userDetails.getPattern();
		String userpassword = userDetails.getPassword();


		if (isPassword.equals("password")) {
			String inputsaltpw = saltUtil.encodePassword(salt, inputpassword); //받아온거
			// 비밀번호 검사
			if (!userpassword.equals(inputsaltpw)) {
				throw new BadCredentialsException("Provider - authenticate() : 비밀번호가 일치하지 않습니다.");
			}
		} else {
			String inputsaltpat = saltUtil.encodePassword(salt, inputpassword); //받아온거
			// 패턴 검사
			if (!userpattern.equals(inputsaltpat)) {
				throw new BadCredentialsException("Provider - authenticate() : 패턴이 일치하지 않습니다.");
			}
		}

		return new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}

