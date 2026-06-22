package com.example.mbminiframework.AuthValidation;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface AuthSessionService {
    Optional<String> validateAndGetUserId(String authKey);
}
