package IO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

public abstract class AbstractFileReader {
    protected BufferedReader reader;
    String inputFilePath;
    protected Pattern splitter;

    public AbstractFileReader() {

    }

    protected void setInputFilePath(String filePath) throws FileNotFoundException {
        this.inputFilePath = filePath;
        reader = new BufferedReader(new FileReader(filePath));
    }

    public void setSplitter(String regex) {
        splitter = Pattern.compile(regex);
    }

    public BufferedReader getReader() {
        return reader;
    }

}
