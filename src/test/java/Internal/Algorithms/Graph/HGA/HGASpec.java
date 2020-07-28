package Internal.Algorithms.Graph.HGA;

import Internal.Algorithms.Graph.Utils.AdjList.UndirectedGraph;
import Internal.Algorithms.Graph.Utils.SimMat;
import Internal.Algorithms.IO.GraphFileReaderSpec;
import Internal.Algorithms.IO.GraphFileReader;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static Internal.Algorithms.Graph.HGA.HGA.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("The HGA is able to ")
class HGASpec extends GraphFileReaderSpec {
    HGA hga;
    private UndirectedGraph udG1;
    private UndirectedGraph udG2;
    private SimMat simMat;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader reader = new GraphFileReader(true, false, false);
        udG1 = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph1.txt", false);
        udG2 = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph2.txt", false);
        reader.setRecordNeighbors(false);
        simMat = reader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt", udG1.getAllNodes(), udG2.getAllNodes(), true);
        hga = new HGA(simMat, udG1, udG2, 0.5,true,0.5,0.01);
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
        Pair<HashMap<String, String>, SimMat> init = getRemapForForced();
        HGA.mapping = remap(init.getSecond(), init.getFirst());
        updatePairNeighbors(HGA.mapping);
        hga.addTopology("A", "Q",HGA.simMat);
//        assertEquals((5.2 / 12 + 5.7 / 18) / 2 / 2, hga.simMat.getVal("A", "I"));
    }

    @Test
    void addAllTopo(){
//        hga.addAllTopology();
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
        updatePairNeighbors(mapping);
    }

//    @DisplayName("get h by account")
//    @Test
//    void getHByAccount(){
//        assertEquals(8,hga.getHByAccount());
//    }


    @Test
    void clean(){
        hga.cleanDebugResult();
    }
    @Test
    void run(){
        hga.run();

    }
    @Test
    void test(){
        double a = 1;
        double b = a;
        a  = 6;
        System.out.println(a+" "+b);
    }

}

