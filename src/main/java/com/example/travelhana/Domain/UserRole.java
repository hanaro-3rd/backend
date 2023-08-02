package com.example.travelhana.Domain;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;


public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

}