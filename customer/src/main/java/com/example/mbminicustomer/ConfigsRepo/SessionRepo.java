package com.example.mbminicustomer.ConfigsRepo;

import com.example.mbminicustomer.Entities.AuthSession;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepo extends CrudRepository<AuthSession,Long> {
}
