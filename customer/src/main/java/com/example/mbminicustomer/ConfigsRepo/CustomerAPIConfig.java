package com.example.mbminicustomer.ConfigsRepo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CustomerAPIConfig {


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}


