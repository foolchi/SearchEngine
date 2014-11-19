package models;

import dataClass.Graph;
import java.util.*;

/**
 * Created by foolchi on 29/10/14.
 * PageRank Model
 */
public class PageRank extends RandomWalk {
    public PageRank(){
        pRandom = 0.5f;
    }

    public PageRank(Graph graph){
        pRandom = 0.5f;
        setGraph(graph);
    }

    public void setGraph(Graph graph){
        this.graph = graph;
        scores = new HashMap<Long, Float>();
        validIds = graph.getValidIds();
        graphSize = graph.getGraphSize();
        float defaultScore = 1.0f / graphSize;
        for (Long id : validIds){
            scores.put(id, defaultScore);
        }
        nPoints = graph.getNPoints();
        baseScore = (1 - pRandom) / graphSize;
    }

    public void setpRandom(float pRandom) {
        this.pRandom = pRandom;
        baseScore = (1 - pRandom) / graphSize;
    }

    @Override
    public void run() {
        for (int i = 0; i < nIteration; i++){
            HashMap<Long, Float> nScores = new HashMap<Long, Float>();
            for (Long id : validIds){
                float currentScore = 0;
                ArrayList<Long> pointedIds = graph.getReversedPoints(id);
                if (pointedIds == null)
                    continue;
                for (Long pointedId : pointedIds){
                    if (scores.containsKey(pointedId))
                        currentScore += scores.get(pointedId) / nPoints.get(pointedId);
                }
                currentScore *= pRandom;
                currentScore += baseScore;
                nScores.put(id, currentScore);
            }
            scores = nScores;
        }
    }
//    private Index index;
//    private int nSeeds, nPointed;
    private float pRandom, baseScore;
    private int graphSize;

    private Set<Long> validIds;
    private HashMap<Long, Integer> nPoints;
}
