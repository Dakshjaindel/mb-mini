package com.example.mbminicart.Repos;


import com.example.mbminicart.Entities.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ItemRepo extends JpaRepository<BasketItem,Long> {


    BasketItem getBasketItemByBasketId(Long basketId);
    BasketItem findByBasketIdAndProductId(Long basketId, Long productId);
    boolean existsBasketItemsByBasketIdAndProductId(Long basketId,Long productId);
    List<BasketItem> findByBasketIdAndFlag(Long basketId, Integer flag);



    List<BasketItem> findAllByProductId(Long productId);

    @Query("SELECT COALESCE(SUM(bi.quantity), 0) FROM BasketItem bi WHERE bi.productId = :productId" )
    Integer CatalogDemand(@Param("productId") Long productId);


    @Query("SELECT COALESCE(SUM(bi.quantity * c.price), 0) " +
            "FROM BasketItem bi JOIN Catalog c ON bi.productId = c.Id " +
            "WHERE bi.basketId = :basketId")
    BigDecimal sumWalletCostByBasketId(@Param("basketId") Long basketId);


    List<BasketItem> findAllByBasketId(Long basketId);


    List<BasketItem> findAllByProductIdAndFlagOrderByCreatedAt(Long productId, int i);
}
