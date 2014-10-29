package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import indexation.Index;
import java.lang.Math;
/**
 * Created by foolchi on 22/10/14.
 */
public class WeighterTFIDF extends Weighter {

    private Index index;
    private int totalDoc;
    public WeighterTFIDF (Index index) {
        this.index = index;
        totalDoc = index.getTotalDoc();
    }

    public ArrayList<Long> getAllIds() {
        return index.getAllIds();
    }

    @Override
    public HashMap<String, Float> getDocWeightsForDoc(long idDoc) {
        try {
            HashMap<String, Integer> doc = index.getTfsForDoc(idDoc);
            int total = 0;
            for (int v : doc.values()) {
                total += v;
            }
            HashMap<String, Float> weights = new HashMap<String, Float>();
            for (String key : doc.keySet()) {
                weights.put(key, (float)(doc.get(key)) / total);
            }
            return weights;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Doc ID not found!");
            return null;
        }
    }

    @Override
    public HashMap<Long, Float> getDocWeightsForStem(String stem) {
        try {
            HashMap<Long, Integer> stems = index.getTfsForStem(stem);
            HashMap<Long, Float> weights = new HashMap<Long, Float>();
            for (Long docId : stems.keySet()) {
                weights.put(docId, getDocWeightsForDoc(docId).get(stem));
            }
            return weights;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HashMap<String, Float> getWeightsForQuery(HashMap<String, Integer> query) {
        try {
            HashMap<String, Float> weights = new HashMap<String, Float>();
            for (String stem : query.keySet()) {
                weights.put(stem, query.get(stem) * index.getIdfsForStem(stem));
            }
            return  weights;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
