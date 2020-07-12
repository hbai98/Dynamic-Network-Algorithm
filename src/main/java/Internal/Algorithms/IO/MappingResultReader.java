package Internal.Algorithms.IO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class MappingResultReader extends AbstractFileReader{
    HashMap<String,String> mapping;
    public MappingResultReader(String inputPath) throws IOException {
        setInputFilePath(inputPath);
        mapping = new HashMap<>();
        String line = null;
        setSplitter("\\s+");
        while((line = reader.readLine())!=null){
            String[] tokens = splitter.split(line);
            if(tokens.length != 2){
                throw new IOException("Your mapping result format is not correct.");
            }
            mapping.put(tokens[0],tokens[1]);
        }
    }

    public HashMap<String, String> getMapping() {
        return mapping;
    }
}
