package com.example.mbminicustomer.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMeDTO {

    private Long id;
    private String name;
    private String phoneNo;
    private String email;
}
