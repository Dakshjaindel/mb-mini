package com.example.mbminiframework.ORS.DTOs;

public class DistanceRequestDTO {
    private LatLongDTO start;
    private LatLongDTO end;

    public Double getStartLat(){
        return start.getLatitude();
    }
    public Double getStartLong(){
        return start.getLongitude();
    }public Double getEndLat(){
        return end.getLatitude();
    }public Double getEndLong(){
        return end.getLongitude();

    }

}
