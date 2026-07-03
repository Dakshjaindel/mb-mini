package com.example.mbminiframework.PolyCheck;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Configuration
public class GeofenceConfig {

    @Bean
    public Polygon fence(){
        Polygon fence=new Polygon();

        fence.addPoint(List.of(28.497324,76.889843));
        fence.addPoint(List.of(28.506854,77.092837));
        fence.addPoint(List.of(28.442874,77.120985));
        fence.addPoint(List.of(28.371606,77.051713));
        fence.addPoint(List.of(28.404226,76.891169));
        fence.addPoint(List.of(28.459175,77.028345));

        return fence;
    }
}
