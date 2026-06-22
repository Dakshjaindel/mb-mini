package com.example.mbminiframework.Auditing;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuditFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            String userId = httpRequest.getHeader("X-User-Id");
            if (userId == null || userId.isBlank()) {
                userId = "system";
            }

            AuditorAwareImpl.setCurrentUser(userId);
            chain.doFilter(request, response);
        } finally {
            AuditorAwareImpl.clear();
        }
    }
}
