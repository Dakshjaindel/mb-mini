package com.example.mbminicart.DTOs;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBasketDTO {

    @NotNull
    private Long UserId;

    @NotNull
    private Date Date;

    @NotNull
    private Integer flag;
}
