package com.example.mbminicart;

import com.example.mbmini.Entities.Catalog;
import com.example.mbminiframework.Configs.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.example.mbminicart","com.example.mbminicustomer","com.example.mbmini"})
@Import({Catalog.class, SecurityConfig.class})
public class MbMiniCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbMiniCartApplication.class, args);
    }

}
