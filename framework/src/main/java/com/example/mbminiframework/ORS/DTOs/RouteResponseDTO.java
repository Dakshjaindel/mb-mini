package com.example.mbminiframework.ORS.DTOs;

import com.example.mbminiframework.ORS.Feature;

import java.util.List;


public class RouteResponseDTO {

    private List<Feature> features;

    public List<Feature> getFeatures(){
        return features;
    }

    public void setFeatures(List<Feature> features){
        this.features=features;
    }

}
