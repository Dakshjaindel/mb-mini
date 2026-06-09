package com.example.mbmini.Services;


import com.example.mbmini.Catalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;


@RequiredArgsConstructor
@Service
public class RedisService {
    private final JedisPool jedisPool;
    public void Create(Catalog catalog){
        ObjectMapper mapper=new ObjectMapper();
        try(Jedis jedis=jedisPool.getResource()){
            String value=mapper.writeValueAsString(catalog);
            jedis.set(String.valueOf(catalog.getId()), value);
            System.out.println("Saved to Redis - Key: " + catalog.getId() + " Value: " + value);
        }catch (Exception e){
            System.out.println("Redis error: " + e.getMessage());
        }
    }

    public void Update(long Id, String productName, Integer quantity, BigDecimal price, Boolean isActive){
        ObjectMapper mapper= new ObjectMapper();
        String key= String.valueOf(Id);
        try (Jedis jedis=jedisPool.getResource()){
            String existing = jedis.get(key);
            if (existing == null) throw new RuntimeException("Key not found in Redis: " + key);

            Catalog catalog = mapper.readValue(existing, Catalog.class);
            if (productName != null) catalog.setProductName(productName);
            if (quantity != null) catalog.setQuantity(quantity);
            if (price != null) catalog.setPrice(price);
            if (isActive != null) catalog.setActive(isActive);

            String value= mapper.writeValueAsString(catalog);
            jedis.set(key,value);
        }
    }

    public String Get(long Id){
        String key=String.valueOf(Id);
        try (Jedis jedis=jedisPool.getResource()){
            return jedis.get(key);
        }
    }

    public void refreshCache(List<Catalog> catalogs){
        ObjectMapper mapper=new ObjectMapper();
        try (Jedis jedis=jedisPool.getResource()){
            for (Catalog catalog: catalogs){
                String data=mapper.writeValueAsString(catalog);
                jedis.set(String.valueOf(catalog.getId()),data);
            }
        } catch (Exception e){
            throw new RuntimeException("Error refresihng cache",e);
        }
    }





}
