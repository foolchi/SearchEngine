package indexation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import models.*;
import dataClass.DocScore;
import dataClass.Query;
import evaluation.CacmQueryParser;
import evaluation.EvalMeasure;
import evaluation.EvalPrecisionRappel;
import evaluation.EvalAveragePrecision;
import evaluation.IRList;
import evaluation.EvalIRModel;
import sun.jvm.hotspot.debugger.Page;


public class testClass {
    public static void main(String[] args) throws IOException {
        //readTest();
        //randomAccessFileTest();
        //writeTest();
        //test();
        //serializeTest();
        //readSerilizeTest();
        //vectorModelTest();
        //queryParserTest();
        //queryEval();
        //okapiTest();
        pageRankTest();
    }

    private static void test(){
        HashMap<String, Integer> hTest = new HashMap<String, Integer>();
        hTest.put("test", 1);
        int haha = hTest.get("haha");
        System.out.println(haha);
    }

    private static void pageRankTest() throws IOException {
        String queryFile = "Data/cacm/cacm.qry", resultFile = "Data/cacm/cacm.rel";
        RandomAccessFile queryFileAccess = new RandomAccessFile(queryFile, "r");
        RandomAccessFile resultFileAccess = new RandomAccessFile(resultFile, "r");
        CacmQueryParser parser = new CacmQueryParser(queryFileAccess, resultFileAccess);
        Index index = getIndex();
        Weighter weighterTFIDF = new WeighterBM25(index);
        //Vector vector = new Vector(weighterTFIDF);
        IRmodel iRmodel = new Okapi(weighterTFIDF);
        PageRank randomWalk = new PageRank();
        randomWalk.setNIteration(10);
        randomWalk.setIndex(index);
        randomWalk.setNPointed(10);
        randomWalk.setNSeed(20);
        iRmodel.setRandomWalk(randomWalk);
        //iRmodel.setTotalModel(index.getStemsIdfs());
        //EvalMeasure evalMeasure = new EvalPrecisionRappel(10);
        EvalMeasure evalMeasure  = new EvalAveragePrecision();
        EvalIRModel evalIRModel = new EvalIRModel();
        evalIRModel.setPrint(true);
        evalIRModel.setiRmodel(iRmodel);
        evalIRModel.setEvalMeasure(evalMeasure);
        evalIRModel.setQueryParser(parser);
        System.out.println("Average Precision: " + evalIRModel.getAverageScore());
    }

    private static void okapiTest() throws IOException {
        String queryFile = "Data/cacm/cacm.qry", resultFile = "Data/cacm/cacm.rel";
        RandomAccessFile queryFileAccess = new RandomAccessFile(queryFile, "r");
        RandomAccessFile resultFileAccess = new RandomAccessFile(resultFile, "r");
        CacmQueryParser parser = new CacmQueryParser(queryFileAccess, resultFileAccess);
        Index index = getIndex();
        Weighter weighterTFIDF = new WeighterBM25(index);
        //Vector vector = new Vector(weighterTFIDF);
        IRmodel iRmodel = new Okapi(weighterTFIDF);
        //iRmodel.setTotalModel(index.getStemsIdfs());
        //EvalMeasure evalMeasure = new EvalPrecisionRappel(10);
        EvalMeasure evalMeasure  = new EvalAveragePrecision();
        EvalIRModel evalIRModel = new EvalIRModel();
        evalIRModel.setPrint(true);
        evalIRModel.setiRmodel(iRmodel);
        evalIRModel.setEvalMeasure(evalMeasure);
        evalIRModel.setQueryParser(parser);
        System.out.println("Average Precision: " + evalIRModel.getAverageScore());
    }
    private static void queryEval() throws IOException{
        String queryFile = "Data/cacm/cacm.qry", resultFile = "Data/cacm/cacm.rel";
        RandomAccessFile queryFileAccess = new RandomAccessFile(queryFile, "r");
        RandomAccessFile resultFileAccess = new RandomAccessFile(resultFile, "r");
        CacmQueryParser parser = new CacmQueryParser(queryFileAccess, resultFileAccess);
        Index index = getIndex();
        WeighterTFIDF weighterTFIDF = new WeighterTFIDF(index);
        //Vector vector = new Vector(weighterTFIDF);
        LanguageModel iRmodel = new LanguageModel(weighterTFIDF);
        iRmodel.setTotalModel(index.getStemsIdfs());
        //EvalMeasure evalMeasure = new EvalPrecisionRappel(10);
        EvalMeasure evalMeasure  = new EvalAveragePrecision();
        EvalIRModel evalIRModel = new EvalIRModel();
        evalIRModel.setPrint(true);
        evalIRModel.setiRmodel(iRmodel);
        evalIRModel.setEvalMeasure(evalMeasure);
        evalIRModel.setQueryParser(parser);

        /*
        int nQuery = 0;
        float sumPrecision = 0;
        Query q = parser.nextQuery();
        while (q != null) {
            //System.out.println(q.toString());
            nQuery ++;
            ArrayList<DocScore> docScores = vector.getRanking(q.toQueryHash());
            ArrayList<Integer> docs = new ArrayList<Integer>();
            for (int i = 0; i < docScores.size(); i++){
                docs.add((int)docScores.get(i).doc);
            }
            IRList irList = new IRList(q, docs);
            float currentPrecision = evalMeasure.eval(irList);
            sumPrecision += currentPrecision;
            System.out.println(currentPrecision);
            q = parser.nextQuery();
        }
*/
        System.out.println("Average Precision: " + evalIRModel.getAverageScore());
    }

    private static void queryParserTest() throws IOException {
        String queryFile = "Data/cacm/cacm.qry", resultFile = "Data/cacm/cacm.rel";
        RandomAccessFile queryFileAccess = new RandomAccessFile(queryFile, "r");
        RandomAccessFile resultFileAccess = new RandomAccessFile(resultFile, "r");
        CacmQueryParser parser = new CacmQueryParser(queryFileAccess, resultFileAccess);

        Query q = parser.nextQuery();
        //q = parser.nextQuery();
        //q = parser.nextQuery();
        /*
        while (q != null) {
            System.out.println(q.toString());
            q = parser.nextQuery();
        }
        */
        System.out.println(q.toString());
        Index index = getIndex();
        WeighterTFIDF weighterTFIDF = new WeighterTFIDF(index);
        Vector vector = new Vector(weighterTFIDF);
        ArrayList<DocScore> docScores = vector.getRanking(q.toQueryHash());
        int size = q.relevants.size();
        for (int i = 0; i < size; i++) {
            System.out.println("Query: " + q.relevants.get(i) + ", Search: " + docScores.get(i).doc);
        }
    }

    private static void vectorModelTest() throws IOException {
        Index index = getIndex();
        if (index == null) {
            System.out.println("Index not found!");
            return;
        }

        WeighterTFIDF weighterTFIDF = new WeighterTFIDF(index);
        Vector vector = new Vector(weighterTFIDF);
        HashMap<String, Integer> query = getQuery();
        ArrayList<DocScore> docScores = vector.getRanking(query);
        for (int i = 0; i < 10 && i < docScores.size(); i++) {
            System.out.println("Doc: " + docScores.get(i).doc + ", Score: " + docScores.get(i).score);
        }
    }

    private static HashMap<String, Integer> getQuery() {
        HashMap<String, Integer> query = new HashMap<String, Integer>();
        query.put("perli", 1);
        query.put("report", 1);
        query.put("intern", 1);
        query.put("languag", 1);
        return query;

    }

    private static void randomAccessFileTest() throws IOException {
        String file = "raf.txt";
        RandomAccessFile input = new RandomAccessFile(file, "rw");
        input.write("Hello world".getBytes());
        input.writeChar('\n');
        input.seek(0);
        System.out.println(input.readLine());
    }

    private static void readTest() throws IOException {
        String cfile = "cacm_index";
        RandomAccessFile input = new RandomAccessFile(cfile, "r");

        for (int i = 0; i < 10; i++){
            System.out.println(input.readLine());
        }
    }



    private static void writeTest() throws IOException {
        String cfile = "Data/cacm/cacm.txt";
        Index i = new Index();
        i.setName("cacm");
        i.setStemmer(new Stemmer());
        RandomAccessFile input = new RandomAccessFile(cfile, "r");
        CacmParser cacmParser = new CacmParser(input, 0);
        i.setParser(cacmParser);
        i.indexation();
        ArrayList<String> aString = new ArrayList<String>();

        System.out.println("stem-tf");
        for (int j = 1; j < 10; j++) {
            for (String s : i.getTfsForDoc(j).keySet()) {
                if (!(aString.contains(s))){
                    aString.add(s);
                }
            }
            System.out.println(i.getTfsForDoc(j));
        }
        System.out.println("tf-stem");
        for (String s : aString) {
            System.out.println(s);
            System.out.println(i.getTfsForStem(s));
        }

        //i.printAllIndex();
    }

    private static void serializeTest() throws IOException {
        String cfile = "Data/cacm/cacm.txt";
        Index i = new Index();
        i.setName("cacm");
        i.setStemmer(new Stemmer());
        RandomAccessFile input = new RandomAccessFile(cfile, "r");
        CacmParser cacmParser = new CacmParser(input, 0);
        i.setParser(cacmParser);
        i.indexation();

        File file = new File("Index.out");
        ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(file));
        oout.writeObject(i);
        oout.close();
    }

    private static Index getIndex() throws IOException {
        File file = new File("Index.out");
        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
        Index i = null;
        try {
            i = (Index)(oin.readObject());
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            i = null;
        }
        if (i == null) {
            serializeTest();
            return getIndex();
        }
        return i;
    }
    private static void readSerilizeTest() throws  IOException {
        File file = new File("Index.out");
        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
        Index i = null;
        try {
            i = (Index)(oin.readObject());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        for (int j = 1; j < 10; j++) {
            System.out.println(i.getTfsForDoc(j));
        }
    }
}
