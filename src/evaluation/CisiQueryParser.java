package evaluation;

import dataClass.Query;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by foolchi on 18/11/14.
 * Cisi query parser
 */
public class CisiQueryParser extends QueryParser {
    public CisiQueryParser(RandomAccessFile query, RandomAccessFile result) {
        this.query = query;
        this.result = result;
    }

    public boolean setQueryFile(RandomAccessFile query){
        this.query = query;
        return true;
    }

    public boolean setResultFile(RandomAccessFile result){
        this.result = result;
        return true;
    }


    @Override
    public Query nextQuery() {
        boolean isStart = false, wStart = false;
        Query queryClass = null;
        long prevOffset;
        String currentLine;
        try {
            // Get query
            while (true) {
                if (query.getFilePointer() >= query.length())
                    break;
                prevOffset = query.getFilePointer();
                currentLine = query.readLine();
                if (currentLine.startsWith(".I")) {
                    if (isStart){
                        query.seek(prevOffset);
                        break;
                    }
                    queryClass = new Query();
                    queryClass.id = Integer.valueOf(currentLine.substring(3));
                    isStart = true;
                    continue;
                }
                else if (currentLine.startsWith(".W")) {
                    wStart = true;
                    continue;
                }
                else if (currentLine.startsWith(".")) {
                    wStart = false;
                    continue;
                }
                if (wStart && queryClass != null) {
                    queryClass.text = queryClass.text + currentLine.toLowerCase();
                }
            }

            // Get result
            long prevPosition;
            long queryId = -1;
            if (queryClass != null)
                queryId = queryClass.id;

            while (true) {
                prevPosition = result.getFilePointer();
                if (prevPosition >= result.length())
                    return queryClass;
                currentLine = result.readLine().trim();
                //System.out.println(currentLine);
                if (currentLine.isEmpty())
                    return queryClass;
                String[] lineStrings = currentLine.split(" ");
//                for (String s : lineStrings)
//                    System.out.println("hello" + s);
                int currentId = Integer.valueOf(lineStrings[0]);
//                System.out.println("ID: " + currentId);
                String relevantString = null;
                for (int i = 1; i < lineStrings.length; i++){
                    if (!(lineStrings[i].isEmpty())){
                        relevantString = lineStrings[i];
//                        System.out.println("RelevantString:" + relevantString);
                        break;
                    }
                }
                if (relevantString == null){
                    return queryClass;
                }
                String[] relevantStrings = relevantString.split("\t");
                int relevant = Integer.valueOf(relevantStrings[0]);
//                System.out.println("Relevant: " + relevant);
                if (currentId != queryId) {
                    result.seek(prevPosition);
                    //System.out.println(queryClass);
                    return queryClass;
                }
                if (queryClass != null)
                    queryClass.relevants.add(relevant);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
