package com.example.mbminicart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.example.mbminicart",
                "com.example.mbmini.Services",
                "com.example.mbmini.RepoConnections",
                "com.example.mbminicustomer",
                "com.example.mbminiframework.RedisPackage",
                "com.example.mbminiframework.Configs",       // ✅ add this — WebConfig, AuthInterceptor, RedisConfig
                "com.example.mbminiframework.AuthValidation", // ✅ add this — AuthInterceptor
                "com.example.mbminiframework.Auditing"        // ✅ add this — FrameworkAuditConfig
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        com.example.mbminicustomer.Services.CustomerRedisService.class,
                        com.example.mbmini.Services.CatalogRedisService.class,
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
        "com.example.mbminicustomer.Entities",
        "com.example.mbminiframework.Entity"
})
public class MbMiniCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(MbMiniCartApplication.class, args);
    }
}