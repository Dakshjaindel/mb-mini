package com.example.mbminiframework.ORS;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ORSRouteConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
