package Algorithms.Graph.Utils;

import Algorithms.Graph.Utils.AdjList.DirtedGraph;
import IO.GraphFileReader;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SimMatTest {
    private SimMat simMat;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader reader = new GraphFileReader(true,true,false);
        DirtedGraph dirtedGraph1 = reader.readToDirectedGraph("src/test/java/resources/AlgTest/HGA/graph1.txt",false);
        DirtedGraph dirtedGraph2 = reader.readToDirectedGraph("src/test/java/resources/AlgTest/HGA/graph2.txt",false);
        reader.setRecordNonZeros(true);
        reader.setRecordNeighbors(false);
        simMat = reader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt", dirtedGraph1.getAllNodes(), dirtedGraph2.getAllNodes(),true);
    }
    @DisplayName("split")
    @Test
    void getSplit() {

        Pair<SimMat,SimMat> res = simMat.getSplit(5);
    }
    @DisplayName("sum mat")
    @Test
    void sum(){
       //assertEquals(simMat.getMat().sum(),simMat.sum());
    }
    @DisplayName("getPart")
    @Test
    void getPart(){
        HashSet<String> cols = new HashSet<>(Arrays.asList("H", "F", "I", "Q"));
        HashSet<String> rows = new HashSet<>(Arrays.asList("A", "B"));
        SimMat res = simMat.getPart(rows,cols);
        assertEquals(rows,res.getRowSet());
        assertEquals(cols,res.getColSet());

    }
}