package com.example.mbmini;

import com.example.mbminiframework.Configs.RedisConfig;
import com.example.mbminiframework.Configs.SecurityConfig;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication

@Import({RedisMethods.class, RedisConfig.class, SecurityConfig.class})

public class MbMiniApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbMiniApplication.class, args);
    }

}
