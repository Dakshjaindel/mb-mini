package com.example.mbmini;

import com.example.mbminiframework.Configs.RedisConfig;
import com.example.mbminiframework.Configs.SecurityConfig;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication(scanBasePackages = {"com.example"})
@EnableJpaRepositories(basePackages = {
        "com.example.mbmini"   })
@EntityScan(basePackages = {
        "com.example.mbmini"
})

@Import({RedisMethods.class, RedisConfig.class, SecurityConfig.class})

public class MbMiniCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbMiniCatalogApplication.class, args);
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
