package com.example.travelhana.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (request.getContentType() == null || !request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            // If the content type is not JSON, fallback to the default behavior
            System.out.println("CustomAuthenticationFilter 걸림");
            return super.attemptAuthentication(request, response);
        }

        try {
            // Parse the JSON request body
            JsonAuthRequest authRequest = objectMapper.readValue(request.getReader(), JsonAuthRequest.class);

            System.out.println("CustomAuthenticationFilter 유저네임"+authRequest.getDeviceId());

            // Create an authentication token with the parsed values
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                 authRequest.getDeviceId(),
                 authRequest.getPassword()
            );

            // Delegate the authentication to the AuthenticationManager
            return authenticationManager.authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class JsonAuthRequest {
        private String deviceId;
        private String password;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        // Getters and setters for username and password
    }
}
