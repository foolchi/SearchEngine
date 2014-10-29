package models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by foolchi on 21/10/14.
 */
public class Vector extends IRmodel {
    private Weighter weighter;
    private boolean normalized;

    public Vector(Weighter weighter) {
        this.weighter = weighter;
    }

    @Override
    public HashMap<Long, Float> getScores(HashMap<String, Integer> query) {
        ArrayList<Long> ids = weighter.getAllIds();
        HashMap<Long, Float> docScores = new HashMap<Long, Float>();
        HashMap<String, Float> queryWeight = weighter.getWeightsForQuery(query);
        for (Long id : ids) {
            float currentWeight = 0;
            HashMap<String, Float> docWeight = weighter.getDocWeightsForDoc(id);
            for (String stem : queryWeight.keySet()) {
                if (docWeight.containsKey(stem)) {
                    //currentWeight += docWeight.get(stem) * queryWeight.get(stem);
                    currentWeight += queryWeight.get(stem) * docWeight.get(stem);
                }
            }
            docScores.put(id, currentWeight);
        }
        return docScores;
    }
}
