package models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by foolchi on 22/10/14.
 */
public abstract class Weighter {
    public abstract HashMap<String, Float> getDocWeightsForDoc (long idDoc);
    public abstract HashMap<Long, Float> getDocWeightsForStem (String stem);
    public abstract HashMap<String, Float> getWeightsForQuery (HashMap<String, Integer> query);
    public abstract ArrayList<Long> getAllIds();
}
