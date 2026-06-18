package com.example.mbminiframework.RedisPackage;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tools.jackson.databind.ObjectMapper;


@Component
public class RedisMethods {

    private final JedisPool jedisPool;

    public <T> void addInRedis(T adding,String Id,long seconds){
        ObjectMapper mapper= new ObjectMapper();
        try(Jedis jedis=jedisPool.getResource()){
            String value=mapper.writeValueAsString(adding);
            String key = adding.getClass().getName()+"."+Id;
            jedis.set(key, value);
            jedis.expire(key,seconds);
            System.out.println("Saved to Redis - Key: " + key + " Value: " + value);
        }catch (Exception e) {
            System.out.println("Redis error: " + e.getMessage());
        }
    }


    public <T> void deleteInRedis(T session,Long Id){
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "AuthSession." +Id;
            jedis.del(key);
            System.out.println("Deleted from Redis: " + key);
        } catch (Exception e) {
            System.out.println("Redis delete error: " + e.getMessage());
        }
    }

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

    public <T> T getFromRedis(Long id, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        String key = clazz.toString() + String.valueOf(id);
        try (Jedis jedis = jedisPool.getResource()) {
            String existing = jedis.get(key);
            if (existing == null) throw new RuntimeException("Key not found in Redis: " + key);
            T customer1 = mapper.readValue(existing, clazz);
            return customer1;


        }
    }


}
