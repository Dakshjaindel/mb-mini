package com.example.mbminicustomer;

import com.example.mbminicustomer.ConfigsRepo.SessionRepo;
import com.example.mbminiframework.AuthValidation.AuthSessionService;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthSessionServiceImpl implements AuthSessionService {

    private SessionRepo sessionRepo; // ✅ your existing repo

    @Override
    public Optional<String> validateAndGetUserId(String authKey) {
        return sessionRepo.getByAuthKey(authKey) // ✅ your existing method
                .filter(session -> session.getAuthKeyExpiresAt().isAfter(LocalDateTime.now()))
                .map(session -> session.getUserId().toString());
    }
}
