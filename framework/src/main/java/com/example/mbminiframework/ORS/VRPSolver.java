package com.example.mbminiframework.ORS;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class VRPSolver {
    private static final int VEHICLE_NUMBER = 1;
    private static final int DEPOT = 0;
    private static final long MAX_DISTANCE = 100000000L;

    public List<List<Double>> solve(Double[][] distanceMatrix, List<List<Double>> positions){
        Loader.loadNativeLibraries();
        int n= distanceMatrix.length;
        long[][] matrix= new long[n][n];
        for (int i=0; i<n;i++){
            for (int j=0; j<n;j++){
                matrix[i][j]=distanceMatrix[i][j]==null ? 0L : distanceMatrix[i][j].longValue();
            }
        }
        RoutingIndexManager manager=new RoutingIndexManager(n,VEHICLE_NUMBER,DEPOT);
        RoutingModel routing =new RoutingModel(manager);
        final int transitCallbackIndex= routing.registerTransitCallback((long fromIndex,long toIndex) -> {
            int fromNode= manager.indexToNode(fromIndex);
            int toNode=manager.indexToNode(toIndex);
            return matrix[fromNode][toNode];
        });
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
        routing.addDimension(transitCallbackIndex,0,MAX_DISTANCE,true,"distance");
        RoutingSearchParameters searchParameters= main.defaultRoutingSearchParameters().toBuilder().setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC).build();
        Assignment solution = routing.solveWithParameters(searchParameters);
         if (solution==null){
             log.warn("No Solution found by VRP Solver");
             return null;
         }

        return extractRoute(routing, manager, solution, positions);


    }

    public List<List<Double>> extractRoute(RoutingModel routing,RoutingIndexManager manager,Assignment solution , List<List<Double>> positions){
        List<List<Double>> route= new ArrayList<>();
        long index=routing.start(0);
        while (!routing.isEnd(index)){
            int node= manager.indexToNode(index);
            route.add(positions.get(node));
            log.info("Visiting node {}: {}", node, positions.get(node));
            index=solution.value(routing.nextVar(index));
        }
        route.add(positions.get(DEPOT));
        log.info("Returning to hub: {}", positions.get(DEPOT));

        return route;
    }


}
