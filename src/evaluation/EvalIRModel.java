package evaluation;

import dataClass.DocScore;
import models.IRmodel;
import evaluation.EvalMeasure;
import evaluation.QueryParser;
import dataClass.Query;

import java.util.ArrayList;

/**
 * Created by foolchi on 28/10/14.
 */
public class EvalIRModel {
    public EvalIRModel(){isPrint = false;}
    private boolean isPrint;
    public void setPrint(boolean isPrint) {
        this.isPrint = isPrint;
    }
    public float getAverageScore(){
        int nQuery = 0;
        float sumPrecision = 0;
        Query q = queryParser.nextQuery();
        while (q != null){
            nQuery ++;
            ArrayList<DocScore> docScores = iRmodel.getRanking(q.toQueryHash());
            ArrayList<Integer> docs = new ArrayList<Integer>();
            for (int i = 0; i < docScores.size(); i++){
                docs.add((int)docScores.get(i).doc);
            }
            IRList irList = new IRList(q, docs);
            float currentPrecision = evalMeasure.eval(irList);
            if (isPrint) {
                System.out.println(currentPrecision);
            }
            sumPrecision += currentPrecision;
            q = queryParser.nextQuery();
        }
        if (nQuery == 0)
            return 0;
        return sumPrecision / nQuery;
    }

    public void setiRmodel(IRmodel iRmodel) {
        this.iRmodel = iRmodel;
    }

    public void setEvalMeasure(EvalMeasure evalMeasure) {
        this.evalMeasure = evalMeasure;
    }

    public void setQueryParser(QueryParser queryParser) {
        this.queryParser = queryParser;
    }

    private IRmodel iRmodel;
    private EvalMeasure evalMeasure;
    private QueryParser queryParser;
}
