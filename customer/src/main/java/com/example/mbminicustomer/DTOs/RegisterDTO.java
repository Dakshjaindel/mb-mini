package com.example.mbminicustomer.DTOs;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {

    @NotNull
    private String Name;


    @Pattern(regexp= "^[0-9]{10}$",message = "Phone number must be exactly 10 digits")
    private String PhoneNo;

    @jakarta.validation.constraints.NotNull
    private String Password;

    @NotBlank
    @Email(message = "invalid Email Format")
    @Column(unique = true,nullable = false)
    private String Email;



    private Long HouseNo;


    private String Locality;


    private String City;


    private Long Pincode;

    private Double latitude;

    private Double longitude;

}
