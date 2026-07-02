package com.example.mbminiframework.ORS;


import io.netty.util.internal.shaded.org.jctools.queues.atomic.AtomicQueueUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class TSPHeuristicSolver {

    public List<List<Double>> greedyTSp(Double[][] distanceMatrix, List<List<Double>> positions){
        int n =distanceMatrix.length;

        int[][] matrix=new int[n][n];
        for (int i=0; i<n;i++){
            for (int j=0;j<n;j++){
                matrix[i][j] = distanceMatrix[i][j] == null ? 0 : distanceMatrix[i][j].intValue();
            }
        }

        List<Integer> path =greedyPath(matrix,n);
        path=linKernighan(path,matrix,n);


        // Step 4: 3-opt (most expensive, run last)
        path = threeOpt(path, matrix);
        log.info("After 3-opt cost: {}", totalCost(path, matrix));

        path = simulatedAnnealing(path, matrix);
        log.info("After SA cost: {}", totalCost(path, matrix));


        return extractRoute(positions,path);
    }

    private List<Integer> nodeInsertion(List<Integer> path, int[][] matrix) {
        List<Integer> tour = new ArrayList<>(path.subList(0, path.size() - 1));
        boolean improved = true;
        long start = System.currentTimeMillis();

        while (improved) {
            if (System.currentTimeMillis() - start > 5000) break;
            improved = false;

            for (int i = 1; i < tour.size(); i++) {  // skip hub at 0
                int node = tour.get(i);
                int prev = tour.get(i - 1);
                int next = tour.get((i + 1) % tour.size());

                // cost of removing node from current position
                int removeCost = matrix[prev][node] + matrix[node][next] - matrix[prev][next];

                // find best insertion position
                int bestGain = 0;
                int bestPos = -1;

                for (int j = 0; j < tour.size(); j++) {
                    if (j == i - 1 || j == i) continue;  // skip current position

                    int a = tour.get(j);
                    int b = tour.get((j + 1) % tour.size());

                    // cost of inserting node between a and b
                    int insertCost = matrix[a][node] + matrix[node][b] - matrix[a][b];
                    int gain = removeCost - insertCost;

                    if (gain > bestGain) {
                        bestGain = gain;
                        bestPos = j;
                    }
                }

                if (bestPos != -1) {
                    // remove from current position
                    tour.remove(i);
                    // insert at best position (adjust for removal)
                    int insertAt = bestPos > i ? bestPos : bestPos + 1;
                    tour.add(insertAt, node);
                    improved = true;
                    log.info("Moved node {} to position {}, gain: {}", node, insertAt, bestGain);
                }
            }
        }

        tour.add(tour.get(0));
        return tour;
    }
    private List<Integer> simulatedAnnealing(List<Integer> path, int[][] matrix) {
        List<Integer> tour = new ArrayList<>(path.subList(0, path.size() - 1));
        List<Integer> bestTour = new ArrayList<>(tour);
        int bestCost = totalCost(tour, matrix);
        int currentCost = bestCost;

        double temperature = 10000.0;
        double coolingRate = 0.995;
        long start = System.currentTimeMillis();

        while (temperature > 1.0) {
            if (System.currentTimeMillis() - start > 8000) break;

            // pick two random non-hub positions
            int i = 1 + (int) (Math.random() * (tour.size() - 1));
            int j = 1 + (int) (Math.random() * (tour.size() - 1));
            if (i == j) continue;

            // swap nodes i and j
            Collections.swap(tour, i, j);
            int newCost = totalCost(tour, matrix);
            int delta = newCost - currentCost;

            if (delta < 0) {
                // improvement — always accept
                currentCost = newCost;
                if (newCost < bestCost) {
                    bestCost = newCost;
                    bestTour = new ArrayList<>(tour);
                    log.info("SA new best cost: {}", bestCost);
                }
            } else {
                // worse — accept with probability based on temperature
                if (Math.random() < Math.exp(-delta / temperature)) {
                    currentCost = newCost;
                } else {
                    // reject — swap back
                    Collections.swap(tour, i, j);
                }
            }

            temperature *= coolingRate;
        }

        bestTour.add(bestTour.get(0));
        return bestTour;
    }


    private int totalCost(List<Integer> path, int[][] matrix) {
        int cost = 0;
        for (int i = 0; i < path.size() - 1; i++)
            cost += matrix[path.get(i)][path.get(i + 1)];
        return cost;
    }


    public List<Integer> greedyPath(int[][] matrix, int n){
        boolean[] visited=new boolean[n];
        List<Integer> path= new ArrayList<>();
        int currCity=0;
        visited[currCity]=true;
        path.add(currCity);

        for (int i=0;i<n;i++){
            int nearestCity=-1;
            int minDist=Integer.MAX_VALUE;

            for (int nextCity=0; nextCity<n;nextCity++){
                if (!visited[nextCity] && matrix[currCity][nextCity]<minDist){
                    minDist=matrix[currCity][nextCity];
                    nearestCity=nextCity;
                }
            }
            if (nearestCity==-1) break;

            currCity=nearestCity;
            visited[currCity]=true;
            path.add(currCity);
        }
        path.add(0);
        return path;
    }

    public List<Integer> linKernighan(List<Integer> path, int[][] matrix, int n){
        List<Integer> tour = new ArrayList<>(path.subList(0, path.size() - 1));
        boolean improved=true;

        while (improved){
            improved=false;
            for (int i=0;i<tour.size()-1;i++){
                for (int j=i+2;j<tour.size();j++){
                    if (i == 0 && j == tour.size() - 1) continue;
                    int gain= twoOptGain(tour,matrix,i,j);
                    if (gain > 0) {
                        // reverse the segment between i+1 and j
                        reverse(tour, i + 1, j);
                        improved = true;
                        log.info("2-opt swap improved route by: {}", gain);
                    }

                }
            }
        }
        tour.add(tour.get(0));
        return tour;
    }

    private  void  reverse(List<Integer> tour, int i, int j){
        while (i < j) {
            int tmp = tour.get(i);
            tour.set(i, tour.get(j));
            tour.set(j, tmp);
            i++;
            j--;
        }
    }

    private int twoOptGain(List<Integer> tour, int[][] matrix, int i, int j) {
        int a = tour.get(i);
        int b = tour.get(i + 1);
        int c = tour.get(j);
        int d = tour.get((j + 1) % tour.size());

        int currentCost = matrix[a][b] + matrix[c][d];
        int newCost     = matrix[a][c] + matrix[b][d];

        return currentCost - newCost;  // positive = improvement
    }

    public List<List<Double>> extractRoute(List<List<Double>> positions,List<Integer> bestPath){

        if (bestPath == null) return null;
        return bestPath.stream()
                .map(index -> {
                    List<Double> pos = positions.get(index);
                    return List.of(pos.get(1), pos.get(0));  // swap back to [lat, lon] for response
                })
                .toList();
    }

    // Move single node or segment of 2-3 to a better position
    private List<Integer> orOpt(List<Integer> tour, int[][] matrix) {
        boolean improved = true;

        while (improved) {
            improved = false;

            // try segment sizes 1, 2, 3
            for (int segLen = 1; segLen <= 3; segLen++) {
                for (int i = 1; i < tour.size() - segLen - 1; i++) {
                    for (int j = 1; j < tour.size() - 1; j++) {
                        if (j >= i && j <= i + segLen) continue; // skip overlapping

                        int gain = orOptGain(tour, matrix, i, segLen, j);
                        if (gain > 0) {
                            applyOrOpt(tour, i, segLen, j);
                            improved = true;
                            log.info("Or-opt improved by {} (seg={}, i={}, j={})", gain, segLen, i, j);
                        }
                    }
                }
            }
        }
        return tour;
    }

    private int orOptGain(List<Integer> tour, int[][] matrix, int i, int segLen, int j) {
        int prev  = tour.get(i - 1);
        int first = tour.get(i);
        int last  = tour.get(i + segLen - 1);
        int next  = tour.get(i + segLen);
        int ins   = tour.get(j);
        int insNext = tour.get(j + 1);

        // cost of removing segment
        int removeCost = matrix[prev][first] + matrix[last][next] - matrix[prev][next];
        // cost of inserting segment after j
        int insertCost = matrix[ins][first] + matrix[last][insNext] - matrix[ins][insNext];

        return removeCost - insertCost;
    }

    private void applyOrOpt(List<Integer> tour, int i, int segLen, int j) {
        List<Integer> segment = new ArrayList<>(tour.subList(i, i + segLen));
        tour.subList(i, i + segLen).clear();

        // adjust j after removal
        int insertPos = j > i ? j - segLen + 1 : j + 1;
        tour.addAll(insertPos, segment);
    }

    private List<Integer> threeOpt(List<Integer> tour, int[][] matrix) {
        boolean improved = true;
        long startTime = System.currentTimeMillis();

        while (improved) {
            if (System.currentTimeMillis() - startTime > 8000) {
                log.info("3-opt time limit reached");
                break;
            }
            improved = false;
            int n = tour.size() - 1; // exclude last depot

            for (int i = 0; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    for (int k = j + 1; k < n; k++) {
                        int improvement = best3OptMove(tour, matrix, i, j, k);
                        if (improvement > 0) {
                            apply3OptMove(tour, i, j, k);
                            improved = true;
                        }
                    }
                }
            }
        }
        return tour;
    }

    private int best3OptMove(List<Integer> tour, int[][] matrix, int i, int j, int k) {
        int a = tour.get(i),     b = tour.get(i + 1);
        int c = tour.get(j),     d = tour.get(j + 1);
        int e = tour.get(k),     f = tour.get((k + 1) % (tour.size() - 1));

        int d0 = matrix[a][b] + matrix[c][d] + matrix[e][f]; // current
        int d1 = matrix[a][c] + matrix[b][d] + matrix[e][f]; // 2-opt on i,j
        int d2 = matrix[a][b] + matrix[c][e] + matrix[d][f]; // 2-opt on j,k
        int d3 = matrix[a][e] + matrix[c][d] + matrix[b][f]; // 2-opt on i,k
        int d4 = matrix[a][c] + matrix[b][e] + matrix[d][f]; // 3-opt move 1
        int d5 = matrix[a][d] + matrix[e][b] + matrix[c][f]; // 3-opt move 2
        int d6 = matrix[a][d] + matrix[e][c] + matrix[b][f]; // 3-opt move 3

        int best = Math.min(d0, Math.min(d1, Math.min(d2, Math.min(d3, Math.min(d4, Math.min(d5, d6))))));
        return d0 - best;
    }

    private void apply3OptMove(List<Integer> tour, int i, int j, int k) {
        // reverse segment i+1 to j (equivalent to best 3-opt move found)
        reverse(tour, i + 1, j);
        reverse(tour, j + 1, k);
    }


}
