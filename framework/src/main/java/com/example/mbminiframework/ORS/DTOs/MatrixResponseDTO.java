package com.example.mbminiframework.ORS.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class MatrixResponseDTO {
    private List<List<Double>> distances;
    private List<List<Double>> durations;
}
