package Internal.Algorithms.IO;

import Internal.Algorithms.Graph.Utils.AdjList.SimList;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.IOException;

public class CsvToAdjList {
    Table tsv;
    SimList target;

    /**
     * @param filePath path to import .tsv or .csv
     * @param columns  columns to select including 2 nodes or edge value
     * @throws IOException format incorrect
     */
    public CsvToAdjList(String filePath, String... columns) throws IOException {
        tsv = Table.read().csv(filePath);
        target = new SimList();
        int size = columns.length;
        Column<?> node1s = tsv.column(columns[0]);
        Column<?> node2s = tsv.column(columns[1]);
        if (node1s.size() != node2s.size()) {
            throw new IOException("Your input format is not right.");
        }
        switch (size) {
            case 2 :{
                for (int i = 0; i < node1s.size(); i++) {
                    target.sortAddOneNode(node1s.get(i).toString(), node2s.get(i).toString());
                }
                break;
            }
            case 3 : {
                for (int i = 0; i < node1s.size(); i++) {
                    target.sortAddOneNode(node1s.get(i).toString(), node2s.get(i).toString(), Double.parseDouble(columns[2]));
                }
                break;
            }
            default :{
                throw new IOException("Your input format is not right.");
            }
        }

    }

    public Table getTsv() {
        return tsv;
    }

    public SimList getTarget() {
        return target;
    }
}
