package com.example.travelhana;

import com.example.travelhana.Service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@SpringBootApplication
@EnableScheduling
@EnableCaching
@RequiredArgsConstructor
public class TravelhanaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelhanaApplication.class, args);
    }


}