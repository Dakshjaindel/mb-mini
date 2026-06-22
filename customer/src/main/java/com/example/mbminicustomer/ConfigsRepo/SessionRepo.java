package com.example.mbminicustomer.ConfigsRepo;

import com.example.mbminiframework.Entity.AuthSession;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SessionRepo extends CrudRepository<AuthSession,Long> {
    Optional<AuthSession> findByAuthKey(String authKey);
    Optional<AuthSession> findByRefreshToken(String refreshToken);

    Optional<AuthSession> getByAuthKey(String authKey);
}
