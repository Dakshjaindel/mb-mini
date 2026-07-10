package com.example.mbminicustomer.DTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePassDTO {

    @NotNull
    @JsonAlias({"id"})
    private Long Id;

    @NotNull
    private String newPass;
}
