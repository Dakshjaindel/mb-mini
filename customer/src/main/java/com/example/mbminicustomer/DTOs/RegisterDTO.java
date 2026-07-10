package com.example.mbminicustomer.DTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias({"name"})
    private String Name;


    @Pattern(regexp= "^[0-9]{10}$",message = "Phone number must be exactly 10 digits")
    @JsonAlias({"phoneNo"})
    private String PhoneNo;

    @jakarta.validation.constraints.NotNull
    @JsonAlias({"password"})
    private String Password;

    @NotBlank
    @Email(message = "invalid Email Format")
    @Column(unique = true,nullable = false)
    @JsonAlias({"email"})
    private String Email;



    @JsonAlias({"houseNo"})
    private Long HouseNo;


    @JsonAlias({"locality"})
    private String Locality;


    @JsonAlias({"city"})
    private String City;


    @JsonAlias({"pincode"})
    private Long Pincode;

    private Double latitude;

    private Double longitude;

}
