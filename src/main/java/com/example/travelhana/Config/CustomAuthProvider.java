package com.example.travelhana.Config;

import com.example.travelhana.Service.CustomUserDetails;
import com.example.travelhana.Service.UserService;
import com.example.travelhana.Util.SaltUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final SaltUtil saltUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        System.out.println("input 받은 비밀번호 ");
        System.out.println(password);
        System.out.println(StringUtils.containsWhitespace(password));
        System.out.println("CustomAuthProvider authentication 이름:"+username);
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        String salt=userDetails.getSalt();
        System.out.println("솔트 값"+salt);
        String userpassword=userDetails.getPassword();
        String inputsaltpw=saltUtil.encodePassword(salt,password); //받아온거
        // PW 검사
        System.out.println("CustomAuthProvider userpassword " +userpassword);
        System.out.println("CustomAuthProvider inputsaltpw  " +inputsaltpw);


        if (!userpassword.equals(inputsaltpw)) {
            throw new BadCredentialsException("Provider - authenticate() : 비밀번호가 일치하지 않습니다.");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
