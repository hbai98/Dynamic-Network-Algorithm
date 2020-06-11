package Algorithms.Graph;

import Algorithms.Graph.Network.EdgeHashSet;
import IO.GraphFileReader;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.AdjList;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("THe algorithm's ")
class NBMSpec {
    AdjList list;
    AdjList graph1;
    AdjList graph2;
    AdjList simList;
    @BeforeEach
    void init() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        list = reader.readToAdjL("src/test/java/resources/IOTest/HomoGeneMap_1");
        graph1 = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/graph1.txt");
        graph2 = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/graph2.txt");
        simList = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/simMat.txt",false);

    }
    @DisplayName("findBestPair finish.")
    @Test
    void findBestPairTest() throws IOException {
        EdgeHashSet pairInit = new EdgeHashSet();
        Hungarian alg = new Hungarian(list, Hungarian.ProblemType.maxLoc);
        int[] res = alg.getResult();
        EdgeHashSet initMap = new EdgeHashSet();
        for (int i = 0; i < res.length; i++) {
            int j = res[i];
            Pair<Node,Node> tp = list.getNodeNameByMatrixIndex(i,j);
            initMap.add(new Edge(tp.getFirst(),tp.getSecond(),tp.getSecond().getValue()));
        }
        NBM nbm = new NBM(graph1,graph2,list,initMap,0);
        nbm.findBestPairs();
        HashMap<String,Node> hashMap = nbm.getMostSimPairMap();
        HashMap<String,Node> result = new HashMap<>();
        result.put("A",new Node("D",0.4));
        result.put("C",new Node("A",0.7));
        assertEquals(result,hashMap);
    }
    @DisplayName("findBestPair finish.")
    @Test
    void NeighborAdjustTest() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        AdjList graph1 = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/small/sGraph1.txt");
        AdjList rev1 = reader.getRevAdjList();
        AdjList graph2 = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/small/sGraph2.txt");
        AdjList simList = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/small/simMat2.txt");
        EdgeHashSet set = new EdgeHashSet();
        set.add("A","A",10);
        NBM.neighborSimAdjust(graph1,rev1,graph2,simList,set);

    }



}