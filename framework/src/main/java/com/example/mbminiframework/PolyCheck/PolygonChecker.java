package com.example.mbminiframework.PolyCheck;


import com.example.mbminiframework.RedisPackage.RedisMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PolygonChecker {

    @Autowired
    private RedisMethods redisMethods;



    public boolean isValidFence(List<List<Double>> points){

        if (points==null || points.size()<3){
            throw new RuntimeException("Minimum size of fence is 3");
        }

        if (!isValidPolygon(points)){
            throw new RuntimeException("Invalid Polygon Provided - edges intersect.");
        }

        if (isCounterClockwise(points)){
            throw new RuntimeException("Negative area founded");
        }

        List<Double> first = points.get(0);
        List<Double> last = points.get(points.size() - 1);
        boolean isClosed = Math.abs(first.get(0) - last.get(0)) < 0.0001
                && Math.abs(first.get(1) - last.get(1)) < 0.0001;
        if (!isClosed) {
            points.add(List.of(first.get(0), first.get(1))); // auto-close
        }

        return true;
    }

    private boolean isCounterClockwise(List<List<Double>> points) {
        double area = 0.0;
        int n = points.size();
        for (int i = 0; i < n; i++) {
            List<Double> curr = points.get(i);
            List<Double> next = points.get((i + 1) % n);
            area += (curr.get(1) * next.get(0));  // lon * nextLat
            area -= (next.get(1) * curr.get(0));  // nextLon * lat
        }
        return area > 0;
    }

    private boolean isValidPolygon(List<List<Double>> points){
        int n = points.size();
        for (int i=0; i<n;i++){
            for (int j=i+2;j<n;j++){
                if (i==0 && j==n-1) continue;
                if (edgesIntersect(points.get(i), points.get((i + 1) % n),
                        points.get(j), points.get((j + 1) % n))) {
                    log.warn("Self-intersection found between edge {} and {}", i, j);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean edgesIntersect(List<Double> p1, List<Double> p2,
                                   List<Double> p3, List<Double> p4){
        double d1 = direction(p3, p4, p1);
        double d2 = direction(p3, p4, p2);
        double d3 = direction(p1, p2, p3);
        double d4 = direction(p1, p2, p4);

        if (((d1>0 && d2<0)||(d1<0 && d2>0))&&((d3>0 && d4<0)||(d3<0 && d4>0))){
            return true;
        }
        return false;


    }
    private double direction(List<Double> pi, List<Double> pj, List<Double> pk) {
        return (pk.get(0) - pi.get(0)) * (pj.get(1) - pi.get(1))
                - (pj.get(0) - pi.get(0)) * (pk.get(1) - pi.get(1));
    }

    public Polygon buildPolygon(List<List<Double>> points){
        Polygon p = new Polygon();
        points.forEach(p::addPoint);
        return p;
    }
}
