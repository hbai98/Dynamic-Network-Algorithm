package IO;

import Algorithms.Graph.Network.AdjList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.ReadOptions;

import java.io.IOException;

class CsvToAdjListTest {

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
        AdjList list = tsvToAdjList.getTarget();
    }

}