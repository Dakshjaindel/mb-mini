package com.example.mbminicart.Repos;

import com.example.mbminicart.Entities.CustomerNetCredit;
import org.springframework.data.repository.CrudRepository;

public interface NetCreditRepo extends CrudRepository<CustomerNetCredit,Long> {
    CustomerNetCredit getCustomerNetCreditByCustomerId(Long customerId);
}
