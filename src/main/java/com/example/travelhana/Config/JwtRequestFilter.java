package com.example.travelhana.Config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtRequestFilter extends BasicAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String jwtToken = jwtUtil.resolveToken(request);

        if (StringUtils.hasText(jwtToken) && jwtUtil.validateToken(jwtToken)) {
            String username = jwtUtil.getUserInfoFromToken(jwtToken).getSubject();
            if (StringUtils.hasText(username)) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (UsernameNotFoundException e) {
                    // 유저가 존재하지 않을 경우 처리
                }
            }
        }

        chain.doFilter(request, response);
    }
}
