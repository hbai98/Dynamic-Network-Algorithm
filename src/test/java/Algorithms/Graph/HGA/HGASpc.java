package Algorithms.Graph.HGA;


import DS.Matrix.SimMat;
import DS.Network.UndirectedGraph;
import IO.GraphFileReader;
import IO.SimMatReader;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("The HGA is able to ")
class HGASpc{
    HGA hga;

    private UndirectedGraph<String, DefaultEdge> udG1;
    private UndirectedGraph<String, DefaultEdge> udG2;
    private SimMat<String> simMat;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader<String, DefaultEdge> reader = new GraphFileReader<>(String.class,DefaultEdge.class);
        udG1 = reader.readToUndirectedGraph( "src/test/java/resources/AlgTest/HGA/graph1.txt",false);
        udG2 = reader.readToUndirectedGraph( "src/test/java/resources/AlgTest/HGA/graph2.txt",false);
        SimMatReader<String> simMatReader = new SimMatReader<>(udG1.vertexSet(),udG2.vertexSet(),String.class);
        simMat = simMatReader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt",true);
        hga = new HGA<>(simMat, udG1, udG2, (float) 0.5, true,  0.5,0.01);
    }

    @DisplayName("Greedily map")
    @Test
    void greedMap() {
        HashMap<String, String> preMap = new HashMap<>();
        preMap.put("D", "F");
        preMap.put("F", "I");
        hga.greedyMap(hga.simMat, preMap);
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

