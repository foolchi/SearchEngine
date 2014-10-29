package models;

import dataClass.DocScore;
import dataClass.Graph;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by foolchi on 16/10/14.
 */
public abstract class RandomWalk {
    public RandomWalk(){}

    public abstract void setDocScores(ArrayList<DocScore> docScores);

    public ArrayList<DocScore> getRanking(){
        HashMap<Long, Float> docScores = scores;
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

    public abstract void run();

    public void setNIteration(int n){
        nIteration = n;
    }
    public void setPrecision(float precision){
        this.precision = precision;
    }

    protected int nIteration;
    protected float precision;
    protected Graph graph;
    protected HashMap<Long, Float> scores;
}
