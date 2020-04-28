package IO;

import Algorithms.Graph.Network.AdjList;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.IOException;
import java.util.*;

public class CsvToAdjList {
    Table tsv;
    AdjList target;

    /**
     * @param filePath path to import .tsv or .csv
     * @param columns  columns to select including 2 nodes or edge value
     * @throws IOException
     */
    public CsvToAdjList(String filePath, String... columns) throws IOException {
        tsv = Table.read().csv(filePath);
        target = new AdjList();
        int size = columns.length;
        Column<?> node1s = tsv.column(columns[0]);
        Column<?> node2s = tsv.column(columns[1]);
        if (node1s.size() != node2s.size()) {
            throw new IOException("Your input format is not right.");
        }
        switch (size) {
            case 2 -> {
                for (int i = 0; i < node1s.size(); i++) {
                    target.sortAddOneNode(node1s.get(i).toString(), node2s.get(i).toString());
                }
            }
            case 3 -> {
                for (int i = 0; i < node1s.size(); i++) {
                    target.sortAddOneNode(node1s.get(i).toString(), node2s.get(i).toString(), Double.parseDouble(columns[2]));
                }
            }
            default -> {
                throw new IOException("Your input format is not right.");
            }
        }

    }

    public Table getTsv() {
        return tsv;
    }

    public AdjList getTarget() {
        return target;
    }
}
