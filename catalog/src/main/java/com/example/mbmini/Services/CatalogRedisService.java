package com.example.mbmini.Services;


import com.example.mbmini.Entities.Catalog;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import com.example.mbminiframework.RedisPackage.RedisMethods;


@RequiredArgsConstructor
@Service
public class CatalogRedisService {

    @Autowired
    private  RedisMethods redisMethods;
    public void Create(Catalog catalog,Long Id){
        redisMethods.addInRedis(catalog,Id.toString());
    }

    public void Update(long Id, String productName, Integer quantity, BigDecimal price, Boolean isActive){
        ObjectMapper mapper= new ObjectMapper();
        String key= String.valueOf(Id);
        Catalog catalog =redisMethods.getFromRedis(Id,Catalog.class);
        if (productName != null) catalog.setProductName(productName);
        if (quantity != null) catalog.setQuantity(quantity);
        if (price != null) catalog.setPrice(price);
        if (isActive != null) catalog.setActive(isActive);
        redisMethods.addInRedis(catalog, catalog.getId().toString());
    }



    public void refreshCache(List<Catalog> catalogs){
        for (Catalog catalog: catalogs){
                redisMethods.addInRedis(catalog,catalog.getId().toString());
            }

    }





}
