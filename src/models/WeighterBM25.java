package models;

import indexation.Index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by foolchi on 29/10/14.
 * BM25 weighter
 */
public class WeighterBM25 extends Weighter {

    private Index index;
    private int totalDoc;
    private float k1, b, averageDocLength;
    public WeighterBM25(Index index){
        this.index = index;
        totalDoc = index.getTotalDoc();
        System.out.println("Total Doc: " + totalDoc);
        calcAverageDocLength();
        k1 = 1.5f; b = 0.75f;
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

    private void calcAverageDocLength(){
        try {
            int totalLength = 0;
            ArrayList<Long> ids = getAllIds();
            for (long id : ids) {
                HashMap<String, Integer> doc = index.getTfsForDoc(id);
                for (int freq : doc.values()) {
                    totalLength += freq;
                }
            }
            averageDocLength = (float)totalLength / totalDoc;
            System.out.println("AverageLengh:" + averageDocLength);
        } catch (IOException e) {
            averageDocLength = 0;
            System.out.println("Doc not found");
        }
    }

    @Override
    public HashMap<Long, Float> getDocWeightsForStem(String stem) {
        try {
            HashMap<Long, Float> docWeights = new HashMap<Long, Float>();
            ArrayList<Long> ids = getAllIds();
            HashMap<Long, Integer> stems = index.getTfsForStem(stem);
            if (stems == null){
                return docWeights;
            }
            int relevants = stems.size();
//            System.out.println("relevants: " + relevants);
            for (Long id : ids){
                HashMap<String, Integer> doc = index.getTfsForDoc(id);
                if (doc.containsKey(stem)){
                    float currentDocLength = 0.0f;
                    for (int l : doc.values()) {
                        currentDocLength += l;
                    }
                    float K = k1 * (1 - b + b * currentDocLength / averageDocLength);
                    float idf =  (float)Math.log10((totalDoc - relevants + 0.5) / (relevants + 0.5));
                    if (idf < 0)
                        idf = 0;
                    //float tf = doc.get(stem)/currentDocLength;
                    float tf = doc.get(stem);
//                    if (id == 10){
//                        System.out.println(idf + "*" + "((" + k1 + "+" +  "1)" + "*" + tf + "/" + "(" + K + "+" + tf + "))");
//                    }
                    float currentWeight = idf * ((k1 + 1) * tf / (K + tf));
                    docWeights.put(id, currentWeight);
                }
            }
            return docWeights;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public HashMap<String, Float> getWeightsForQuery(HashMap<String, Integer> query) {
        return null;
    }

    @Override
    public ArrayList<Long> getAllIds() {
        return index.getAllIds();
    }
}
