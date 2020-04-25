package IO;

import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.AdjList;
import Algorithms.Graph.Utils.HNodeList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class GraphFileWriter {
    BufferedWriter bufWriter;

    public void writeToTxt(AdjList graph, String fileName, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(filePath + fileName + ".txt"), StandardCharsets.UTF_8);
            bufWriter = new BufferedWriter(writer);
            for (HNodeList list : graph) {
                String headName = list.getSignName();
                String result = replaceSpace_(headName);
                bufWriter.write(result + " ");
                for (Node node : list) {
                    String nodeName = replaceSpace_(node.getStrName());
                    bufWriter.write(nodeName + " " + node.getValue() + " ");
                }
                bufWriter.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufWriter != null) {
                    bufWriter.flush();
                    bufWriter.close();
                }
            } catch (Exception ex) {
                System.out.println("Error in closing the BufferedWriter" + ex);
            }
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
