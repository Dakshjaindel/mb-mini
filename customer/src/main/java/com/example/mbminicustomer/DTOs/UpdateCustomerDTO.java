package com.example.mbminicustomer.DTOs;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerDTO {

    @NotNull
    private  Long customerId;

    private String newName;

    @Email(message = "invalid Email Format")
    private String newEmail;

    private Long newHouseNO;

    private String newLocality;

    private String newCity;

    private Long newPincode;





}
