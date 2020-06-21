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
        hga = new HGA(simMat, graph1, graph2, 0.5,true,0.5,0.01);
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
        hga.addTopology("A", "I",simMat);
        assertEquals((5.7 / 28 + 1.3 / 3) / 2 / 2, hga.simMat.getVal("A", "I"));
    }

    @Test
    void addAllTopo(){
        hga.addAllTopology();
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

    @DisplayName("get h by account")
    @Test
    void getHByAccount(){
        assertEquals(8,hga.getHByAccount());
    }


    @Test
    void clean(){
        hga.cleanDebugResult();
    }
    @Test
    void run(){
        hga.run();
    }

}

