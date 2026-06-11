package com.example.mbminicart.Repos;

import com.example.mbminicart.Entities.BasketItem;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ItemRepo extends CrudRepository<BasketItem,Long> {


    BasketItem getBasketItemByBasketId(Long basketId);
    BasketItem findByBasketIdAndProductId(Long basketId, Long productId);
    boolean existsBasketItemsByBasketIdAndProductId(Long basketId,Long productId);

}
