package com.example.mbminiframework.Entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogQuantityUpdateDTO {
    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;
}
