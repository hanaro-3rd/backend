package com.example.travelhana.Util;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Configuration
public class SaltUtil {

    public String encodePassword(String salt, String password) {
        return BCrypt.hashpw(password, salt);
    }

    public String generateSalt() {
        return BCrypt.gensalt();
    }

}