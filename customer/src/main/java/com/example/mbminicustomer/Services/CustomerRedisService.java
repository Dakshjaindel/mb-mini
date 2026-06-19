package com.example.mbminicustomer.Services;


import com.example.mbminicustomer.Entities.Customer;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import tools.jackson.databind.ObjectMapper;


@RequiredArgsConstructor
@Service
public class CustomerRedisService {

    @Autowired
    private RedisMethods redisMethods;

    @Autowired
    private JedisPool jedisPool;

    public void updatePassInRedis(Long CustomerId,String newPass) {
        Customer customer=redisMethods.getFromRedis(CustomerId.toString(), Customer.class);
        customer.setPassword(newPass);
        redisMethods.addInRedis(customer,CustomerId.toString());

    }

    public void updateInRedis(Long customerId, String newName, String newEmail, Long newHouseNo, String newLocality, String newCity, Long newPincode) {
        ObjectMapper mapper = new ObjectMapper();
        String key = Customer.class.toString() + String.valueOf(customerId);
        Customer customer=redisMethods.getFromRedis(customerId.toString(), Customer.class);
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
        redisMethods.addInRedis(customer,customerId.toString());
    }
}


