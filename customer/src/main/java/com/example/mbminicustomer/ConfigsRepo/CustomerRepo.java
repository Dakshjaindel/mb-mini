package com.example.mbminicustomer.ConfigsRepo;

import com.example.mbminicustomer.Entities.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepo extends CrudRepository<Customer,Long> {
    boolean existsByPhoneNo(String phoneNo);

    Customer findByPhoneNo(String phoneNo);

    Customer getCustomerById(Long customerId);
}
