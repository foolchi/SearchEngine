package models;

import dataClass.DocScore;
import dataClass.Graph;
import indexation.Index;

import java.util.*;

/**
 * Created by foolchi on 29/10/14.
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

    public void setDocScores(ArrayList<DocScore> docScores){
        Graph graph = new Graph(index.getPointedGraph());
        Set<Long> validIdsInput = new HashSet<Long>();
        for (int i = 0; i < nSeeds; i++){
            validIdsInput.add(docScores.get(i).doc);
        }
        Set<Long> pointedIds = new HashSet<Long>();
        for (Long id : validIdsInput){
            try {
                pointedIds.addAll(graph.getReversedPoints(id));
            } catch (NullPointerException e){
                System.out.println("Id : " + id + " is empty");
                continue;
            }
        }
        Random rand = new Random();
        int min = nSeeds, max = docScores.size();
        int pointedAdded = 0;
        while (pointedAdded < nPointed) {
            //System.out.println("Random");
            int r = rand.nextInt(max-min) + min;
            Long docId = docScores.get(r).doc;
            if (!(validIdsInput.contains(docId)) && pointedIds.contains(docId)){
                validIdsInput.add(docId);
                pointedAdded ++;
            }
        }
        graph.setValidIds(validIdsInput);
        setGraph(graph);
    }

    public void setIndex(Index index){
        this.index = index;
    }
    public void setNSeed(int nSeeds){
        this.nSeeds = nSeeds;
    }
    public void setNPointed(int nPointed){
        this.nPointed = nPointed;
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
                for (Long pointedId : pointedIds){
                    currentScore += scores.get(pointedId) / nPoints.get(pointedId);
                }
                currentScore *= pRandom;
                currentScore += baseScore;
                nScores.put(id, currentScore);
            }
            scores = nScores;
        }
    }
    private Index index;
    private float pRandom, baseScore;
    private int graphSize, nSeeds, nPointed;
    private Set<Long> validIds;
    private HashMap<Long, Integer> nPoints;
}
