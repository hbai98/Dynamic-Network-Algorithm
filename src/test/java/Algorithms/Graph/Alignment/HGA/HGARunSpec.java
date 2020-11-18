
package Algorithms.Graph.Alignment.HGA;

import DS.Matrix.SimMat;
import DS.Network.UndirectedGraph;
import IO.Reader.GraphFileReader;
import IO.Reader.SimMatReader;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class HGARunSpec {
    UndirectedGraph<String, DefaultWeightedEdge> udG1;
    UndirectedGraph<String, DefaultWeightedEdge> udG2;
    SimMat<String> simMat;
    HGA<String,DefaultWeightedEdge> hga;

    @BeforeEach
    void init() {

    }

    @Test
    void run_test() throws IOException {
        GraphFileReader<String, DefaultWeightedEdge> reader = new GraphFileReader<>(String.class,DefaultWeightedEdge.class);
        udG1 = reader.readToUndirectedGraph( "src/test/java/resources/AlgTest/HGA/graph1.txt",false);
        udG2 = reader.readToUndirectedGraph( "src/test/java/resources/AlgTest/HGA/graph2.txt",false);
        SimMatReader<String> simMatReader = new SimMatReader<>(udG1.vertexSet(),udG2.vertexSet(),String.class);
        simMat = simMatReader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt",true);
        hga = new HGA<>(simMat, udG1, udG2, 0.5, true,  0.5,0.01);
        hga.run();
    }

    @Test
    void run_test_GPU() throws IOException {
        GraphFileReader<String,DefaultWeightedEdge> reader = new GraphFileReader<>(String.class,DefaultWeightedEdge.class);
        udG1 = reader.readToUndirectedGraph( "src/test/java/resources/AlgTest/HGA/graph1.txt",false);
        udG2 = reader.readToUndirectedGraph( "src/test/java/resources/AlgTest/HGA/graph2.txt",false);
        SimMatReader<String> simMatReader = new SimMatReader<>(udG1.vertexSet(),udG2.vertexSet(),String.class);
        simMat = simMatReader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt",true);
        hga = new HGA<>(simMat, udG1, udG2, 0.5, true,0.5,  0.01);
        HGA.GPU = true;
        hga.run();
    }

    @Test
    void run_yeast() throws IOException {
        GraphFileReader<String,DefaultWeightedEdge> reader = new GraphFileReader<>(String.class,DefaultWeightedEdge.class);
        udG1 = reader.readToUndirectedGraph(
                "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt",false);
        udG2 = reader.readToUndirectedGraph(
                "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt",false);
        SimMatReader<String> simMatReader = new SimMatReader<>(udG1.vertexSet(),udG2.vertexSet(),String.class);
        simMat = simMatReader.readToSimMat(
                "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/fasta/yeastHumanSimList_EvalueLessThan1e-10.txt",true);
        hga = new HGA<>(simMat, udG1, udG2, 0.4, true,  0.5,0.01);
        HGA.debugOutputPath = "src\\test\\java\\resources\\jupiter\\data\\";
        HGA.GPU = true;
        hga.run();
    }



}

