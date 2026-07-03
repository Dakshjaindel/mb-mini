package com.example.mbminiframework.RedisPackage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;


@Component
public class RedisMethods {


    private final JedisPool jedisPool;
    private final ObjectMapper mapper;

    public RedisMethods(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }



    public <T> void addInRedis(T adding, String Id, long seconds) {

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


    public <T> void addInRedis(T adding,String Id){

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
        String key = clazz.getName() + "." + id;
        try (Jedis jedis = jedisPool.getResource()) {
            String existing = jedis.get(key);
            if (existing == null) return null;
            return mapper.readValue(existing, clazz);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Redis error: " + e.getMessage());
        }
    }


    public void saveFence(List<List<Double>> points) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = mapper.writeValueAsString(points);
            jedis.set("serviceableGeoFence", value);  // fixed key — always overwrites
            System.out.println("Fence saved to Redis");
        } catch (Exception e) {
            System.out.println("Redis fence save error: " + e.getMessage());
        }
    }

    public List<List<Double>> getFence() {
        try (Jedis jedis = jedisPool.getResource()) {
            String existing = jedis.get("serviceableGeoFence");
            if (existing == null) return null;
            return mapper.readValue(existing, new com.fasterxml.jackson.core.type.TypeReference<List<List<Double>>>() {});
        } catch (Exception e) {
            System.out.println("Redis fence get error: " + e.getMessage());
            return null;
        }
    }


}
