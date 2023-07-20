package com.example.travelhana.Dto;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class PostAuthorizationToken extends UsernamePasswordAuthenticationToken {

    // 1.
    private PostAuthorizationToken(
            Object principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(principal, credentials, authorities);
    }

    public static PostAuthorizationToken getTokenFormUserDetails(UserDetails userDetails) {

        return new PostAuthorizationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

    public static PostAuthorizationToken getTokenFormUserDetails(UserDto accountDTO) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(
                new SimpleGrantedAuthority(accountDTO.getRole().toString())
        );

        return new PostAuthorizationToken(
                accountDTO,
                "null password",
                grantedAuthorities
        );
    }

    public UserDetails getUserDetails() {

        return (UserDetails) super.getPrincipal();
    }
}