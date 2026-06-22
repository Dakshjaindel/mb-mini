package com.example.mbminicart.DTOs;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditAddDTO {

    @NotNull
    @Min(0)
    private BigDecimal creditAmount;

    @NotNull
    private String type;

    @NotNull
    private Integer flag;

}
