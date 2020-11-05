package Algorithms.Graph.Dynamic;

import Algorithms.Graph.Dynamic.Diffusion_Kernel.DK;
import DS.Matrix.StatisticsMatrix;
import DS.Network.UndirectedGraph;
import IO.GraphFileReader;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

class DKTest {
    UndirectedGraph<String, DefaultWeightedEdge> graph;
    GraphFileReader<String, DefaultWeightedEdge> reader;

    @BeforeEach
    void init() throws IOException {
        reader = new GraphFileReader<>(String.class, DefaultWeightedEdge.class);
    }

    @Test
    void run() throws IOException {
        graph = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/small/sGraph1.txt",true);
        UndirectedGraph<String,DefaultWeightedEdge> src = new UndirectedGraph<>(DefaultWeightedEdge.class);
        src.addVertex("F");
        src.addVertex("C");
        DK<String,DefaultWeightedEdge> dk = new DK<>(src,graph,0.5) ;
        dk.run();
        StatisticsMatrix res = dk.getResult();
        // normalize to percentage
        double sum = res.elementSum();
        Arrays.stream(res.data()).forEach(e->System.out.printf("%f.2\n",e/sum*100));
    }

    @Test
    void inverse() throws IOException {
        graph = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/small/sGraph1.txt",true);
        StatisticsMatrix statisticsMatrix = StatisticsMatrix.createIdentity(2);
        statisticsMatrix.convertToSparse();
        StatisticsMatrix res = statisticsMatrix.invert();
    }

    @Test
    void test_nCov_host_diffuse(){
//        UndirectedGraph<String,DefaultWeightedEdge> host = reader.readToUndirectedGraph("");
    }

}