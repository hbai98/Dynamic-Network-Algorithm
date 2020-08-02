//package Algorithms.Graph;
//
<<<<<<< Updated upstream:src/test/java/Algorithms/Graph/NBMSpec.java
//import Algorithms.Graph.Network.EdgeHashSet;
//import Algorithms.Graph.Utils.AdjList.SimList;
//import IO.GraphFileReader;
//import Algorithms.Graph.Network.Edge;
//import Algorithms.Graph.Network.Node;
=======
//import Internal.Algorithms.DS.Network.EdgeHashSet;
//import Internal.Algorithms.DS.Network.AdjList.SimList;
//import Internal.Algorithms.IO.GraphFileReader;
//import Internal.Algorithms.DS.Network.Edge;
//import Internal.Algorithms.DS.Network.Node;
>>>>>>> Stashed changes:src/test/java/Internal/Algorithms/Graph/NBMSpec.java
//import org.jgrapht.alg.util.Pair;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.HashMap;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@DisplayName("THe algorithm's ")
//class NBMSpec {
//    SimList list;
//    SimList graph1;
//    SimList graph2;
//    SimList simList;
//    @BeforeEach
//    void init() throws IOException {
//        GraphFileReader reader = new GraphFileReader();
//        list = reader.readToSimList("src/test/java/resources/IOTest/HomoGeneMap_1");
//        graph1 = reader.readToSimList("src/test/java/resources/AlgTest/HGA/graph1.txt");
//        graph2 = reader.readToSimList("src/test/java/resources/AlgTest/HGA/graph2.txt");
//        simList = reader.readToSimList("src/test/java/resources/AlgTest/HGA/simMat.txt",false);
//
//    }
//    @DisplayName("findBestPair finish.")
//    @Test
//    void findBestPairTest() throws IOException {
//        EdgeHashSet pairInit = new EdgeHashSet();
//        Hungarian alg = new Hungarian(list, Hungarian.ProblemType.maxLoc);
//        int[] res = alg.getResult();
//        EdgeHashSet initMap = new EdgeHashSet();
//        for (int i = 0; i < res.length; i++) {
//            int j = res[i];
//            Pair<Node,Node> tp = list.getNodeNameByMatrixIndex(i,j);
//            initMap.add(new Edge(tp.getFirst(),tp.getSecond(),tp.getSecond().getValue()));
//        }
//        NBM nbm = new NBM(graph1,graph2,list,initMap,0);
//        nbm.findBestPairs();
//        HashMap<String,Node> hashMap = nbm.getMostSimPairMap();
//        HashMap<String,Node> result = new HashMap<>();
//        result.put("A",new Node("D",0.4));
//        result.put("C",new Node("A",0.7));
//        assertEquals(result,hashMap);
//    }
//    @DisplayName("findBestPair finish.")
//    @Test
//    void NeighborAdjustTest() throws IOException {
//        GraphFileReader reader = new GraphFileReader();
//        SimList graph1 = reader.readToSimList("src/test/java/resources/AlgTest/HGA/small/sGraph1.txt");
//        SimList rev1 = reader.getRevAdjList();
//        SimList graph2 = reader.readToSimList("src/test/java/resources/AlgTest/HGA/small/sGraph2.txt");
//        SimList simList = reader.readToSimList("src/test/java/resources/AlgTest/HGA/small/simMat2.txt");
//        EdgeHashSet set = new EdgeHashSet();
//        set.add("A","A",10);
//        NBM.neighborSimAdjust(graph1,rev1,graph2,simList,set);
//
//    }
//
//
//
//}