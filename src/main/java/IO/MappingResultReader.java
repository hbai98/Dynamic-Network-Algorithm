/*
 * @Author: Haotian Bai
 * @Github: https://github.com/164140757
 * @Date: 2020-08-03 10:20:08
 * @LastEditors: Haotian Bai
 * @LastEditTime: 2020-08-12 13:40:04
 * @FilePath: \MyAlgorithms-master_1.0\src\main\java\Internal\Algorithms\IO\MappingResultReader.java
 * @Description: 
 */
package IO;
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
