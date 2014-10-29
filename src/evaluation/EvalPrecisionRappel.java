package evaluation;


import dataClass.Query;

import java.util.ArrayList;

/**
 * Created by foolchi on 28/10/14.
 * Precision-Rappel Measure
 */
public class EvalPrecisionRappel extends EvalMeasure{

    public EvalPrecisionRappel(){
        nbLevels = Integer.MAX_VALUE;
    }
    public EvalPrecisionRappel(int nbLevels){
        this.nbLevels = nbLevels;
    }
    @Override
    public float eval(IRList l) {
        ArrayList<Integer> relevants = l.getQuery().relevants;
        ArrayList<Integer> pertinence = l.getPertinence();
        int currentLevel = nbLevels, nRelevants = relevants.size(), nPertinence = pertinence.size();
        if (currentLevel > nRelevants)
            currentLevel = nRelevants;
        if (currentLevel > nPertinence)
            currentLevel = nPertinence;
        if (nRelevants == 0)
            return 1;
        if (currentLevel == 0)
            return 0;
        int nHit = 0;
        for (int i = 0; i < currentLevel; i++){
            if (relevants.contains(pertinence.get(i))){
                nHit ++;
            }
        }

        return (float)nHit / currentLevel;
    }
    private int nbLevels;
}
