package models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by foolchi on 22/10/14.
 * Abstract class for weight model
 */
public abstract class Weighter {
    public abstract HashMap<String, Float> getDocWeightsForDoc (long idDoc);
    public abstract HashMap<Long, Float> getDocWeightsForStem (String stem);
    public abstract HashMap<String, Float> getWeightsForQuery (HashMap<String, Integer> query);
    public abstract ArrayList<Long> getAllIds();
    public float getDocNorm(long idDoc){
        if (docNorm == null)
            docNorm = new HashMap<Long, Float>();
        if (docNorm.containsKey(idDoc))
            return docNorm.get(idDoc);
        float norm = 0;
        HashMap<String, Float> docWeights = getDocWeightsForDoc(idDoc);
        for (float val : docWeights.values()){
            norm += val * val;
        }
        if (norm > 0)
            norm = (float)Math.sqrt(norm);
        else
            norm = 1;
        docNorm.put(idDoc, norm);
        return norm;
    }
    protected HashMap<Long, Float> docNorm;
}
