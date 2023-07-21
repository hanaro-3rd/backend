//package com.example.travelhana.Config;
//
//import com.example.travelhana.Service.UserService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    private final JwtUtil jwtUtil;
//    private final UserService userService;
//    private final AuthenticationManager authenticationManager;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public WebSecurityConfig(JwtUtil jwtUtil, UserService userService, AuthenticationManager authenticationManager, JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtUtil = jwtUtil;
//        this.userService = userService;
//        this.authenticationManager = authenticationManager;
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        // 해당 경로들은 보안 필터를 적용하지 않음
//        web.ignoring()
//                .antMatchers("/login") // 로그인 API
//                .antMatchers("/signup"); // 회원가입 API
//    }
//
//    // AuthenticationManager 빈 등록
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//    }
//}
//
