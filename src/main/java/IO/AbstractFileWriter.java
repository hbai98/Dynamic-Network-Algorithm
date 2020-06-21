package IO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public abstract class AbstractFileWriter {
    BufferedWriter bufWriter;
    String path;

    public AbstractFileWriter() {

    }


    public void write(Vector<String> context,boolean close) {
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
                }
                if(bufWriter != null && close){
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

    public void setPath(String path) throws FileNotFoundException {
        this.path = path;
        OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(path), StandardCharsets.UTF_8);
        bufWriter = new BufferedWriter(writer);
    }

    public String getPath() {
        return path;
    }

    public void setClose(){
        try {
            bufWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
