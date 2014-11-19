package models;

import dataClass.DocScore;
import dataClass.Graph;
import indexation.Index;

import java.util.*;

/**
 * Created by foolchi on 16/10/14.
 * Abstract random walk model for PageRank and HITS
 */
public abstract class RandomWalk {
    public RandomWalk(){}

    //public abstract void setDocScores(ArrayList<DocScore> docScores);

    public abstract void setGraph(Graph graph);
    public ArrayList<DocScore> getRanking(){
        HashMap<Long, Float> docScores = scores;
        System.out.println("DocScore Size: " + docScores.size());
        Long[] ids = new Long[docScores.keySet().size()];
        int currentPosition = 0;
        for (Long id : docScores.keySet()) {
            ids[currentPosition] = id;
            currentPosition ++;
        }

        int size = ids.length;
        for (int i = 0; i < size -1; i++)
            for (int j = i + 1; j < size; j++) {
                if (docScores.get(ids[i]) < docScores.get(ids[j])) {
                    long temp = ids[i];
                    ids[i] = ids[j];
                    ids[j] = temp;
                }
            }
        ArrayList<DocScore> docResults = new ArrayList<DocScore>();
        for (long id : ids) {
            docResults.add(new DocScore(id, docScores.get(id)));
        }
        return docResults;
    }

    public ArrayList<DocScore> getDocScores(){
        ArrayList<DocScore> docScores = new ArrayList<DocScore>();
        for (Long id : scores.keySet()){
            docScores.add(new DocScore(id, scores.get(id)));
        }
        return docScores;
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
                //System.out.println("Id : " + id + " is empty");

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


    public abstract void run();

    public void setNIteration(int n){
        nIteration = n;
    }
    public void setPrecision(float precision){
        this.precision = precision;
    }

    protected int nIteration, nSeeds, nPointed;
    protected Index index;
    protected float precision;
    protected Graph graph;
    protected HashMap<Long, Float> scores;
}
