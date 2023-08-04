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
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();

		log.info("CustomAuthProvider");
		CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(
				username);
		String salt = userDetails.getSalt();
		String userpassword = userDetails.getPassword();
		String inputsaltpw = saltUtil.encodePassword(salt, password); //받아온거
		// PW 검사
		if (!userpassword.equals(inputsaltpw)) {
			throw new BadCredentialsException("Provider - authenticate() : 비밀번호가 일치하지 않습니다.");
		}

		return new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}
