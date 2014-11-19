package indexation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import evaluation.*;
import models.*;
import dataClass.DocScore;
import dataClass.Query;

public class testClass {
    public static void main(String[] args) throws IOException {
        String s1 = "cacm", s2 = "cisi";
        //readTest();
        //randomAccessFileTest();
        //writeTest();
        //test();
        //serializeTest();
        //readSerilizeTest();
        //vectorModelTest();
        //queryParserTest();
        //vectorTest("cacm");
        //vectorTest("cisi");
        //languageModelTest(s2);
        //languageModelTest(s1);
        okapiTest(s1);
        //pageRankTest(s1);
    }

    private static void test(){
        HashMap<String, Integer> hTest = new HashMap<String, Integer>();
        hTest.put("test", 1);
        int haha = hTest.get("haha");
        System.out.println(haha);
    }

    private static void HITStest(String data) throws IOException {
        String queryFile = "Data/" + data + "/" + data + ".qry", resultFile = "Data/" + data + "/" + data + ".rel";
        Index index = getIndex(data);

        RandomAccessFile queryFileAccess = new RandomAccessFile(queryFile, "r");
        RandomAccessFile resultFileAccess = new RandomAccessFile(resultFile, "r");
        QueryParser parser = null;
        if (data.equals("cacm"))
            parser = new CacmQueryParser(queryFileAccess, resultFileAccess);
        else if (data.equals("cisi"))
            parser = new CisiQueryParser(queryFileAccess, resultFileAccess);

        Weighter weighterTFIDF = new WeighterBM25(index);
        IRmodel iRmodel = new Okapi(weighterTFIDF);

        RandomWalk randomWalk = new HITS();
        randomWalk.setNIteration(10);
        randomWalk.setIndex(index);
        randomWalk.setNPointed(10);
        randomWalk.setNSeed(30);
        iRmodel.setRandomWalk(randomWalk);
        EvalIRModel evalIRModel = new EvalIRModel();
        evalIRModel.setPrint(false);
        evalIRModel.setiRmodel(iRmodel);
        evalIRModel.setQueryParser(parser);
        evalIRModel.testAllEvaluation();

    }

    private static void pageRankTest(String data) throws IOException {
        String queryFile = "Data/" + data + "/" + data + ".qry", resultFile = "Data/" + data + "/" + data + ".rel";
        Index index = getIndex(data);

        RandomAccessFile queryFileAccess = new RandomAccessFile(queryFile, "r");
        RandomAccessFile resultFileAccess = new RandomAccessFile(resultFile, "r");
        QueryParser parser = null;
        if (data.equals("cacm"))
            parser = new CacmQueryParser(queryFileAccess, resultFileAccess);
        else if (data.equals("cisi"))
            parser = new CisiQueryParser(queryFileAccess, resultFileAccess);

        Weighter weighterTFIDF = new WeighterBM25(index);
        IRmodel iRmodel = new Okapi(weighterTFIDF);
        PageRank randomWalk = new PageRank();
        //RandomWalk randomWalk = new HITS();
        randomWalk.setNIteration(10);
        randomWalk.setIndex(index);
        randomWalk.setNPointed(10);
        randomWalk.setNSeed(30);
        iRmodel.setRandomWalk(randomWalk);
        EvalIRModel evalIRModel = new EvalIRModel();
        evalIRModel.setPrint(false);
        evalIRModel.setiRmodel(iRmodel);
        evalIRModel.setQueryParser(parser);
        evalIRModel.testAllEvaluation();
    }

    private static void okapiTest(String data) throws IOException {

        String queryFile = "Data/" + data + "/" + data + ".qry", resultFile = "Data/" + data + "/" + data + ".rel";
        Index index = getIndex(data);

        RandomAccessFile queryFileAccess = new RandomAccessFile(queryFile, "r");
        RandomAccessFile resultFileAccess = new RandomAccessFile(resultFile, "r");
        QueryParser parser = null;
        if (data.equals("cacm"))
            parser = new CacmQueryParser(queryFileAccess, resultFileAccess);
        else if (data.equals("cisi"))
            parser = new CisiQueryParser(queryFileAccess, resultFileAccess);

        Weighter weighterTFIDF = new WeighterBM25(index);
        IRmodel iRmodel = new Okapi(weighterTFIDF);
        EvalIRModel evalIRModel = new EvalIRModel();
        evalIRModel.setPrint(false);
        evalIRModel.setiRmodel(iRmodel);
        evalIRModel.setQueryParser(parser);
        evalIRModel.testAllEvaluation();

    }

    private static void languageModelTest(String data) throws IOException{

        for (int i = 1; i < 10; i++) {
            String queryFile = "Data/" + data + "/" + data + ".qry", resultFile = "Data/" + data + "/" + data + ".rel";
            Index index = getIndex(data);

            RandomAccessFile queryFileAccess = new RandomAccessFile(queryFile, "r");
            RandomAccessFile resultFileAccess = new RandomAccessFile(resultFile, "r");
            QueryParser parser = null;
            if (data.equals("cacm"))
                parser = new CacmQueryParser(queryFileAccess, resultFileAccess);
            else if (data.equals("cisi"))
                parser = new CisiQueryParser(queryFileAccess, resultFileAccess);

            WeighterTFIDF weighterTFIDF = new WeighterTFIDF(index);
            weighterTFIDF.setMODEL(3);
            LanguageModel iRmodel = new LanguageModel(weighterTFIDF);
            iRmodel.setTotalModel(index.getStemsIdfs());
            iRmodel.setLambda(i / 10.0f);
            System.out.println("====================lambda:" + i / 10.0f + "====================");
            EvalIRModel evalIRModel = new EvalIRModel();
            evalIRModel.setPrint(false);
            evalIRModel.setiRmodel(iRmodel);
            evalIRModel.setQueryParser(parser);
            //System.out.println("Average Precision: " + evalIRModel.getAverageScore());
            evalIRModel.testAllEvaluation();
        }
    }

    private static void vectorTest(String data) throws IOException{

        String queryFile = "Data/" + data + "/" + data + ".qry", resultFile = "Data/" + data + "/" + data + ".rel";
        Index index = getIndex(data);

        RandomAccessFile queryFileAccess = new RandomAccessFile(queryFile, "r");
        RandomAccessFile resultFileAccess = new RandomAccessFile(resultFile, "r");
        QueryParser parser = null;
        if (data.equals("cacm"))
            parser = new CacmQueryParser(queryFileAccess, resultFileAccess);
        else if (data.equals("cisi"))
            parser = new CisiQueryParser(queryFileAccess, resultFileAccess);

        WeighterTFIDF weighterTFIDF = new WeighterTFIDF(index);
        weighterTFIDF.setMODEL(3);
        Vector iRmodel = new Vector(weighterTFIDF);
        iRmodel.setNormalized(true);
        EvalIRModel evalIRModel = new EvalIRModel();
        evalIRModel.setPrint(false);
        evalIRModel.setiRmodel(iRmodel);
        evalIRModel.setQueryParser(parser);
        evalIRModel.testAllEvaluation();
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
        Index index = getIndex("cacm");
        WeighterTFIDF weighterTFIDF = new WeighterTFIDF(index);
        Vector vector = new Vector(weighterTFIDF);
        ArrayList<DocScore> docScores = vector.getRanking(q.toQueryHash());
        int size = q.relevants.size();
        for (int i = 0; i < size; i++) {
            System.out.println("Query: " + q.relevants.get(i) + ", Search: " + docScores.get(i).doc);
        }
    }

    private static void vectorModelTest() throws IOException {
        Index index = getIndex("cacm");
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

//    private static void serializeTest() throws IOException {
//        String cfile = "Data/cacm/cacm.txt";
//        Index i = new Index();
//        i.setName("cacm");
//        i.setStemmer(new Stemmer());
//        RandomAccessFile input = new RandomAccessFile(cfile, "r");
//        CacmParser cacmParser = new CacmParser(input, 0);
//        i.setParser(cacmParser);
//        i.indexation();
//
//        File file = new File("Index.out");
//        ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(file));
//        oout.writeObject(i);
//        oout.close();
//    }
//
//    private static Index getIndex() throws IOException {
//        File file = new File("Index.out");
//        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
//        Index i = null;
//        try {
//            i = (Index)(oin.readObject());
//            return i;
//        } catch (Exception e) {
//            e.printStackTrace();
//            serializeTest();
//            return getIndex();
//        }
//    }


    private static void serializeTest(String data) throws IOException {
        String cfile = "Data/" + data + "/" + data + ".txt";
        Index i = new Index();
        i.setName(data);
        i.setStemmer(new Stemmer());
        RandomAccessFile input = new RandomAccessFile(cfile, "r");
        Parser parser = null;
        if (data.equals("cacm"))
            parser = new CacmParser(input, 0);
        else if (data.equals("cisi"))
            parser = new CisiParser(input, 0);

        i.setParser(parser);
        i.indexation();
        String out = "Index_" + data + ".out";
        File file = new File(out);
        ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(file));
        oout.writeObject(i);
        oout.close();
    }


    private static Index getIndex(String data) throws IOException {

        try {
            String out = "Index_" + data + ".out";
            File file = new File(out);
            ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
            return (Index)(oin.readObject());
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Data file not exist, re-serialise");
            serializeTest(data);
            return getIndex(data);
        }
    }


    private static void readSerialiseTest() throws  IOException {
        File file = new File("Index.out");
        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
        Index i;
        try {
            i = (Index)(oin.readObject());
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Data file not exist");
            return;
        }

        for (int j = 1; j < 10; j++) {
            System.out.println(i.getTfsForDoc(j));
        }
    }
}
