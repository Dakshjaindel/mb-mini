package com.example.mbminiframework.AuthValidation;

import com.example.mbminiframework.Auditing.AuditorAwareImpl;
import com.example.mbminiframework.Entity.AuthSession;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class AuthInterceptor implements HandlerInterceptor {


    @Autowired
    private RedisMethods redisMethods;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = request.getHeader("AuthKey");
        System.out.println("[DEBUG FOR LOGOUT: TOKEN : "+ token);

        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Missing AuthKey header\"}");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
        }

        AuthSession authSession;
        try {
            authSession = redisMethods.getFromRedis(token, AuthSession.class);

            if (authSession == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                return false;
            }
        } catch (RuntimeException e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return false;
        }

        if (authSession.getAuthKeyExpiresAt().isBefore(LocalDateTime.now())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Token expired, please refresh\"}");
            return false;
        }

        request.setAttribute("userId",authSession.getUserId());

        String userId = authSession.getUserId().toString();
        AuditorAwareImpl.setCurrentUser(userId);

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        AuditorAwareImpl.clear();
    }

}
