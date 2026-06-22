package com.example.mbminicart.Repos;

import com.example.mbminicart.Entities.Basket;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BasketRepo extends CrudRepository<Basket,Long> {


    Basket getBasketById(Long basketId);

    Optional<Basket> findByUserId(Long userId);
}
