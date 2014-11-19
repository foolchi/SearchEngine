package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import indexation.Index;
import java.lang.Math;
/**
 * Created by foolchi on 22/10/14.
 * TF-IDF model
 *
 * 1:
 * w(t,d) = tf(t,d)
 * w(t,q) = 1 if t in q else 0
 * 2:
 * w(t,d) = tf(t,d)
 * w(t,q) = tf(t,q)
 * 3:
 * w(t,d) = tf(t,d)
 * w(t,q) = idf(t) if t in q else 0
 * 4:
 * w(t,d) = log(1+tf(t,d)) if t in d else 0
 * w(t,q) = idf(t) if t in q else 0
 * 5:
 * w(t,d) = log(1+tf(t,d))*idf(t) if t in d else 0
 * w(t,q) = log(1+tf(t,q)*idf(t) if t in q else 0
 */
public class WeighterTFIDF extends Weighter {

    private Index index;
    private int MODEL;
    private int totalDoc;
    public WeighterTFIDF (Index index) {
        this.index = index;
        totalDoc = index.getTotalDoc();
        MODEL = 1;
    }

    public void setMODEL(int model){
        MODEL = model;
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
                float wtd;
                switch (MODEL){
                    case 1:
                    case 2:
                    case 3:
                        wtd =  (float)(doc.get(key)) / total;
                        break;
                    case 4:
                        wtd = (float)Math.log10((float)(doc.get(key)) / total);
                        break;
                    case 5:
                        wtd = (float)Math.log10((float)(doc.get(key)) / total) * index.getIdfsForStem(key);
                        break;
                    default:
                        wtd = (float)(doc.get(key)) / total;
                        break;
                }
                weights.put(key,wtd);
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
            float total = 0.0f;
            for (int n : query.values()){
                total += n;
            }
            if (total == 0)
                total = 1.0f;
            for (String stem : query.keySet()) {
                float wtq;
                switch (MODEL){
                    case 1:
                        wtq = 1.0f;
                        break;
                    case 2:
                        wtq = query.get(stem) / total;
                        break;
                    case 3:
                    case 4:
                        wtq = index.getIdfsForStem(stem);
                        break;
                    case 5:
                        wtq = (float)Math.log10(query.get(stem)/total) * index.getIdfsForStem(stem);
                        break;
                    default:
                        wtq = 1.0f;

                }
                weights.put(stem, wtq);
            }
            return  weights;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
