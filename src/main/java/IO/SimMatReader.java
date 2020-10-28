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
            // clean for each line
            sifLine.clear();
        }
        if (closeWhenFinished) {
            reader.close();
        }
        return simMat;
    }

    /**
     * <ol>
     *     <li>node1 node2 value12</li>
     *     <li>node2 node3 value23 node4 value24 node5 value25</>
     *     <li>node0 value00</li>
     *  </ol>
     * <p>
     *     The first line identifies two nodes, called node1 and node2, and the weight of the edge between node1 node2. The second line specifies three new nodes, node3, node4, and node5; here “node2” refers to the same node as in the first line.
     *     The second line also specifies three relationships, all of the individual weight and with node2 as the source, with node3, node4, and node5 as the targets.
     *     This second form is simply shorthand for specifying multiple relationships of the same type with the same source node.
     *     The third line indicates how to specify a node that has no relationships with other nodes.
     * </p>
     *
     * <p>NOTICE:add() -> use sortAdd()</p>
     *
     * @param sifLine result very line
     */
    private void parseForSimMat(Vector<V> sifLine) throws IOException {
        int sifSize = sifLine.size();
        if (sifSize == 0) {
            throw new IOException("Nothing has been reader!.");
        }
        if (sifSize == 2) {
            V src = sifLine.get(0);
            V tgt = sifLine.get(1);
            simMat.put(src, tgt,0.);
        } else if ((sifSize - 1) % 2 != 0) {
            throw new IOException("The file reader format is not correct.");
        } else {
            V src = sifLine.get(0);
            // name value ... and it has already checked (sifSize -1) % 2 == 0
            for (int index = 1; index < sifSize; index += 2) {
                V tgt = sifLine.get(index);
                V val = sifLine.get(index + 1);
                if (!isDouble((String)val)) {
                    throw new IOException("The file reader format is not correct. Plus: some name-value pairs are incorrect!");
                }
                double weight = Double.parseDouble((String)val);
                // make sure only nodes in selection will be put into the simMat
                if (weight != 0 && simMat.getRowMap().containsKey(src) && simMat.getColMap().containsKey(tgt)) {
                    simMat.put(src, tgt, weight);
                }
            }
        }
        sifLine.clear();
    }


}
