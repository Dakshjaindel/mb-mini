package com.example.mbminicart.Repos;

import com.example.mbminicart.Entities.BasketItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepo extends CrudRepository<BasketItem,Long> {


    BasketItem getBasketItemByBasketId(Long basketId);
    BasketItem findByBasketIdAndProductId(Long basketId, Long productId);
    boolean existsBasketItemsByBasketIdAndProductId(Long basketId,Long productId);
    List<BasketItem> findByBasketIdAndFlag(Long basketId, Integer flag);

}
