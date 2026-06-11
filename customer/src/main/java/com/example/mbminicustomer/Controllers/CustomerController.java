package com.example.mbminicustomer.Controllers;


import com.example.mbminicustomer.DTOs.GenerateLoginDTO;
import com.example.mbminicustomer.DTOs.LoginDTO;
import com.example.mbminicustomer.DTOs.RegisterDTO;
import com.example.mbminicustomer.Entities.Customer;
import com.example.mbminicustomer.Services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping({"/customers/generate_login"})
    public @ResponseBody String generateLogin(@Valid @RequestBody GenerateLoginDTO generateLoginDTO){
        String PhoneNo= generateLoginDTO.getPhoneNo();
        return service.generateLogin(PhoneNo);
    }

    @PostMapping({"/customers/login"})
    public @ResponseBody String login(@Valid @RequestBody LoginDTO loginDTO){
        String PhoneNo=loginDTO.getPhoneNo();
        String Password =loginDTO.getPassword();
        return service.login(PhoneNo, Password);
    }


    @PostMapping({"/customers/register"})
    public @ResponseBody String register(@Valid @RequestBody RegisterDTO registerDTO){
        Customer customer = new Customer(
                registerDTO.getName(),
                registerDTO.getPhoneNo(),
                registerDTO.getPassword(),
                registerDTO.getEmail(),
                registerDTO.getHouseNo(),
                registerDTO.getLocality(),
                registerDTO.getCity(),
                registerDTO.getPincode()

        );
        return service.Register(customer);
    }
}
