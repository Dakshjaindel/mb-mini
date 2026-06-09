package com.example.mbminicustomer;

import org.springframework.data.repository.CrudRepository;

public interface SessionRepo extends CrudRepository<AuthSession,Long> {
}
