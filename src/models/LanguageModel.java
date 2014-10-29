package models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by foolchi on 29/10/14.
 */
public class LanguageModel extends IRmodel {
    private Weighter weighter;
    private float lambda;
    private HashMap<String, Float> totalModel;
    public LanguageModel(Weighter weighter) {this.weighter = weighter; lambda = 0.6f;}
    public void setTotalModel(HashMap<String, Float> totalModel) {
        this.totalModel = new HashMap<String, Float>();
        for (String key : totalModel.keySet()) {
            this.totalModel.put(key, (float)Math.pow(10, 1 / totalModel.get(key)));
        }
    }

    @Override
    public HashMap<Long, Float> getScores(HashMap<String, Integer> query) {
        ArrayList<Long> ids = weighter.getAllIds();
        HashMap<Long, Float> docScores = new HashMap<Long, Float>();
        HashMap<String, Float> queryWeight = weighter.getWeightsForQuery(query);
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
                currentScore += Math.log(lambda * currentWeight + (1 - lambda) * totalModel.get(stem));
            }
            docScores.put(id, currentScore);
        }
        return docScores;
    }
}
