package Algorithms.Graph;

import Algorithms.Graph.IO.GraphFileReader;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.AdjList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("THe algorithm's ")
class NBMSpec {
    AdjList list;
    HashSet<String> graph_1;
    HashSet<String>  graph_2;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        list = reader.readToAdjL("src/test/java/resources/IOTest/HomoGeneMap_1");
        graph_1 = reader.getGraph_1();
        graph_2 = reader.getGraph_2();
    }
    @DisplayName("findBestPair finish.")
    @Test
    void findBestPairTest(){
        NBM nbm = new NBM(list,graph_1,graph_2);
        nbm.findBestPairs();
        HashMap<String,Node> hashMap = nbm.getMostSimPairMap();
        HashMap<String,Node> result = new HashMap<>();
        result.put("A",new Node("D",0.4));
        result.put("C",new Node("A",0.7));
        assertEquals(result,hashMap);
    }
}