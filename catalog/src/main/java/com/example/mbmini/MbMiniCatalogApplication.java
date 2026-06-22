package com.example.mbmini;

import com.example.mbminiframework.Configs.RedisConfig;
import com.example.mbminiframework.Configs.SecurityConfig;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication(scanBasePackages = {"com.example"})

@Import({RedisMethods.class, RedisConfig.class, SecurityConfig.class})

public class MbMiniCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbMiniCatalogApplication.class, args);
    }

}
