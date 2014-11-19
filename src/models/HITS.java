package models;

import dataClass.Graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by foolchi on 18/11/14.
 * HITS Model
 */
public class HITS extends RandomWalk {
    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
        scores = new HashMap<Long, Float>();
        hubScores = new HashMap<Long, Float>();
        validIds = graph.getValidIds();
//        graphSize = graph.getGraphSize();
        float defaultScore = 1.0f;
        for (Long id : validIds){
            scores.put(id, defaultScore);
            hubScores.put(id, defaultScore);
            //System.out.println("Valid id: " + id);
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < nIteration; i++){
            HashMap<Long, Float> currentAuthorityScores = new HashMap<Long, Float>(), currentHubsScores = new HashMap<Long, Float>();

            for (Long id : validIds){
                ArrayList<Long> reversedId = graph.getReversedPoints(id);
                if (reversedId == null)
                    continue;
                //System.out.println("Valid id: " + id);
                float authority = 0.0f, hub = 0.0f;
                for (Long rId : reversedId){
                    if (!(validIds.contains(rId)))
                        continue;
                    if (hubScores.containsKey(rId))
                        authority += hubScores.get(rId);
                    if (scores.containsKey(rId))
                        hub += scores.get(rId);
                }
                currentAuthorityScores.put(id, authority);
                currentHubsScores.put(id, hub);
            }
            scores = normalized(currentAuthorityScores);
            hubScores = normalized(currentHubsScores);
        }
    }

    private HashMap<Long, Float> normalized(HashMap<Long, Float> s){
        HashMap<Long, Float> norm = new HashMap<Long, Float>();
        float total = 0.0f;
        for (float val : s.values()){
            total += val;
        }
        for (Long id : s.keySet()){
            norm.put(id, s.get(id)/total);
        }

        return norm;
    }

//    private int graphSize;
    private HashMap<Long, Float> hubScores;
    private Set<Long> validIds;
}
