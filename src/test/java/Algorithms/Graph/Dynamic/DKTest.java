package Algorithms.Graph.Dynamic;

import Algorithms.Graph.Dynamic.Diffusion_Kernel.DK;
import DS.Matrix.StatisticsMatrix;
import DS.Network.UndirectedGraph;
import IO.GraphFileReader;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

class DKTest {
    private UndirectedGraph<String, DefaultWeightedEdge> graph;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader<String, DefaultWeightedEdge> reader = new GraphFileReader<>(String.class, DefaultWeightedEdge.class);
        graph = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/small/sGraph1.txt",true);
    }

    @Test
    void run() {
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
    void inverse(){
        StatisticsMatrix statisticsMatrix = StatisticsMatrix.createIdentity(2);
        statisticsMatrix.convertToSparse();
        StatisticsMatrix res = statisticsMatrix.invert();

    }

}