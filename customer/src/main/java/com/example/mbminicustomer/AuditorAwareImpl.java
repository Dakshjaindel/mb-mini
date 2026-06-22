package com.example.mbminicustomer;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    public static final ThreadLocal<String> currentUser= new ThreadLocal<>();

    public static void setCurrentUser(String userId) {
        currentUser.set(userId);
    }



    public static void clear() {
        currentUser.remove();
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(currentUser.get());
    }
}
