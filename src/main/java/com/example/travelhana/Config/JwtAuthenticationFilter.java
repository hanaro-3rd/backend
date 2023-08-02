//package com.example.travelhana.Config;
//
//
//import com.example.travelhana.Config.JwtUtil;
//import com.example.travelhana.Service.UserService;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final JwtUtil jwtUtil;
//    private final UserService userService;
//    private final AuthenticationManager authenticationManager;
//
//    @Autowired
//    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService, AuthenticationManager authenticationManager) {
//        this.jwtUtil = jwtUtil;
//        this.userService = userService;
//        this.authenticationManager = authenticationManager;
//        setFilterProcessesUrl("/login");
//    }
//
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
//            throws AuthenticationException {
//        String token = jwtUtil.resolveToken(request);
//
//        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
//            Authentication authentication = jwtUtil.getAuthentication(token);
//            return getAuthenticationManager().authenticate(authentication);
//        }
//
//        // 인증에 실패한 경우, null을 리턴
//        return null;
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
//                                            Authentication authResult) throws IOException, ServletException {
//        SecurityContextHolder.getContext().setAuthentication(authResult);
//        chain.doFilter(request, response);
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                              AuthenticationException failed) throws IOException, ServletException {
//        SecurityContextHolder.clearContext();
//        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
//    }
//}
