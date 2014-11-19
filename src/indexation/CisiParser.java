package indexation;

import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by foolchi on 18/11/14.
 * Cisi Parser
 */
public class CisiParser extends Parser{


    public CisiParser(RandomAccessFile input, long start){
        super(input, start);
    }

    @Override
    public Document getDocument(String s) {
        if (s.isEmpty()){
            return null;
        }
        long id;
        boolean validString = false;

        String[] contents = s.split("\n");
        Document currentDocument = new Document();
        String currentString = null;
        // ArrayList<String> currentA = new ArrayList<String>();
        ArrayList<Long> currentX = new ArrayList<Long>();

        for(String line : contents){
            //System.out.println(line);
            if (line.startsWith(".I")){
                try{
                    id = Long.valueOf(line.substring(3));
                }
                catch (Exception e){
                    e.printStackTrace();
                    id = -1;
                }
                currentDocument.setId(id);
                continue;
            }
            if (line.startsWith(".K") || line.startsWith(".T") || line.startsWith(".A") || line.startsWith(".W") || line.startsWith(".X")){
                validString = true;
                currentString = line.substring(0,2);
                continue;
            }
            else if (line.startsWith(".")){
                validString = false;
                continue;
            }
            if (validString && currentString != null){
                if (currentString.equals(".K")) {
                    currentDocument.setK(currentDocument.getK() + " " + line);
                }
                else if (currentString.equals(".T")) {
                    currentDocument.setT(currentDocument.getT() + " " + line);
                }
                else if (currentString.equals(".A")) {
                    currentDocument.setA(currentDocument.getA() + " " + line);
                }
                else if (currentString.equals(".W")) {
                    currentDocument.setW(currentDocument.getW() + " " + line);
                }
                else if (currentString.equals(".X")) {
                    String[] splitLine = line.split("\t");
                    currentX.add(Long.valueOf(splitLine[0]));
                }
            }

        }
        currentDocument.setX(currentX);

        long end = start;
        try{
            end = input.getFilePointer();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        currentDocument.setStart(start);
        currentDocument.setEnd(end);
        return currentDocument;
    }
}
