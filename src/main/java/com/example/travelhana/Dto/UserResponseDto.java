package com.example.travelhana.Dto;

import com.example.travelhana.Domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponseDto {

    private int id;

    private String name;

    private String password;

    private String phoneNum;

    private String pattern;

    private String deviceId;

    private String salt;

    private Boolean isWithdrawl; //디폴트 =false

    public UserResponseDto(User user)
    {
        this.id=user.getId();
        this.deviceId=user.getDeviceId();
        this.isWithdrawl=user.getIsWithdrawal();
        this.name= user.getName();
        this.password=user.getPassword();
        this.pattern= user.getPattern();
        this.phoneNum=user.getPhoneNum();
        this.salt= user.getSalt();
    }
}
