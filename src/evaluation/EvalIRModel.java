package evaluation;

import dataClass.DocScore;
import indexation.Index;
import models.HITS;
import models.IRmodel;
import dataClass.Query;
import models.PageRank;
import models.RandomWalk;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by foolchi on 28/10/14.
 * Evaluation Class
 */
public class EvalIRModel {
    public EvalIRModel(){isPrint = false;}
    private boolean isPrint;
    private ArrayList<IRList> irLists;
    public void setPrint(boolean isPrint) {
        this.isPrint = isPrint;
    }
    private Query q;
    private final String resultFile = "result.txt";


    private int randomWalkMode = -1; // 0: PageRank, 1: HITS
    public int nIteration, nPointed, nSeed;
    public Index index;

    public void setRandomWalk(int randomWalkMode){
        this.randomWalkMode = randomWalkMode;
    }


    private void saveResults(){
        // Save the result

        if (irLists == null)
            generateSearchResults();
        Writer writer = null;
        try{
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "utf-8"));
            for (IRList irList : irLists){
                writer.write('\n');
                writer.write("Query: " + irList.getQuery().id);
                writer.write('\n');
                ArrayList<Integer> pertinence = irList.getPertinence();
                ArrayList<Integer> relevants = irList.getQuery().relevants;
                writer.write("Relevants: ");
                //writer.write('\n');
                for (int relevant : relevants) {
                    writer.write(String.valueOf(relevant));
                    //writer.write(r);
                    writer.write('\t');
                }
                writer.write('\n');
                writer.write("Pertinences: ");
                //writer.write('\n');
                for (int i = 0; i < pertinence.size() && i < 10; i++){
                    writer.write(String.valueOf(pertinence.get(i)));
                    //writer.write(r);
                    writer.write('\t');
                }
                writer.write('\n');
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try { if (writer != null) writer.close();} catch (Exception e) {}
        }

    }

    public void generateSearchResults(){
        irLists = new ArrayList<IRList>();
        q = queryParser.nextQuery();

//        while (q != null){
//            ArrayList<DocScore> docScores = iRmodel.getRanking(q.toQueryHash());
//                            HITS pageRank = new HITS();
//                        pageRank.setNSeed(nSeed);
//                        pageRank.setNPointed(nPointed);
//                        pageRank.setIndex(index);
//                        pageRank.setNIteration(nIteration);
//            pageRank.setDocScores(docScores);
//            pageRank.run();
//            docScores = pageRank.getRanking();
//
//            ArrayList<Integer> docs = new ArrayList<Integer>();
//            for (DocScore docScore : docScores) {
//                docs.add((int) docScore.doc);
//            }
//            irLists.add(new IRList(q, docs));
//            q = queryParser.nextQuery();
//        }

        // Multi-thread
        ArrayList<Thread> threads = new ArrayList<Thread>();
        final ArrayList<Query> queries = new ArrayList<Query>();
        while (q != null){
            queries.add(q);

            q = queryParser.nextQuery();
        }
        final ArrayList<RandomWalk> randomWalks = new ArrayList<RandomWalk>();
        for (int i = 0; i < queries.size(); i++){
            RandomWalk randomWalk;
            if (randomWalkMode == 0)
                randomWalk = new PageRank();
            else
                randomWalk = new HITS();

            randomWalk.setNSeed(nSeed);
            randomWalk.setNPointed(nPointed);
            randomWalk.setIndex(index);
            randomWalk.setNIteration(nIteration);
            randomWalks.add(randomWalk);
        }

        for (int i = 0; i < queries.size(); i++){
            final int finalI = i;
            Thread t = new Thread(){
                public void run(){
                    ArrayList<DocScore> docScores = iRmodel.getRanking(queries.get(finalI).toQueryHash());
                    if (randomWalkMode != -1){
                        RandomWalk randomWalk = randomWalks.get(finalI);
                        randomWalk.setDocScores(docScores);
                        randomWalk.run();
                        docScores = randomWalk.getRanking();
                    }
                    ArrayList<Integer> docs = new ArrayList<Integer>();
                    for (DocScore docScore : docScores) {
                        docs.add((int) docScore.doc);
                    }
                    irLists.add(new IRList(queries.get(finalI), docs));
                }
            };
            threads.add(t);
            t.start();
        }
        for (Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        saveResults();
    }


    public void testAllEvaluation(){
        if (irLists == null)
            generateSearchResults();
        EvalMeasure evalMeasure;
        System.out.println("=========AveragePrecision=========");
        evalMeasure  = new EvalAveragePrecision();
        setEvalMeasure(evalMeasure);
        getAverageScore();

        for (int i = 1; i <= 9; i++){
            System.out.println("=========PrecisionRappel@" + i +"=========");
            evalMeasure = new EvalPrecisionRappel(i);
            setEvalMeasure(evalMeasure);
            getAverageScore();
        }
    }

    public float getAverageScore(){
        if (irLists == null)
            generateSearchResults();
        int nQuery = 0;
        float sumPrecision = 0;
        ArrayList<Float> precisions = new ArrayList<Float>();
        for (IRList irList : irLists){
            float currentPrecision = evalMeasure.eval(irList);
            if (currentPrecision >= 0){
                if (isPrint) {
                    System.out.println(currentPrecision);
                }
                precisions.add(currentPrecision);
                sumPrecision += currentPrecision;
                nQuery ++;
            }
        }
        if (nQuery == 0){
            System.out.println("No valid query!");
            return 0;
        }
        float average = sumPrecision / nQuery;
        float ecart = 0;
        for (float p : precisions){
            ecart += (p - average) * (p - average);
        }
        ecart = (float)Math.sqrt(ecart / nQuery);
        System.out.println("Average Precision: " + average + ", ecart-type: " + ecart);
        return average;
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
