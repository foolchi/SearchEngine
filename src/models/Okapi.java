package models;

import java.util.HashMap;

/**
 * Created by foolchi on 29/10/14.
 */
public class Okapi extends IRmodel {

    private Weighter weighter;

    public Okapi(Weighter weighter) {this.weighter = weighter;}

    @Override
    public HashMap<Long, Float> getScores(HashMap<String, Integer> query) {
        HashMap<Long, Float> docScores = new HashMap<Long, Float>();
        for (String stem : query.keySet()) {
            HashMap<Long, Float> stemScores = weighter.getDocWeightsForStem(stem);
            for (Long id : stemScores.keySet()) {
                if (docScores.containsKey(id)){
                    docScores.put(id, docScores.get(id) + stemScores.get(id));
                } else {
                    docScores.put(id, stemScores.get(id));
                }
            }
        }
        return docScores;
    }
}
