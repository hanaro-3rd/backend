package com.example.travelhana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//@SpringBootApplication(exclude = SecurityAutoConfiguration.class) //스프링 시큐리티 기능 제거
@SpringBootApplication
public class TravelhanaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelhanaApplication.class, args);
    }

}
