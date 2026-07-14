package com.example.mbminicustomer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class RandomPointGenerator {

    public static List<List<Double>> generateNearbyPoints() {
        double centerLat = 28.430;
        double centerLon = 77.048;
        double offset = 0.015;

        return List.of(
                List.of(centerLat - offset, centerLon - offset),  // SW
                List.of(centerLat + offset, centerLon - offset),  // NW
                List.of(centerLat + offset, centerLon + offset),  // NE
                List.of(centerLat - offset, centerLon + offset),  // SE
                List.of(centerLat - offset, centerLon - offset)   // close
        );
    }
}
