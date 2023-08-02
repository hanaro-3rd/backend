package com.example.travelhana.Dto;


import com.example.travelhana.Domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class PreAuthorizationToken extends UsernamePasswordAuthenticationToken {

    private PreAuthorizationToken(String deviceId, String password) {
        super(deviceId, password);
    }

    public PreAuthorizationToken(UserDto dto) {
        this(dto.getDeviceId(), dto.getPassword());
    }

    public String getUsername() {
        return (String) super.getPrincipal();
    }

    public String getUserPassword() {
        return (String) super.getCredentials();
    }
}