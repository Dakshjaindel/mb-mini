package com.example.mbminicustomer.DTOs;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotNull
    @Pattern(regexp= "^[0-9]{10}$",message = "Phone number must be exactly 10 digits")
    private String PhoneNo;

    @NotBlank
    private String Password;
}
