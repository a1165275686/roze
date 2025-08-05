package com.allure.rap.roze;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.allure.rap.roze.mapper")
public class RozeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RozeApplication.class, args);
    }

}
