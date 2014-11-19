package models;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by foolchi on 21/10/14.
 * Vector Model
 */
public class Vector extends IRmodel {
    private Weighter weighter;
    private boolean normalized;

    public Vector(Weighter weighter) {
        this.weighter = weighter;
        normalized = false;
    }

    public void setNormalized(boolean normalized){
        this.normalized = normalized;
    }

    @Override
    public HashMap<Long, Float> getScores(HashMap<String, Integer> query) {
        ArrayList<Long> ids = weighter.getAllIds();
        HashMap<Long, Float> docScores = new HashMap<Long, Float>();
        HashMap<String, Float> queryWeight = weighter.getWeightsForQuery(query);
        float norm = 0;
        if (normalized){
            for (float key : queryWeight.values()){
                norm += key * key;
            }
            norm = (float)Math.sqrt(norm);
            if (norm == 0)
                norm = 1;
        }
        for (Long id : ids) {
            float currentWeight = 0;
            HashMap<String, Float> docWeight = weighter.getDocWeightsForDoc(id);
            for (String stem : queryWeight.keySet()) {
                if (docWeight.containsKey(stem)) {
                    //currentWeight += docWeight.get(stem) * queryWeight.get(stem);
                    //currentWeight += qStem * dStem;
                    currentWeight += queryWeight.get(stem) * docWeight.get(stem);
                }
            }
            if (normalized){
                currentWeight /= (weighter.getDocNorm(id) * norm);
            }
            docScores.put(id, currentWeight);
        }
        return docScores;
    }
}
