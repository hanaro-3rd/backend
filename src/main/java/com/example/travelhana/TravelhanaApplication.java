package com.example.travelhana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class) //스프링 시큐리티 기능 제거
@EnableScheduling
public class TravelhanaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelhanaApplication.class, args);
    }

}
