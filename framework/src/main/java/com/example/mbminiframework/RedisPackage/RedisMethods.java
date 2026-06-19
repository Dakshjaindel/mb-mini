package com.example.mbminiframework.RedisPackage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Component
public class RedisMethods {

    private final JedisPool jedisPool;

    public <T> void addInRedis(T adding, String Id, long seconds) {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // ✅ add this
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ✅ store as string not array
        try (Jedis jedis = jedisPool.getResource()) {
            String value = mapper.writeValueAsString(adding);
            String key = adding.getClass().getName() + "." + Id;
            jedis.set(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
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
        com.fasterxml.jackson.databind.ObjectMapper mapper= new com.fasterxml.jackson.databind.ObjectMapper();
        try(Jedis jedis=jedisPool.getResource()){
            String value=mapper.writeValueAsString(adding);
            String key = adding.getClass().getName()+"."+Id;
            jedis.set(key, value);
            System.out.println("Saved to Redis - Key: " + key + " Value: " + value);
        }catch (Exception e) {
            System.out.println("Redis error: " + e.getMessage());
        }
    }

    public <T> T getFromRedis(String id, Class<T> clazz) {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // ✅ add this — missing!
        String key = clazz.getName() + "." + id;
        try (Jedis jedis = jedisPool.getResource()) {
            String existing = jedis.get(key);
            if (existing == null) throw new RuntimeException("Key not found in Redis: " + key);
            return mapper.readValue(existing, clazz);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Redis error: " + e.getMessage());
        }
    }


}
