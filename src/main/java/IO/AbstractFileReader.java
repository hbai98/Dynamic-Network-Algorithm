package IO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

public abstract class AbstractFileReader {
    BufferedReader reader;
    Vector<String> context;
    String inputFilePath;
    public AbstractFileReader(String inputFilePath) throws FileNotFoundException {
        this.inputFilePath = inputFilePath;
        reader = new BufferedReader(new FileReader(inputFilePath));
    }

    public void readToVector() throws IOException {
        context = new Vector<>();
        String line;
        Pattern splitter = Pattern.compile("\\s+");
        while ((line = reader.readLine()) != null) {
            String[] tokens = splitter.split(line);
            if (tokens.length == 0) {
                continue;
            }
            for (String token : tokens) {
                if (token.length() != 0) {
                    context.add(token);
                }
            }

        }
    }

    public BufferedReader getReader() {
        return reader;
    }

    public Vector<String> getContext() {
        return context;
    }
}
