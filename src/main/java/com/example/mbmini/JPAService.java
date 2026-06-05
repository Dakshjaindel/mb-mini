package com.example.mbmini;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;// for jackson 3.x (Spring Boot 4)

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JPAService {
    private final JPARepo repository;

    @Autowired
    private RedisService redisService;


    public JPAService(JPARepo repository) {this.repository = repository;}

    public String create(Catalog catalog){
        Catalog saved= repository.save(catalog);
        redisService.Create(saved);
        String key= String.valueOf(saved.getId());
        return "Saved with the Id"+ key;
    }

    public String Update(long Id,String productName,Integer quantity,BigDecimal price, Boolean isActive){
        redisService.Update( Id, productName, quantity, price,  isActive);
        Catalog catalog=repository.findById(Id).orElseThrow(()->new RuntimeException("Catalog not Found with Id: "+ Id));
        if (productName != null){
            catalog.setProductName(productName);
        }
        if (quantity != null){
            catalog.setQuantity(quantity);
        }
        if (price !=null){
            catalog.setPrice(price);
        }
        if ( isActive !=null){
            catalog.setActive(isActive);
        }
        repository.save(catalog);
        
        return "Updated Successfully";
    }

    public String Get(long Id){
        ObjectMapper mapper= new ObjectMapper();
        String cached=redisService.Get(Id);
        if (cached!= null){
            return cached;
        }
        String needed= String.valueOf(Id);
        Catalog catalog = repository.findById(Id).orElseThrow(()->new RuntimeException("Catalog not found with id: " + Id));

        redisService.Create((catalog));


        try {
            return new ObjectMapper().writeValueAsString(catalog);
        } catch (JacksonException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }

    }


    public String cacheRefresh(){
        List<Catalog> catalogs=new ArrayList<>();
        repository.findAll().forEach(catalogs::add);
        if (catalogs.isEmpty()){
            return "Na data in MYSQL DB";
        }
        redisService.refreshCache(catalogs);
        return "Data refreshed in redis";

    }


}
