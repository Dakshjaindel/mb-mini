package com.example.mbminicustomer;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepo extends CrudRepository<Customer,Long> {
    boolean existsByPhoneNo(String phoneNo);

    Customer findByPhoneNo(String phoneNo);
}
