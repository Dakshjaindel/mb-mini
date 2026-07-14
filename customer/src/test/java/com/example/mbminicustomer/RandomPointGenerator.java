package com.example.mbminicustomer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class RandomPointGenerator {

    public static List<List<Double>> generateNearbyPoints() {
        Random random = new Random();
        List<List<Double>> points = new ArrayList<>();

        // 1. Generate a valid central anchor (using realistic Delhi region defaults)
        double centerLat = 28.40 + (random.nextDouble() * 0.1);
        double centerLng = 77.00 + (random.nextDouble() * 0.1);

        // 2. Generate a random radius size for the fence (e.g., 0.01 to 0.03 degrees)
        double size = 0.01 + (random.nextDouble() * 0.02);

        // 3. Construct an explicit, ordered bounding box clockwise (Top-Left -> Top-Right -> Bottom-Right -> Bottom-Left)
        // This mathematically guarantees lines will never intersect or twist
        double minLat = centerLat - size;
        double maxLat = centerLat + size;
        double minLng = centerLng - size;
        double maxLng = centerLng + size;

        points.add(List.of(minLat, minLng)); // 1. Bottom-Left (Start)
        points.add(List.of(maxLat, minLng)); // 2. Top-Left
        points.add(List.of(maxLat, maxLng)); // 3. Top-Right
        points.add(List.of(minLat, maxLng)); // 4. Bottom-Right
        points.add(List.of(minLat, minLng)); // 5. Bottom-Left (Explicit Loop Closure)

        return points;
    }
}
