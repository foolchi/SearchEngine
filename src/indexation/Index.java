package indexation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 3405309 on 25/09/14.
 * Indexation for input
 */
public class Index implements Serializable{

    public Index() throws IOException{
        docs = new HashMap<Long, Pair>();
        stems = new HashMap<String, Pair>();

        pointedGraph = new HashMap<Long, ArrayList<Long>>();
        //docFrom = new HashMap<Long, DocFrom>();
    }

    private String name;
    transient private RandomAccessFile index, inverted;
    private HashMap<Long, Pair> docs;
    private HashMap<String, Pair> stems;
    private HashMap<Long, ArrayList<Long>> pointedGraph;
    private int totalDoc;
    private HashMap<Long, HashMap<String, Integer>> docTfs;
    private HashMap<String, HashMap<Long, Integer>> stemTfs;



    private HashMap<String, Float> stemsIdfs;
    //private HashMap<Long, DocFrom> docFrom;

    transient private Parser parser;
    transient private Stemmer stemmer;

    public void indexation() throws IOException{
        Document d = parser.nextDocument();
        totalDoc = 0;
        HashMap<String, ArrayList<Pair>> stemsTemp = new HashMap<String, ArrayList<Pair>>();
        ArrayList<String> allKeys = new ArrayList<String>();
        HashMap<String, Integer> hm;
        try {
            index = new RandomAccessFile(name + "_index", "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        long current = 0;
        while (d != null) {
            totalDoc ++;
            try {
                current = index.getFilePointer();
            } catch (IOException e) {
                e.printStackTrace();
            }

            hm = stemmer.porterStemmerHash(d.toString());
            for (String key : hm.keySet()){
                if (key.equals(" * "))
                    continue;
                try {
                    if (!(allKeys.contains(key)))
                        allKeys.add(key);
                    if (!(stemsTemp.containsKey(key))) {
                        stemsTemp.put(key, new ArrayList<Pair>());
                    }
                    stemsTemp.get(key).add(new Pair(d.getId(), hm.get(key)));

                    index.write(key.getBytes());
                    index.writeChar('\n');
                    index.write(hm.get(key).toString().getBytes());
                    index.writeChar('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("doc id : " + d.getId());
            pointedGraph.put(d.getId(), d.getX());

            try {
                docs.put(d.getId(), new Pair(current, index.getFilePointer() - current));
            } catch (IOException e) {
                e.printStackTrace();
            }
            d = parser.nextDocument();
        }

        try {
            inverted = new RandomAccessFile(name + "_inverted", "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        for (String s : stemsTemp.keySet()) {
            ArrayList<Pair> pA = stemsTemp.get(s);
            current = inverted.getFilePointer();
            for (Pair p : pA) {
                inverted.write(Long.toString(p.start).getBytes());
                inverted.writeChar('\n');
                inverted.write(Long.toString(p.length).getBytes());
                inverted.writeChar('\n');
            }
            stems.put(s, new Pair(current, inverted.getFilePointer() - current));
        }
        generateTfsForStem();
        generateIdfsForStem();
        generateTfsForDoc();

    }

    private void generateTfsForDoc() throws IOException{
        docTfs = new HashMap<Long, HashMap<String, Integer>>();
        for (Long id : docs.keySet()){
            if (index == null) {
                index = new RandomAccessFile(name + "_index", "rw");
            }
            //RandomAccessFile readFile = new RandomAccessFile(name + "_index", "r");
            HashMap<String, Integer> doc = new HashMap<String, Integer>();
            String word, count;
            int iCount;
            //System.out.println(index.length());
            Pair p = docs.get(id);
            if (p == null){
                System.out.println("Id not found");
                continue;
            }
            long end = p.start + p.length;

            index.seek(p.start);
            while (index.getFilePointer() < end) {
                word = index.readLine().trim();
                count = index.readLine().trim();
                //System.out.println(word + ", " + count);
                try {
                    iCount = Integer.valueOf(count);
                } catch (NumberFormatException e) {
                    iCount = 0;
                }
                doc.put(word, iCount);
            }
            docTfs.put(id, doc);
        }
    }

    public HashMap<String, Integer> getTfsForDoc(long id) throws IOException {
        return docTfs.get(id);
    }

    public void generateIdfsForStem() throws IOException {
        stemsIdfs = new HashMap<String, Float>();
        for (String stem : stems.keySet()) {
            HashMap<Long, Integer> s = getTfsForStem(stem);
            stemsIdfs.put(stem, (float)Math.log10((1.0f + totalDoc)/(1.0f + s.size())));
            //stemsIdfs.put(stem, (1.0f * totalDoc) / s.size());
        }
    }

    public float getIdfsForStem(String stem) throws IOException {
        //System.out.println(stem);
        //System.out.println(stemsIdfs);
        if (stemsIdfs.containsKey(stem))
            return stemsIdfs.get(stem);

        return 0;
    }

    public void generateTfsForStem() throws IOException{
        stemTfs = new HashMap<String, HashMap<Long, Integer>>();
        for (String s : stems.keySet()){
            if (inverted == null) {
                inverted = new RandomAccessFile(name + "_inverted", "rw");
            }
            HashMap<Long, Integer> stem = new HashMap<Long, Integer>();
            Pair p = stems.get(s);
            if (p == null) {
                System.out.println("Word not found!");
                continue;
            }

            String idString, tfString;
            long id;
            int tf;
            long end = p.start + p.length;

            inverted.seek(p.start);
            while (inverted.getFilePointer() < end) {
                idString = inverted.readLine().trim();
                tfString = inverted.readLine().trim();
                try {
                    id = Long.valueOf(idString);
                    tf = Integer.valueOf(tfString);
                    stem.put(id, tf);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            stemTfs.put(s, stem);
        }
    }
    public HashMap<Long, Integer> getTfsForStem(String s) throws  IOException {
        return stemTfs.get(s);
    }

    public void printAllIndex() throws FileNotFoundException {
        RandomAccessFile printFile = new RandomAccessFile(name + "_index", "r");
        while (true){
            try {
                String line = printFile.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            } catch (IOException e) {
                break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        try {
            index = new RandomAccessFile(name + "_index", "rw");
            inverted = new RandomAccessFile(name + "_inverted", "rw");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot create index and inverted file");
            e.printStackTrace();
        }
    }

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public Stemmer getStemmer() {
        return stemmer;
    }

    public void setStemmer(Stemmer stemmer) {
        this.stemmer = stemmer;
    }

    public RandomAccessFile getIndex() {
        return index;
    }

    public void setIndex(RandomAccessFile index) {
        this.index = index;
    }

    public RandomAccessFile getInverted() {
        return inverted;
    }

    public void setInverted(RandomAccessFile inverted) {
        this.inverted = inverted;
    }

    public HashMap<Long, Pair> getDocs() {
        return docs;
    }

    public void setDocs(HashMap<Long, Pair> docs) {
        this.docs = docs;
    }

    public HashMap<String, Pair> getStems() {
        return stems;
    }

    public void setStems(HashMap<String, Pair> stems) {
        this.stems = stems;
    }

    public int getTotalDoc() {
        return totalDoc;
    }

    public void setTotalDoc(int totalDoc) {
        this.totalDoc = totalDoc;
    }
    public HashMap<String, Float> getStemsIdfs() {
        return stemsIdfs;
    }
    public ArrayList<Long> getAllIds() {
        ArrayList<Long> ids = new ArrayList<Long>();
        for (Long id : docs.keySet()) {
            ids.add(id);
        }
        return ids;
    }

    public HashMap<Long, ArrayList<Long>> getPointedGraph() {
        return pointedGraph;
    }

    /*
    public HashMap<Long, DocFrom> getDocFrom() {
        return docFrom;
    }

    public void setDocFrom(HashMap<Long, DocFrom> docFrom) {
        this.docFrom = docFrom;
    }
    */

    private class Pair implements Serializable{
        public Pair(long start, long length){
            this.start = start;
            this.length = length;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public long start, length;
    }


    private class DocFrom{
        public String file;
        public Pair pair;
    }
}
