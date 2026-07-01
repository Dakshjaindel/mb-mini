package com.example.mbminiframework.ORS.DTOs;

public class LatLongDTO {

    private Double Longitude;
    private Double Latitude;
    public Double getLongitude(){
        return this.Longitude;
    }
    public void setLongitude(Double longitude){
        this.Longitude=longitude;
    }
    public Double getLatitude(){
        return this.Latitude;
    }
    public void setLatitude(Double latitude){
        this.Latitude=latitude;
    }


}
