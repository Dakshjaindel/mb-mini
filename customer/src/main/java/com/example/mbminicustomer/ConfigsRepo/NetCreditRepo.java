package com.example.mbminicustomer.ConfigsRepo;

import com.example.mbminicustomer.Entities.CustomerNetCredit;
import org.springframework.data.repository.CrudRepository;

public interface NetCreditRepo extends CrudRepository<CustomerNetCredit,Long> {
    CustomerNetCredit getCustomerNetCreditByCustomerId(Long customerId);
}
