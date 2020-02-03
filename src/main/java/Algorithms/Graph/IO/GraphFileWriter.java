package Algorithms.Graph.IO;

import Algorithms.Graph.Utils.AdjList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class GraphFileWriter {
    BufferedWriter bufWriter;

    public void writeToTxt(AdjList graph,String fileName,String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath+fileName+".txt"), StandardCharsets.UTF_8);
        BufferedWriter bufWriter = new BufferedWriter(writer);

        graph.forEach(list -> {
            String headName = list.getSignName();
            String result = replaceSpace_(headName);

            try {
                bufWriter.write(result+" ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            list.forEach(node -> {
                try {
                    bufWriter.write(node.getStrName()+" "+node.getValue()+" ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            try {
                bufWriter.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
