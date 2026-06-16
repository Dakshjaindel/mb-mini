package com.example.mbminicustomer.Services;


import com.example.mbminicustomer.Entities.AuthSession;
import com.example.mbminicustomer.Entities.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tools.jackson.databind.ObjectMapper;


@RequiredArgsConstructor
@Service
public class CustomerRedisService {
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

    public void updatePassInRedis(Long CustomerId,String newPass) {
        ObjectMapper mapper = new ObjectMapper();
        String key = Customer.class.toString() + String.valueOf(CustomerId);
        try (Jedis jedis = jedisPool.getResource()) {
            String existing = jedis.get(key);
            if (existing == null) throw new RuntimeException("Key not found in Redis: " + key);
            Customer customer1 = mapper.readValue(existing, Customer.class);
            customer1.setPassword(newPass);
            String value = mapper.writeValueAsString(customer1);
            jedis.set(key, value);
        }
    }
    public void updateInRedis(Long customerId, String newName, String newEmail, Long newHouseNo, String newLocality, String newCity, Long newPincode) {
        ObjectMapper mapper = new ObjectMapper();
        String key = Customer.class.toString() + String.valueOf(customerId);
        try (Jedis jedis = jedisPool.getResource()) {
            String existing = jedis.get(key);
            if (existing == null) throw new RuntimeException("key not found in redis");
            Customer customer = mapper.readValue(existing, Customer.class);
            if (newName != null) {
                customer.setName(newName);
            }
            if (newEmail != null) {
                customer.setEmail(newEmail);
            }
            if (newHouseNo != null) {
                customer.setHouseNo(newHouseNo);
            }
            if (newLocality != null) {
                customer.setLocality(newLocality);
            }
            if (newCity != null) {
                customer.setCity(newCity);
            }
            if (newPincode != null) {
                customer.setPincode(newPincode);
            }
            String value = mapper.writeValueAsString(customer);
            jedis.set(key, value);

        }
    }
}


