package com.example.mbminiframework.ORS;


import com.example.mbminiframework.ORS.DTOs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.mbminiframework.ORS.RouteService;

import java.util.List;

@RestController
@RequestMapping("/route")
public class ORSController {


    private final RouteService service;


    public ORSController(RouteService service) {
        this.service = service;
    }

    @GetMapping("/get")
    public RouteResponseDTO getRoute(@RequestBody DistanceRequestDTO requestDTO){
        return service.getRoute(requestDTO.getStartLong(), requestDTO.getStartLat(), requestDTO.getEndLong(), requestDTO.getEndLat());
    }

    @PostMapping("/optimalRoute")
    public List<List<Double>> optimalRoute(@RequestBody MatrixServiceRequestDTO matrixServiceRequestDTO){
        return service.getMatrix(matrixServiceRequestDTO,matrixServiceRequestDTO.getLocations());
    }



}
