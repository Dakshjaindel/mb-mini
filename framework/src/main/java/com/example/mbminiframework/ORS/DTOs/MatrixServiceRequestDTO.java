package com.example.mbminiframework.ORS.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class MatrixServiceRequestDTO {
    private List<List<Double>> locations;
    private List<Integer> sources;
    private List<Integer> destinations;
    private List<String> metrics;

    public MatrixServiceRequestDTO(List<List<Double>> locations,List<Integer> sources,List<Integer> destinations,List<String> metrics){
        this.locations=locations;
        this.sources=sources;
        this.destinations=destinations;
        this.metrics=metrics;
    }
}
