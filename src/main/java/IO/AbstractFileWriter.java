package IO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public abstract class AbstractFileWriter {
    BufferedWriter bufWriter;

    public AbstractFileWriter(String filePath) throws FileNotFoundException {
        OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8);
        bufWriter = new BufferedWriter(writer);
    }


    public void write(Vector<String> context) {
        assert context != null;
        try {
            for (String s : context) {
                bufWriter.write(s);
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

    public BufferedWriter getBufWriter() {
        return bufWriter;
    }
}
