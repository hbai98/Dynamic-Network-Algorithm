package Internal.Algorithms.Graph.HGA;

import Internal.Algorithms.DS.Network.AdjList.UndirectedGraph;
import Internal.Algorithms.DS.Network.SimMat;
import Internal.Algorithms.IO.GraphFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class HGARunSpec {
    UndirectedGraph undG1;
    UndirectedGraph undG2;
    SimMat simMat;
    HGA hga;

    @BeforeEach
    void init() throws IOException {

    }
    @Test
    void run_test() throws IOException {
        GraphFileReader reader = new GraphFileReader(true, false, false);
        undG1 = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph1.txt", false);
        undG2 = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph2.txt", false);
        reader.setRecordNeighbors(false);
        simMat = reader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt", undG1.getAllNodes(), undG2.getAllNodes(), true);
        hga = new HGA(simMat, undG1, undG2, 0.5,true,0.5,0.01);
        hga.run();
    }
    @Test
    void run_test_GPU() throws IOException {
        GraphFileReader reader = new GraphFileReader(true, false, false);
        undG1 = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph1.txt", false);
        undG2 = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph2.txt", false);
        reader.setRecordNeighbors(false);
        simMat = reader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt", undG1.getAllNodes(), undG2.getAllNodes(), true);
        hga = new HGA(simMat, undG1, undG2, 0.5,true,0.5,0.01);
        HGA.GPU = true;
        hga.run();
    }

    @Test
    void run_yeast() throws IOException {
        // reader for reading undirected graphs and the similarity matrix
        GraphFileReader reader = new GraphFileReader(true, false, false);
        // read graphs with the file path
        undG1 = reader.readToUndirectedGraph("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt", false);
        undG2 = reader.readToUndirectedGraph("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt", false);
        // undirected graphs don't have to record neighbors
        reader.setRecordNeighbors(false);
        // read the simMat
        simMat = reader.readToSimMat("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/fasta/yeastHumanSimList_EvalueLessThan1e-10.txt",
                undG1.getAllNodes(), undG2.getAllNodes(), true);
        // hga init
        hga = new HGA(simMat, undG1, undG2, 0.4,true,0.5,0.01);
        // specify where you want the log matrix, score, and mapping result.
        HGA.debugOutputPath = "src\\test\\java\\resources\\Jupiter\\data\\";
        HGA.GPU = true;
        hga.run();
    }

}
