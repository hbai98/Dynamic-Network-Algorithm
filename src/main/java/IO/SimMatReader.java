package IO;

import DS.Matrix.SimMat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.Vector;

import static Tools.Functions.isDouble;

/**
 * Reader class for matrix reading
 * Mainly for test.
 */
@SuppressWarnings("unchecked")
public class SimMatReader<V> extends AbstractFileReader {
    public final Class<V> typeParameterClass;
    private final SimMat<V> simMat;

    public SimMatReader(Set<V> g1, Set<V> g2, Class<V> typeParameterClass) {
        this.simMat =new SimMat<>(g1, g2, typeParameterClass);
        this.typeParameterClass = typeParameterClass;
    }
    public SimMat<V> readToSimMatExcel(String sourcePath) throws IOException {
        setInputFilePath(sourcePath);
        String extString = inputFilePath.substring(inputFilePath.lastIndexOf("."));
        FileInputStream is = new FileInputStream(inputFilePath);
        Workbook wb;
        if (".xls".equals(extString)) {
            wb = new HSSFWorkbook(is);
        } else if (".xlsx".equals(extString)) {
            wb = new XSSFWorkbook(is);
        } else {
            throw new IOException("Your excel file should be in format of .xlsx or .xls");
        }
        Sheet sheet = wb.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        for (Row row : sheet) {   //iteration over row using for each loop
            if (row.getPhysicalNumberOfCells() != 3) {
                throw new IOException("Your excel file is incorrect.");
            }
            double val = row.getCell(2).getNumericCellValue();
            double eValue = 1 / (1 - 1 / Math.log(val));
            if(typeParameterClass.equals(String.class)){
                simMat.put((V)formatter.formatCellValue(row.getCell(0)),
                        (V)formatter.formatCellValue(row.getCell(1)),
                        eValue);
            }
            // add more ... if in need

        }
        return simMat;
    }

    /**
     * include checking whether simMat contains all nodes
     */
    public SimMat<V> readToSimMat(String sourcePath,boolean closeWhenFinished) throws IOException {
        setInputFilePath(sourcePath);
        // matches sequence of one or more whitespace characters.
        setSplitter("\\s+");
        Vector<String> sifLine = new Vector<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = splitter.split(line);
            if (tokens.length == 0) continue;
            //  it will be handled in pareLine()
            // which will throw an IOException if not the right case.
            for (String token : tokens) {
                if (token.length() != 0) {
                    sifLine.add(token);
                }
            }
            parseForSimMat((Vector<V>) sifLine);
        }
        if (closeWhenFinished) {
            reader.close();
        }
        return simMat;
    }

    private void parseForSimMat(Vector<V> sifLine) throws IOException {
        int sifSize = sifLine.size();
        if(sifSize!=3){
            throw new IOException("The simMat format is not right. Please follow source target value.");
        }
        simMat.put(sifLine.get(0),sifLine.get(1),Double.parseDouble((String) sifLine.get(2)));
        sifLine.clear();
    }


}
