package Algorithms.Graph;

import Algorithms.Graph.IO.GraphFileReader;
import Algorithms.Graph.Utils.AdjList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;

@DisplayName("THe algorithm's ")
class NBMSpec {
    AdjList list;
    HashSet<String> graph_1;
    HashSet<String>  graph_2;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        list = reader.readToAdjL("src/main/java/resources/smith_watermanRes.txt");
        graph_1 = reader.getGraph_1();
        graph_2 = reader.getGraph_2();
    }
    @DisplayName("findBestPair finish.")
    @Test
    void findBestPairTest(){

    }
}