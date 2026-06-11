package com.example.mbminicart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.mbminicart","com.example.mbminicustomer","com.example.mbmini"})
public class MbMiniCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbMiniCartApplication.class, args);
    }

}
