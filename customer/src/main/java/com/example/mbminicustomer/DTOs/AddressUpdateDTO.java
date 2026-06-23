package com.example.mbminicustomer.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateDTO {

    @NotNull
    private Long id;

    private Long newHouseNo;

    private String newLocality;

    private String newCity;

    private Long newPincode;

}
