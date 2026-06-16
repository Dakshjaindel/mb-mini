package com.example.mbminicustomer;

import com.example.mbminiframework.Configs.RedisConfig;
import com.example.mbminiframework.Configs.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RedisConfig.class, SecurityConfig.class})
public class MbMiniCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbMiniCustomerApplication.class, args);
    }

}
