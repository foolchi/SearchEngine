package evaluation;

import java.util.ArrayList;

/**
 * Created by foolchi on 28/10/14.
 */
public class EvalAveragePrecision extends EvalMeasure {
    public EvalAveragePrecision(){}

    @Override
    public float eval(IRList l) {
        ArrayList<Integer> relevants = l.getQuery().relevants;
        ArrayList<Integer> pertinence = l.getPertinence();
        int nRelevant = relevants.size(), nPertinence = pertinence.size();
        if (nRelevant == 0)
            return 1;
        float totalPrecision = 0;
        int totalHit = 0;
        for (int i = 0; i < nPertinence; i++){
            if (relevants.contains(pertinence.get(i))){
                totalHit += 1;
                totalPrecision += (float)(totalHit) / (i + 1);
                if (totalHit >= nRelevant) {
                    break;
                }
            }
        }
        if (totalHit == 0)
            return 0;
        return totalPrecision / totalHit;
    }
}
