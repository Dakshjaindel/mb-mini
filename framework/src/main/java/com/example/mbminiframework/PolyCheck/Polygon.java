package com.example.mbminiframework.PolyCheck;


import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private List<Point> vertices;

    public Polygon(){
        this.vertices=new ArrayList<>();
    }

    public void addPoint(List<Double> point){
        vertices.add(new Point(point.get(0),point.get(1)));
    }

    public int getSideCount(){
        return vertices.size();
    }

    public boolean insidePolygon(List<Double> point){
        Point p = new Point(point.get(0), point.get(1) );
        int numVertices=getSideCount();
        if (numVertices<3){
            return false;
        }
        boolean inside= false;
        Point p1=vertices.get(0);
        for (int i=1;i<numVertices;i++){
            Point p2=vertices.get(i%numVertices);
            if (((p1.getLongitude()>p.getLongitude())!=(p2.getLongitude()>p.getLongitude()))&&
                    (p.getLatitutde()<(p2.getLatitutde()-p1.getLatitutde())*(p.getLongitude()- p1.getLongitude())/(p2.getLongitude()- p1.getLongitude())+p1.getLatitutde())){
                inside=!inside;
            }
            p1=p2;
        }
        return inside;

    }
}
