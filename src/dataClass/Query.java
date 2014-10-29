package dataClass;

import java.util.ArrayList;
import java.util.HashMap;
import indexation.Stemmer;

/**
 * Created by foolchi on 22/10/14.
 */
public class Query {
    public Query(){
        relevants = new ArrayList<Integer>();
        text = "";
    }
    public int id;
    public String text;
    public ArrayList<Integer> relevants;

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append("\n");
        sb.append(text);
        sb.append("\n");
        for (int r : relevants) {
            sb.append(r);
            sb.append(" ");
        }
        return sb.toString();
    }

    public HashMap<String, Integer> toQueryHash() {
        if (text.isEmpty())
            return null;
        Stemmer stemmer = new Stemmer();
        HashMap<String, Integer> stems = stemmer.porterStemmerHash(text);
        if (stems.containsKey(" * ")) {
            stems.remove(" * ");
        }
        return stems;
    }
}
