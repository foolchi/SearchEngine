package models;

import java.util.ArrayList;
import java.util.HashMap;
import dataClass.DocScore;

/**
 * Created by foolchi on 22/10/14.
 * Abstract Class for search models
 */
public abstract class IRmodel {
    public abstract HashMap<Long, Float> getScores(HashMap<String, Integer> query);

    public void setRandomWalk(RandomWalk randomWalk){
        this.randomWalk = randomWalk;
    }

    public ArrayList<DocScore> getRanking (HashMap<String, Integer> query) {
        HashMap<Long, Float> docScores = getScores(query);
        Long[] ids = new Long[docScores.keySet().size()];
        //System.out.println("id size : " + ids.length);
        int currentPosition = 0;
        for (Long id : docScores.keySet()) {
            ids[currentPosition] = id;
            currentPosition ++;
        }

        int size = ids.length;
        for (int i = 0; i < size -1; i++)
            for (int j = i + 1; j < size; j++) {
                if (docScores.get(ids[i]) < docScores.get(ids[j])) {
                    long temp = ids[i];
                    ids[i] = ids[j];
                    ids[j] = temp;
                }
            }
        ArrayList<DocScore> docResults = new ArrayList<DocScore>();
        for (long id : ids) {
            docResults.add(new DocScore(id, docScores.get(id)));
        }
//        if (randomWalk  != null){
//
//            //System.out.println("RandomWalk");
//            randomWalk.setDocScores(docResults);
//            randomWalk.run();
//            return randomWalk.getRanking();
//        }
        return docResults;
    }
    protected RandomWalk randomWalk;

}
