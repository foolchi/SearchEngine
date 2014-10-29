package dataClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by foolchi on 29/10/14.
 */
public class Graph {
    private HashMap<Long, ArrayList<Long>> pointedGraph, reversedGraph;
    private Set<Long> validIds;
    public Graph(){
        pointedGraph = new HashMap<Long, ArrayList<Long>>();
        reversedGraph = new HashMap<Long, ArrayList<Long>>();
    }

    public HashMap<Long, Integer> getNPoints(){
        HashMap<Long, Integer> nPoints = new HashMap<Long, Integer>();
        for (Long id : validIds){
            nPoints.put(id, pointedGraph.get(id).size());
        }
        return nPoints;
    }

    public Graph(HashMap<Long, ArrayList<Long>> pointedGraph) {
        this.pointedGraph = pointedGraph;
        cleanGraph();
        generateReversedGraph();
    }

    public void addGraph(long id, ArrayList<Long> points) {
        if (pointedGraph.containsKey(id)){
            pointedGraph.get(id).addAll(points);
        } else {
            pointedGraph.put(id, points);
        }
    }

    public ArrayList<Long> getPoints(long id) {
        return pointedGraph.get(id);
    }

    public ArrayList<Long> getReversedPoints(long id) {
        return reversedGraph.get(id);
    }

    public void generateReversedGraph(){
        Set<Long> allIds = new HashSet<Long>();
        for (ArrayList<Long> points : pointedGraph.values()) {
            allIds.addAll(points);
        }
        System.out.println("All ID: " + allIds);
        for (long id : allIds){
            ArrayList<Long> reversedPoint = new ArrayList<Long>();
            for (long pointedId : pointedGraph.keySet()) {
                if (pointedGraph.get(pointedId).contains(id)) {
                    reversedPoint.add(pointedId);
                }
            }
            reversedGraph.put(id, reversedPoint);
        }
    }

    public void cleanGraph(){
        Set<Long> allIds = new HashSet<Long>();
        validIds = new HashSet<Long>();
        for (ArrayList<Long> points : pointedGraph.values()) {
            allIds.addAll(points);
        }
        for (Long id : validIds){
            if (pointedGraph.containsKey(id)){
                validIds.add(id);
            }
        }
        Set<Long> toBeRemoved = new HashSet<Long>();
        for (Long id : pointedGraph.keySet()){
            if (!(validIds.contains(id))){
                toBeRemoved.add(id);
                continue;
            }
            ArrayList<Long> pointed = pointedGraph.get(id), validPointed = new ArrayList<Long>();
            for (Long pointedId : pointed){
                if (validIds.contains(pointedId)){
                    validPointed.add(pointedId);
                }
            }

            pointedGraph.put(id, validPointed);
        }
        for (Long id : toBeRemoved){
            pointedGraph.remove(id);
        }
    }

    public void setValidIds(Set<Long> validIds){
        this.validIds = validIds;
        cleanGraph();
        generateReversedGraph();
    }

    public int getGraphSize(){
        return pointedGraph.size();
    }
    public Set<Long> getValidIds(){
        return validIds;
    }

}
