package indexation;

import java.util.ArrayList;

public class Document {
    // I: Identifiant
    // T: Titre
    // B: Date de publication
    // A: Auteur
    // K: Mots-clé
    // W: Texte du document
    // X: liens du document

    public Document(){
        T = "";
        N = "";
        A = "";
        K = "";
        X = new ArrayList<Long>();
        id = 0;
        start = 0;
        end = 0;
    }

    public Document(String contents){
        this.contents = contents;
        id = -1;
    }
    public Document(String contents, long id){
        this.contents = contents;
        this.id = id;
    }
    public Document(String contents, long id, long start, long end){
        this.contents = contents;
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public long getId(){
        return id;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if (T != null)
            sb.append(T);
        if (A != null)
            sb.append(A);
        if (K != null)
            sb.append(K);
        if (W != null)
            sb.append(W);

        return sb.toString();
        //return contents;
    }

    public long nextOffset(){
        return end;
    }
    public long startOffset(){
        return start;
    }

    public String getT() {
        return T;
    }

    public void setT(String t) {
        T = t;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public ArrayList<Long> getX() {
        return X;
    }

    public void setX(ArrayList<Long> x) {
        X = x;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getK() {
        return K;
    }

    public String getW() {
        return W;
    }

    public void setW(String w) {
        W = w;
    }

    public void setK(String k) {
        K = k;

    }

    private String T, N, K, W, A;
    private ArrayList<Long> X;
    private String contents;
    private long id, start, end;
}

