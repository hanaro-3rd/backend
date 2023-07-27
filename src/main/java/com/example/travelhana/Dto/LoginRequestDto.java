package com.example.travelhana.Dto;

import com.example.travelhana.Domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    private String deviceId;
    private String password;
    public User toEntity() {
        return User.builder()
             .deviceId(deviceId)
             .password(password)
             .build();
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
    // Getter, Setter, 생성자...
}

