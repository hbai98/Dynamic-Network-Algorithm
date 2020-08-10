package Algorithms.Graph.Dynamic;

import Algorithms.Graph.Dynamic.Diffusion_Kernel.DK;
import DS.Matrix.StatisticsMatrix;
import DS.Network.UndirectedGraph;
import IO.GraphFileReader;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

class DKTest {
    private UndirectedGraph<String, DefaultEdge> graph;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader<String, DefaultEdge> reader = new GraphFileReader<>(String.class,DefaultEdge.class);
        graph = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/small/sGraph1.txt",true);
    }

    @Test
    void run() {
        UndirectedGraph<String,DefaultEdge> src = new UndirectedGraph<>(DefaultEdge.class);
        src.addVertex("F");
        src.addVertex("C");
        DK<String,DefaultEdge> dk = new DK<>(src,graph,0.5) ;
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