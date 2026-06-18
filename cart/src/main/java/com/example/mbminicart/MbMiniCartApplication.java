package com.example.mbminicart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.example.mbminicart",
                "com.example.mbmini.Services",
                "com.example.mbmini.RepoConnections",
                "com.example.mbminicustomer",
                "com.example.mbminiframework.RedisPackage"
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        com.example.mbminicustomer.ConfigsRepo.CustomerAuditConfig.class,
                        com.example.mbminicustomer.ConfigsRepo.CustomerRedisConfig.class,
                        com.example.mbminicustomer.Services.CustomerRedisService.class,
                        com.example.mbmini.RepoConnections.AuditConfig.class,
                        com.example.mbmini.RepoConnections.RedisConfig.class,
                        com.example.mbmini.Services.CatalogRedisService.class,
                        com.example.mbmini.RepoConnections.SecurityConfig.class
                })
        }
)
@EnableJpaRepositories(basePackages = {
        "com.example.mbminicart.Repos",
        "com.example.mbmini.RepoConnections",
        "com.example.mbminicustomer.ConfigsRepo"
})
@EntityScan(basePackages = {
        "com.example.mbminicart.Entities",
        "com.example.mbmini.Entities",
        "com.example.mbminicustomer.Entities"
})
@Import({com.example.mbminiframework.Configs.SecurityConfig.class, com.example.mbminiframework.RedisPackage.RedisMethods.class})
public class MbMiniCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(MbMiniCartApplication.class, args);
    }
}