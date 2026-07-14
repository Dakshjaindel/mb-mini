package com.example.mbminicustomer.Controllers;


import com.example.mbminicustomer.DTOs.*;
import com.example.mbminicustomer.Entities.Customer;
import com.example.mbminicustomer.Services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.AuthProvider;

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
                registerDTO.getPincode(),
                registerDTO.getLatitude(),
                registerDTO.getLongitude()

        );
        return service.Register(customer);
    }

    @PutMapping({"/customers"})
    public @ResponseBody String updateCustomer(@Valid @RequestBody UpdateCustomerDTO updateCustomerDTO){

        return service.update(updateCustomerDTO.getCustomerId(), updateCustomerDTO.getNewName(),updateCustomerDTO.getNewEmail(), updateCustomerDTO.getNewHouseNO(), updateCustomerDTO.getNewLocality(),updateCustomerDTO.getNewCity(), updateCustomerDTO.getNewPincode());
    }

    @PutMapping({"/customers/password"})
    public @ResponseBody String updatePass(@Valid@RequestBody UpdatePassDTO updatePassDTO){
        return service.passwordUpdate(updatePassDTO.getId(),updatePassDTO.getNewPass());
    }

    @PutMapping({"/customer/address"})
    public @ResponseBody String updateAddress(@Valid@RequestBody AddressUpdateDTO addressUpdateDTO){
        return service.addressUpdate(addressUpdateDTO.getId(), addressUpdateDTO.getNewHouseNo(), addressUpdateDTO.getNewLocality(),addressUpdateDTO.getNewCity(),addressUpdateDTO.getNewPincode());
    }

    @PostMapping({"/consumer/customers/logout"})
    public @ResponseBody String logout(@RequestHeader("AuthKey") String authHeader){
        System.out.println("Logout API is Called");
        String authKey = authHeader.replace("Bearer ", "");
        return service.logout(authKey);
    }

    @PostMapping({"/customer/refresh"})
    public @ResponseBody String refresh(@RequestParam String refreshToken){
        return service.refresh(refreshToken);
    }

    @GetMapping({"/customers/me"})
    public @ResponseBody CustomerMeDTO me(@RequestHeader("AuthKey") String authHeader){
        String authKey = authHeader.replace("Bearer ", "");
        Customer customer = service.getCustomerByAuthKey(authKey);
        return new CustomerMeDTO(
                customer.getId(),
                customer.getName(),
                customer.getPhoneNo(),
                customer.getEmail()
        );
    }

    @PostMapping({"/setFence"})
    public @ResponseBody String setFence(@RequestBody FenceDTO fenceDTO){
        return service.validPolygon(fenceDTO.getPoints());
    }


}
