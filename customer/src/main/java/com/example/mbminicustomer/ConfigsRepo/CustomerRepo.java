package com.example.mbminicustomer.ConfigsRepo;

import com.example.mbminicustomer.Entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepo extends JpaRepository<Customer,Long> {
    boolean existsByPhoneNo(String phoneNo);

    Customer findByPhoneNo(String phoneNo);

    Customer getCustomerById(Long customerId);
}
