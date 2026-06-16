package com.example.mbminicustomer.ConfigsRepo;

import com.example.mbminicustomer.Entities.AuthSession;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SessionRepo extends CrudRepository<AuthSession,Long> {
    Optional<AuthSession> findByAuthKey(String authKey);
    Optional<AuthSession> findByRefreshToken(String refreshToken);
}
