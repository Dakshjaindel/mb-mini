package com.example.mbmini.DTOs;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogUpdateDTO {

    @NotNull
    private long Id;

    private String productName;

    @Min(value = 0)
    private Integer quantity;

    @Min(value = 0, message = "Cant be negative")
    @Digits(fraction = 2,message = "Numeric value out of bounds (.<2 digits> expected)", integer = 12)
    private BigDecimal price;

    private Boolean isActive;



}
