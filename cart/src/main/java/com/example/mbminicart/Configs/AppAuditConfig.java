package com.example.mbminicart.Configs;


import com.example.mbminicustomer.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "cartAuditorAware")
public class AppAuditConfig {

    @Bean("customerAuditorAware")
    public AuditorAware<String> customerAuditorAware(){
        return new AuditorAwareImpl();
    }

    @Bean("catalogAuditorAware")
    public AuditorAware<String> catalogAuditorAware(){
        return () -> Optional.of("System");
    }

    @Bean("cartAuditorAware")
    public AuditorAware<String> cartAuditorAware() {
        return () -> Optional.of("System");
    }

}
