package evaluation;

import dataClass.Query;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by foolchi on 22/10/14.
 */
public class CacmQueryParser extends QueryParser {
    public CacmQueryParser(RandomAccessFile query, RandomAccessFile result) {
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
        String currentLine;
        try {
            while (true) {
                if (query.getFilePointer() >= query.length())
                    return queryClass;
                currentLine = query.readLine();
                if (currentLine == null || currentLine.isEmpty()) {
                    if (isStart)
                        break;
                    continue;
                }
                if (currentLine.startsWith(".I")) {
                    queryClass = new Query();
                    queryClass.id = Integer.valueOf(currentLine.substring(3));
                    isStart = true;
                    continue;
                }
                else if (currentLine.startsWith(".W")) {
                    wStart = true;
                    continue;
                }
                else if (currentLine.startsWith(".N")) {
                    if (isStart)
                        break;
                    continue;
                }
                if (wStart) {
                    queryClass.text = queryClass.text + currentLine;
                }
            }

            long currentPosition;
            while (true) {
                currentPosition = result.getFilePointer();
                currentLine = result.readLine();
                if (currentLine == null || currentLine.isEmpty())
                    return queryClass;
                String[] lineStrings = currentLine.split(" ");
                int currentId = Integer.valueOf(lineStrings[0]);
                if (currentId != queryClass.id) {
                    result.seek(currentPosition);
                    return queryClass;
                }
                queryClass.relevants.add(Integer.valueOf(lineStrings[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
