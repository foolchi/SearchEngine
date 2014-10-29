package evaluation;

import dataClass.Query;
import java.util.ArrayList;

/**
 * Created by foolchi on 28/10/14.
 */
public class IRList {
    public IRList(Query query, ArrayList<Integer> pertinence){
        this.query = query;
        this.pertinence = pertinence;
    }

    public Query getQuery() {
        return query;
    }

    public ArrayList<Integer> getPertinence() {
        return pertinence;
    }

    private Query query;
    private ArrayList<Integer> pertinence;
}
