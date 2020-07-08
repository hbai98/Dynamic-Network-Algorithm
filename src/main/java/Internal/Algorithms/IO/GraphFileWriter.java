package Internal.Algorithms.IO;

import Internal.Algorithms.Graph.Utils.SimMat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Pattern;

public class GraphFileWriter {
    BufferedWriter bufWriter;

    public void writeToTxt(SimMat simMat, String toWhere) throws IOException {
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(toWhere), StandardCharsets.UTF_8);
            bufWriter = new BufferedWriter(writer);
            HashMap<String, Integer> rowMap = simMat.getRowMap();
            HashMap<String, Integer> colMap = simMat.getColMap();
            for (String row : rowMap.keySet()) {
                    bufWriter.write(row + " ");
                for (String col : colMap.keySet()) {
                    double val = simMat.getVal(row, col);
                    if (val != 0) {
                        bufWriter.write( col+ " "+val+" ");
                    }
                }
                bufWriter.write("\n");
            }
            if (bufWriter != null) {
                bufWriter.flush();
                bufWriter.close();
            }
    }


    public String replaceSpace_(String headName) {
        StringBuilder result = new StringBuilder();
        Pattern splitter = Pattern.compile("\\s+");
        String[] tokens = splitter.split(headName);
        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].length() != 0) {
                result.append(tokens[i]).append("_");
            }
        }
        result.append(tokens[tokens.length - 1]);
        return result.toString();
    }
}
