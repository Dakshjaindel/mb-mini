package com.example.mbminicart.DTOs;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketAddDTO {
    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;
}
