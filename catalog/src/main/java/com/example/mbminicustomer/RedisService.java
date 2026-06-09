package com.example.mbminicustomer;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tools.jackson.databind.ObjectMapper;


@RequiredArgsConstructor
@Service
public class RedisService {
    private final JedisPool jedisPool;

    public void Register(Customer customer){
        ObjectMapper mapper= new ObjectMapper();
        try(Jedis jedis=jedisPool.getResource()){
            String value=mapper.writeValueAsString(customer);
            String key = "Customer."+customer.getId();
            jedis.set(key, value);
            System.out.println("Saved to Redis - Key: " + customer.getId() + " Value: " + value);
        }catch (Exception e){
            System.out.println("Redis error: " + e.getMessage());
        }
    }

    public void sessionInRedis(AuthSession authSession){
        ObjectMapper mapper= new ObjectMapper();
        try(Jedis jedis=jedisPool.getResource()){
            String value=mapper.writeValueAsString(authSession);
            String key = "AuthSession."+authSession.getId();
            jedis.set(key, value);
            System.out.println("Saved to Redis - Key: " + authSession.getId() + " Value: " + value);
        }catch (Exception e){
            System.out.println("Redis error: " + e.getMessage());
        }
    }


}
