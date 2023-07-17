package com.example.travelhana;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class) //스프링 시큐리티 기능 제거
public class TravelhanaApplicationTests {

    @Test
    void contextLoads() {
    }

}
