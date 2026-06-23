package com.example.mbmini.DTOs;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogGetAllDTO {

    @NotNull
    @Min(value = 1)
    private Integer pageSize;

    @NotNull
    @Min(value=1)
    private Integer pageNo;

    private String similar;

    private String productNameFilter;

    private String quantityFilter;

}
