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
    AdjList revList;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        list = reader.readToAdjL("src/test/java/resources/IOTest/HomoGeneMap_1");
        revList = reader.getRevAdjList();

    }
    @DisplayName("findBestPair finish.")
    @Test
    void findBestPairTest(){
        NBM nbm = new NBM(list,revList,);
        nbm.findBestPairs();
        HashMap<String,Node> hashMap = nbm.getMostSimPairMap();
        HashMap<String,Node> result = new HashMap<>();
        result.put("A",new Node("D",0.4));
        result.put("C",new Node("A",0.7));
        assertEquals(result,hashMap);
    }
}