package com.example.travelhana.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private int id;
    private String name;
    private String password;
    private String registrationNum;
    private String phoneNum;
    private String pattern;
    private Boolean isWithdrawl; //디폴트 =false
    private Role role;
    private String deviceId;
    private String salt;
    private Collection<? extends GrantedAuthority> authorities;


    @Builder
    public UserDetailsImpl(int id, String name, String password, String deviceId, String registrationNum, String phoneNum, Boolean isWithdrawl, String salt, Role role, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.deviceId = deviceId;
        this.password = password;
        this.registrationNum=registrationNum;
        this.phoneNum=phoneNum;
        this.isWithdrawl=isWithdrawl;
        this.salt=salt;
        this.role = role;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return deviceId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



}
