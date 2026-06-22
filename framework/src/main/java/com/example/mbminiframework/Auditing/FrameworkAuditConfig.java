package com.example.mbminiframework.Auditing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class FrameworkAuditConfig {

    @Bean("auditorAwareImpl")
    public AuditorAware<String> auditorAwareImpl() {
        return new AuditorAwareImpl();
    }
}


