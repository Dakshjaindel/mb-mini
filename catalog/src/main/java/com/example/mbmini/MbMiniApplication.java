package com.example.mbmini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication


public class MbMiniApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbMiniApplication.class, args);
    }

}
