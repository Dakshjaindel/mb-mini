package com.example.mbminicart.Repos;

import com.example.mbminicart.Entities.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BasketRepo extends JpaRepository<Basket,Long> {


    Basket getBasketById(Long basketId);

    Optional<Basket> findByUserId(Long userId);



    @Query("SELECT b FROM Basket b WHERE DATE(b.date) = DATE(:currDate) AND b.flag = :flag")
    List<Basket> findByDateAndFlag(@Param("currDate") Date currDate, @Param("flag") Integer flag);
}
