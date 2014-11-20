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
        HashMap<Long, Float> currentAuthorityScores = new HashMap<Long, Float>(), currentHubsScores = new HashMap<Long, Float>();
        for (int i = 0; i < nIteration; i++){
            for (Long id : validIds){
                ArrayList<Long> reversedId = graph.getReversedPoints(id);
                if (reversedId == null) {
                    currentAuthorityScores.put(id, 0.0f);
                    currentHubsScores.put(id, 0.0f);
                    continue;
                }
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
            float totalAuth = 0.0f, totalHub = 0.0f;
            for (float val : currentAuthorityScores.values()){
                totalAuth += val;
            }
            for (float val : currentHubsScores.values()){
                totalHub += val;
            }
            if (totalAuth <= 0)
                totalAuth = 1;
            if (totalHub <= 0)
                totalHub = 1;

            for (Long id : validIds) {
                scores.put(id, currentAuthorityScores.get(id)/totalAuth);
                hubScores.put(id, currentHubsScores.get(id)/totalHub);
            }
        }
//        float total  = 0.0f;
//        for (float val : scores.values())
//            total += val;
//        System.out.println("Total: " + total);
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
