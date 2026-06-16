package com.example.mbminiframework.RedisPackage;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tools.jackson.databind.ObjectMapper;


@Component
public class RedisMethods {

    private final JedisPool jedisPool;

    public RedisMethods(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public <T> void addInRedis(T adding,String Id){
        ObjectMapper mapper= new ObjectMapper();
        try(Jedis jedis=jedisPool.getResource()){
            String value=mapper.writeValueAsString(adding);
            String key = adding.getClass().getName()+"."+Id;
            jedis.set(key, value);
            System.out.println("Saved to Redis - Key: " + key + " Value: " + value);
        }catch (Exception e) {
            System.out.println("Redis error: " + e.getMessage());
        }
    }
}
