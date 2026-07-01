package com.example.mbminicustomer.ConfigsRepo;

import com.example.mbminicustomer.Entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepo extends JpaRepository<Customer,Long> {
    boolean existsByPhoneNo(String phoneNo);

    Customer findByPhoneNo(String phoneNo);

    Customer getCustomerById(Long customerId);

    List<Customer> findAllByIdIn(List<Long> userIds);
}
