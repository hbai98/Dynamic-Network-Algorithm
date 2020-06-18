package Algorithms.Graph.HGA;

import Algorithms.Graph.Utils.AdjList.Graph;
import Algorithms.Graph.Utils.SimMat;
import IO.GraphFileReader;
import IO.GraphFileReaderSpec;
import Tools.Stopwatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("The HGA is able to ")
class HGASpc extends GraphFileReaderSpec {
    HGA hga;
    private Graph graph1;
    private Graph graph2;
    private SimMat simMat;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader reader = new GraphFileReader(true, true, false);
        graph1 = reader.readToGraph("src/test/java/resources/AlgTest/HGA/graph1.txt", false);
        graph2 = reader.readToGraph("src/test/java/resources/AlgTest/HGA/graph2.txt", false);
        reader.setRecordNonZeros(true);
        reader.setRecordNeighbors(false);
        simMat = reader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt", graph1.getAllNodes(), graph2.getAllNodes(), true);
        hga = new HGA(simMat, graph1, graph2, 0.5);
    }

    @DisplayName("Greedily map")
    @Test
    void greedMap() {
        HashMap<String, String> preMap = new HashMap<>();
        preMap.put("D", "F");
        preMap.put("F", "I");
        HGA.greedyMap(hga.simMat, preMap);
    }

    @DisplayName("Edge correctness")
    @Test
    void getEC() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("W", "A");
        mapping.put("Q", "W");
        mapping.put("I", "H");
        mapping.put("G", "M");
        hga.setEC(mapping);
        assertEquals((double) 2 / 15, hga.getEC());
    }

    @DisplayName("topo")
    @Test
    void topo() {
        hga.addTopology("A", "I");
        assertEquals((5.7 / 28 + 1.3 / 3) / 2 / 2, hga.simMat.getVal("A", "I"));
    }

    @DisplayName("score mapping")
    @Test
    void score() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("W", "A");
        mapping.put("Q", "W");
        mapping.put("I", "H");
        mapping.put("G", "M");
        hga.scoreMapping(mapping);
        assertEquals(hga.getScore(), (double) 40 / 3 + 1.1);
        assertEquals(hga.getES(), 1.);
        assertEquals(hga.getPS(), 0.6);
        assertEquals(hga.getPE(), 1.1);
    }

    @DisplayName("update neighbors by the mapping result")
    @Test
    void updateNeighbors() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("W", "A");
        mapping.put("Q", "W");
        mapping.put("I", "H");
        mapping.put("G", "M");
        hga.updatePairNeighbors(mapping);
    }
    @DisplayName("compare parallel speed")
    @Test
    void parallelTest() {
        Stopwatch stopwatch = new Stopwatch();
        parallel();
        stopwatch.outElapsedByMiniSecond();
        // without parallel 5 ms
        // parallel -> head 8 ms
        // parallel -> head + 2for 10 ms

    }
    void parallel() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("W", "A");
        mapping.put("Q", "W");
        mapping.put("I", "H");
        mapping.put("G", "M");
        // distinguish the result simMat from the indexing one
        HashMap<String, HashSet<String>> neb1Map = graph1.getNeighborsMap();
        HashMap<String, HashSet<String>> neb2Map = graph2.getNeighborsMap();
        mapping.entrySet().parallelStream().forEach(entry -> {
            String node1 = entry.getKey();
            String node2 = entry.getValue();
            double simUV = simMat.getVal(node1, node2);
            // direct neighbors of the head node
            HashSet<String> neb1 = neb1Map.get(node1);
            HashSet<String> neb2 = neb2Map.get(node2);
            neb1.parallelStream().forEach(s1 -> {
                int nebNumbNode1 = neb1Map.get(s1).size();
                double reward = simUV / nebNumbNode1;
                neb2.parallelStream().forEach(s2 -> {
                    double newWeight = simMat.getVal(s1, s2) + reward;
                    simMat.put(s1, s2, newWeight);
                });
            });
        });
    }

//    @BeforeEach
//    void init() throws IOException {
//        GraphFileReader reader = new GraphFileReader();
////        simList = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/simMat.txt",false);
////        graph1 = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/graph1.txt",false);
////        rev1 = reader.getRevAdjList();
////        graph2 = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/graph2.txt",true);
////        hga = new HGA(simList,graph1,graph2);
//    }
//    @DisplayName("init mapping.")
//    @Test
//    void initMap() throws IOException {
//        EdgeHashSet initMap = hga.getEdgeMapFromHA(simList);
//        EdgeHashSet res = new EdgeHashSet();
//        res.add(new Edge("A","C",0.8));
//        res.add(new Edge("B","B",0.8));
//        res.add(new Edge("C","I",0.7));
//        res.add(new Edge("D","A",0.7));
//        res.add(new Edge("F","D",0.9));
//        res.add(new Edge("G","Q",0.2));
//        res.add(new Edge("I","M",0.6));
//        res.add(new Edge("M","W",0.9));
//        res.add(new Edge("Q","F",0.9));
//        assertEquals(res,initMap);
//    }
//    @DisplayName("update matrix.")
//    @Test
//    void updateMap() throws IOException {
//        NBM.neighborSimAdjust(graph1,rev1,graph2,simList,hga.getEdgeMapFromHA(simList));
//        assertEquals(1.4,simList.getValByName("W","A"));
//        assertEquals(1.4,simList.getValByMatName("W","A"));
//    }
//    @DisplayName("add topological effect.")
//    @Test
//    void addTopologicalEffect() throws IOException {
//        String humanPath = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt";
//        String yeastPath = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt";
//        String simPath = "src\\test\\java\\resources\\TestModule\\HGATestData\\Human-Yeast\\fasta\\yeastHumanSimList_EvalueLessThan1e-10.txt";
//        SimList yeast = readAdjList(yeastPath);// 38
//        SimList human = readAdjList(humanPath);// 9141
//        simList = readAdjList(simPath);// 38*9141 1e-10
//        hga = new HGA(simList,yeast,human);
//        hga.addAllTopology(0.4);
//    }
//    @DisplayName("get EC.")
//    @Test
//    void getEdgeCorrectness() throws IOException {
//        assertEquals((float)4/15,hga.getEC(hga.getEdgeMapFromHA(simList)));
//    }
//    @DisplayName("compute topo info.")
//    @Test
//    void topoTest() throws IOException {
//        GraphFileReader reader = new GraphFileReader();
//        SimList graph1 = reader.readToSimList("src/test/java/resources/AlgTest/HGA/small/sGraph1.txt");
//        SimList rev1 = reader.getRevAdjList();
//        SimList graph2 = reader.readToSimList("src/test/java/resources/AlgTest/HGA/small/sGraph2.txt");
//        SimList simList = reader.readToSimList("src/test/java/resources/AlgTest/HGA/small/simMat2.txt");
//        hga = new HGA(simList,graph1,graph2);
//        hga.addTopology("A","A",0.5);
//
//    }
//    @DisplayName("remap.")
//    @Test
//    void reMappingTest() throws IOException {
//        hga.remapping(simList,6);
//    }
//    @DisplayName("pass final Test.")
//    @Test
//    void finalTest() throws IOException {
//        long startTime =  System.currentTimeMillis();
//        hga.run(0.5,0.01,3,true);
//        long endTime =  System.currentTimeMillis();
//        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
//    }
//
//    @DisplayName("pass final Test.")
//    @Test
//    void finalTest_2() throws IOException {
//
//    }





}

