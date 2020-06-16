package Algorithms.Graph.Utils;

import Algorithms.Graph.Utils.AdjList.Graph;
import IO.GraphFileReader;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SimMatTest {

    @Test
    void getSplit() throws IOException {
        int h = 2;
        GraphFileReader reader = new GraphFileReader(true,false,true);
        Graph graph1 = reader.readToGraph("src/test/java/resources/AlgTest/HGA/graph1.txt",false);
        Graph graph2 = reader.readToGraph("src/test/java/resources/AlgTest/HGA/graph2.txt",false);
        reader.setRecordNeighbors(true);
        SimMat simMat = reader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt",graph1.getAllNodes(),graph2.getAllNodes(),true);
        Pair<SimMat,SimMat> res = simMat.getSplit(5);
    }
}