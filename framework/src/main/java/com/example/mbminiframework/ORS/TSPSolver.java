package com.example.mbminiframework.ORS;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

@Component
public class TSPSolver {
    private final Integer INF=Integer.MAX_VALUE;
    static final double BLOCK = -1.0;

    public static class Node implements Comparable<Node>{
        Double[][] reducedMatrix;
        Double cost;
        Integer currentLocation;
        Integer level;
        List<Integer> path;

        public Node(Double[][] reducedMatrix,Double cost,Integer currentLocation,Integer level,List<Integer> path){
            this.reducedMatrix=reducedMatrix;
            this.cost=cost;
            this.currentLocation=currentLocation;
            this.level=level;
            this.path=path;
        }

        @Override
        public int compareTo(Node other){
            return Double.compare(this.cost,other.cost);
        }
    }

    public static Double[][] copyMatrix(Double[][] matrix){
        Double[][] copy= new Double[matrix.length][];
        for (int i =0; i<matrix.length;i++){
            copy[i]=matrix[i].clone();
        }
        return copy;
    }

    public static Double reduceMatrix(Double[][] matrix){
        int n = matrix.length;
        double reductionCost=0.0;
        for (int i=0;i<n;i++){
            double rowMin=Double.MAX_VALUE;
            for (int j=0;j<n;j++){
                if (matrix[i][j]<rowMin){
                    rowMin=matrix[i][j];
                }
            }
            if (rowMin!=Double.MAX_VALUE && rowMin>0){
                reductionCost+=rowMin;
                for (int j=0;j<n;j++){
                    matrix[i][j]-=rowMin;
                }
            }
        }

        for (int j = 0; j < n; j++) {
            double colMin = Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (matrix[i][j] < colMin) {
                    colMin = matrix[i][j];
                }
            }
            if (colMin != Double.MAX_VALUE && colMin > 0) {
                reductionCost += colMin;
                for (int i = 0; i < n; i++) {
                        matrix[i][j] -= colMin;

                }
            }
        }
        return reductionCost;
    }

    public  static  List<Integer> reconstructPath(int[][] parent, int n){
        List<Integer> path=new ArrayList<>();
        int mask=1;
        int curr=0;
        path.add(curr);
        while (mask!=(1<< n)-1){
            int next=parent[mask][curr];
            path.add(next);
            mask|=(1<<next);
            curr=next;
        }
        path.add(0);
        return path;


    }

    public String heldKarp(Double[][] matrix){
        Integer n= matrix.length;
        int numStates=1<<n;
        Double[][] memo= new Double[1<<n][n];
        int[][] parent =new int[1<<n][n];

        for (Double[] row:memo){
            Arrays.fill(row,-1.0);
        }

        double optimalCost = tspRec(1, 0, matrix, memo, parent, n);
        List<Integer> path =reconstructPath(parent,n);
        StringBuilder pathString=new StringBuilder();
        for (int i=0;i< path.size();i++){
            pathString.append(path.get(i));
            if (i < path.size() - 1){
                pathString.append(" -> ");
            }
        }
        return pathString.toString();


    }

    public Double tspRec(int mask, int u , Double[][] matrix, Double[][] memo, int[][] parent,int n){
        if (mask==(1<<n)-1){
            return matrix[u][0];
        }
        if (memo[mask][u]>=0.0) {
            return memo[mask][0];
        }
        Double minCost=Double.MAX_VALUE;
        int nextBestCity=-1;
        for (int v=0;v<n;v++){
            if ((mask &(1<<v))==0){
                int nextMask=mask|(1<<v);
                double subProblemCost = tspRec(nextMask, v, matrix, memo, parent, n);
                if (subProblemCost!=Double.MAX_VALUE){
                    Double totalCost=subProblemCost+matrix[u][v];

                    if (totalCost<minCost){
                        minCost=totalCost;
                        nextBestCity=v;
                    }
                }
            }
        }
        parent[mask][u] = nextBestCity;
        return memo[mask][u] = minCost;

    }






    public List<List<Double>> optimalPath(Double[][] matrix, List<List<Double>> positions){
        int n = matrix.length;
        PriorityQueue<Node> pq=new PriorityQueue<>();

        Double[][] rootMatrix= copyMatrix(matrix);
        Double reduceCost=reduceMatrix(matrix);
        Node root=new Node(rootMatrix,reduceCost,0,0,new ArrayList<>(List.of(0 )));
        pq.add(root);
        Double minCost=Double.MAX_VALUE;
        List<Integer> bestPath=null;
        while (!pq.isEmpty()){
            Node current = pq.poll();
            if (current.cost>=minCost) continue;
            if (current.level==n-1){
                Double returnEdge=current.reducedMatrix[current.currentLocation][0];
                Double finalCost= current.cost+returnEdge;
                if (finalCost<minCost){
                    minCost=finalCost;
                    bestPath=new ArrayList<>(current.path);
                    bestPath.add(0);
                }
                continue;
            }
            for (int nextLocation=0;nextLocation<n;nextLocation++){
                if (current.reducedMatrix[current.currentLocation][nextLocation]!=BLOCK){
                    Double[][] childMatrix=copyMatrix(current.reducedMatrix);
                    Double edgeWeight= childMatrix[current.currentLocation][nextLocation];

                    for (int k =0; k<n;k++){
                        childMatrix[current.currentLocation][k]=BLOCK;
                        childMatrix[k][nextLocation]=BLOCK;
                    }
                    childMatrix[nextLocation][0]=BLOCK;
                    Double reductionCost=reduceMatrix(childMatrix);
                    Double childCost= current.cost+edgeWeight+reductionCost;
                    if (childCost<minCost){
                        List<Integer> newPath = new ArrayList<>(current.path);
                        newPath.add(nextLocation);
                        pq.add(new Node(childMatrix,childCost,nextLocation, current.level+1,newPath ));
                    }
                }
            }
        }
        if (bestPath == null) return null;
        return bestPath.stream()
                .map(index -> {
                    List<Double> pos = positions.get(index);
                    return List.of(pos.get(1), pos.get(0));  // swap back to [lat, lon] for response
                })
                .toList();
    }
}
