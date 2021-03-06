package indexation;
import java.io.*;
public abstract class Parser {
    //public Parser(){};

    public Parser(RandomAccessFile input, long start){
        this.input = input;
        this.start = start;
    }

    protected RandomAccessFile input;
    protected long start;

    public Document nextDocument(){
        return nextDocument(input, start);
    }

    public Document nextDocument(RandomAccessFile input, long start){
        try {
            input.seek(start);
        } catch (IOException e) {
            e.printStackTrace();
            return getDocument("");
        }

        StringBuilder sb = new StringBuilder();
        boolean dStart = false;
        String line;
        long prevOffset;
        while(true){
            try {
                prevOffset = input.getFilePointer();
                line = input.readLine();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
//            if (line == null || line.isEmpty())
//                break;
            if (line == null)
                break;
            if (line.startsWith(startString)){
                if (dStart){
                    try {
                        input.seek(prevOffset);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                dStart = true;
            }

            if (dStart){
                sb.append(line);
                sb.append("\n");
            }
        }
        try {
            this.start = input.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getDocument(sb.toString());
    }

    protected final String startString = ".I";
    public abstract Document getDocument(String s);
}
