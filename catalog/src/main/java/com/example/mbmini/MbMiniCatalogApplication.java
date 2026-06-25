package com.example.mbmini;

import com.example.mbminiframework.Configs.RedisConfig;
import com.example.mbminiframework.Configs.SecurityConfig;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {"com.example"})
@EnableJpaRepositories(basePackages = {
        "com.example.mbmini",            // catalog's own repos
        "com.example.mbminishared"       // shared repos (ItemRepo lives here)
})
@EntityScan(basePackages = {
        "com.example.mbmini",            // catalog's own entities
        "com.example.mbminishared"       // shared entities (BasketItem lives here)
})

@Import({RedisMethods.class, RedisConfig.class, SecurityConfig.class})

public class MbMiniCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbMiniCatalogApplication.class, args);
    }

}
