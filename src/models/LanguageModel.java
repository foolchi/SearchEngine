package models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by foolchi on 29/10/14.
 * Language Model
 */
public class LanguageModel extends IRmodel {
    private Weighter weighter;
    private float lambda;
    private HashMap<String, Float> totalModel;
    public LanguageModel(Weighter weighter) {this.weighter = weighter; lambda = 0.6f;}
    public void setLambda(float lambda){
        this.lambda = lambda;
    }
    public void setTotalModel(HashMap<String, Float> totalModel) {
        this.totalModel = new HashMap<String, Float>();
        for (String key : totalModel.keySet()) {
            this.totalModel.put(key, 1.0f / (float)Math.pow(10, totalModel.get(key)));
        }
    }

    @Override
    public HashMap<Long, Float> getScores(HashMap<String, Integer> query) {
        float totalQuery = 0.0f;
        for (int val : query.values()){
            totalQuery += val;
        }
        if (totalQuery == 0){
            totalQuery = 1;
        }

        ArrayList<Long> ids = weighter.getAllIds();
        HashMap<Long, Float> docScores = new HashMap<Long, Float>();
        //HashMap<String, Float> queryWeight = weighter.getWeightsForQuery(query);
        for (Long id : ids) {
            float currentScore = 0;
            HashMap<String, Float> docWeight = weighter.getDocWeightsForDoc(id);
            for (String stem : query.keySet()) {
                float currentWeight = 0;
                if (docWeight.containsKey(stem)) {
                    currentWeight = docWeight.get(stem);
                }
                if (!(totalModel.containsKey(stem))){
                    continue;
                }
//                if (id == 1)
//                    System.out.println((query.get(stem)/totalQuery) + "*" + "log(" + lambda + "*" + currentWeight + "+"  + (1 - lambda) + "*" + totalModel.get(stem) + ")");
                currentScore += (query.get(stem)/totalQuery) * Math.log(lambda * currentWeight + (1 - lambda) * totalModel.get(stem));
            }
//            System.out.println("id: " + id + "language score: " + currentScore);
//            currentScore = -1/currentScore;
            docScores.put(id, currentScore);
        }
        return docScores;
    }
}
