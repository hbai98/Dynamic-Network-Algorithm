package IO;

import Algorithms.Graph.Utils.AdjList.SimList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;

import java.io.IOException;

class CsvToSimListTest {

    @DisplayName("read tsv file")
    @Test
    void read() throws IOException {
        Table tsv = Table.read().csv("C:\\Users\\Haotian Bai\\Desktop\\cov19\\A1.csv");
        System.out.print(tsv);
    }
    @DisplayName("Parse tsv file")
    @Test
    void parse() throws IOException {
        CsvToAdjList tsvToAdjList = new CsvToAdjList("C:\\Users\\Haotian Bai\\Desktop\\cov19\\A1.csv",
                "node1","node2");
        SimList list = tsvToAdjList.getTarget();
    }

}