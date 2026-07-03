package com.example.mbminiframework.PolyCheck;


import lombok.Data;

@Data
public class Point {
    private Double latitutde;
    private Double longitude;

    public Point(Double latitutde,Double longitude){
        this.latitutde=latitutde;
        this.longitude=longitude;
    }



}
