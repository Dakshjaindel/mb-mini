package com.example.mbminicustomer.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePassDTO {

    @NotNull
    private Long Id;

    @NotNull
    private String newPass;
}
