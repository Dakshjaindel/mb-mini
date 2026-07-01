package com.example.mbminiframework.ORS;

import com.example.mbminiframework.ORS.DTOs.MatrixResponseDTO;
import com.example.mbminiframework.ORS.DTOs.MatrixServiceRequestDTO;
import com.example.mbminiframework.ORS.DTOs.RouteResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.mbminiframework.ORS.TSPSolver;

import java.util.List;


@Slf4j
@Service
public class RouteService {

    private final RestTemplate resttemplate;
    private final TSPSolver tspSolver;

    @Value("${ors.api.key}")
    private String apikey;

    @Value("${ors.api.url}")
    private String apiurl;

    public RouteService(RestTemplate restTemplate, TSPSolver tspSolver){
        this.resttemplate=restTemplate;
        this.tspSolver = tspSolver;
    }


    public RouteResponseDTO getRoute(Double startLon, Double startLat, Double endLon, Double endLat){
        String url= String.format("%s/v2/directions/driving-car?api_key=%s&start=%f,%f&end=%f,%f",apiurl,apikey,startLon,startLat,endLon,endLat);

        return resttemplate.getForObject(url,RouteResponseDTO.class);
    }


    public List<List<Double>> getMatrix(MatrixServiceRequestDTO matrixRequest,List<List<Double>> positions){
        String url = apiurl+ "/v2/matrix/driving-car";
        HttpHeaders headers=new org.springframework.http.HttpHeaders();
        headers.set("Authorization", apikey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MatrixServiceRequestDTO> entity= new HttpEntity<>(matrixRequest,headers);
        MatrixResponseDTO result= resttemplate.postForObject(url,entity,MatrixResponseDTO.class);
        log.info("ORS Response: {}",result);
        Double[][] matrix = result.getDistances().stream()
                .map(innerList -> innerList.stream()
                        .map(val -> val == null ? 0.0 : val)
                        .toArray(Double[]::new))
                .toArray(Double[][]::new);
        log.info("matrix made and returned from ors api: {}",matrix);
        return tspSolver.optimalPath(matrix, positions);

    }
}
