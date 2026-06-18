package com.example.mbmini.Services;


import com.example.mbmini.Entities.Catalog;
import com.example.mbmini.RepoConnections.JPARepo;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;// for jackson 3.x (Spring Boot 4)

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class JPAService {
    @Autowired
    private JPARepo repository;

    @Autowired
    private RedisMethods redisMethods;


    @Autowired
    private CatalogRedisService catalogRedisService;


    public String create(Catalog catalog){
        Catalog saved= repository.save(catalog);
        redisMethods.addInRedis(saved,saved.getId().toString());
        String key= String.valueOf(saved.getId());
        return "Saved with the Id"+ key;
    }

    public String Update(long Id,String productName,Integer quantity,BigDecimal price, Boolean isActive){
        catalogRedisService.Update( Id, productName, quantity, price,  isActive);
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

    public Catalog Get(long Id){
        ObjectMapper mapper= new ObjectMapper();
        String cached= String.valueOf(redisMethods.getFromRedis(Id, Catalog.class));

        if (cached!= null){
            Catalog cachedCatalog= mapper.readValue(cached, Catalog.class);
            return cachedCatalog;
        }
        String needed= String.valueOf(Id);
        Catalog catalog = repository.findById(Id).orElseThrow(()->new RuntimeException("Catalog not found with id: " + Id));

        catalogRedisService.Create(catalog,catalog.getId());


        try {
            return catalog;
        } catch (JacksonException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }

    }


    public List<Catalog> findAll() {
        List<Catalog> catalogs = new ArrayList<>();
        repository.findAll().forEach(catalogs::add);
        return catalogs;
    }

    public String cacheRefresh(){
        List<Catalog> catalogs=new ArrayList<>();
        repository.findAll().forEach(catalogs::add);
        if (catalogs.isEmpty()){
            return "Na data in MYSQL DB";
        }
        catalogRedisService.refreshCache(catalogs);
        return "Data refreshed in redis";

    }


}
