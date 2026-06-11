package com.example.mbminicustomer.ConfigsRepo;


import com.example.mbminicustomer.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


public class CustomerAuditConfig {

    @Bean("customerAuditorAware")
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareImpl();
    }

}
