package com.example.mbminicustomer.DTOs;


import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateLoginDTO {

    @NotNull
    @Pattern(regexp= "^[0-9]{10}$",message = "Phone number must be exactly 10 digits")
    @JsonAlias({"phoneNo"})
    private String PhoneNo;

}
