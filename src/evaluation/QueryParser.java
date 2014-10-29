package evaluation;

import dataClass.Query;

import java.io.RandomAccessFile;

/**
 * Created by foolchi on 22/10/14.
 */
public abstract class QueryParser {
    public QueryParser(){
        query = null;
        result = null;
    }

    protected RandomAccessFile query, result;
    public abstract Query nextQuery();
}
