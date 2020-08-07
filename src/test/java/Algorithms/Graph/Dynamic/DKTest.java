package Algorithms.Graph.Dynamic;

import DS.Matrix.StatisticsMatrix;
import DS.Network.UndirectedGraph;
import IO.GraphFileReader;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DKTest {
    private UndirectedGraph<Character, DefaultEdge> graph;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader<Character, DefaultEdge> reader = new GraphFileReader<>(Character.class,DefaultEdge.class);
        graph = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/small/sGraph1.txt",true);
    }

    @Test
    void run() {
        UndirectedGraph<Character,DefaultEdge> src = new UndirectedGraph<>(DefaultEdge.class);
        src.addVertex('F');
        src.addVertex('C');
        DK<Character,DefaultEdge> dk = new DK<>(src,graph,0.5) ;
        dk.run();
        StatisticsMatrix res = dk.getResult();
    }

    @Test
    void inverse(){
        StatisticsMatrix statisticsMatrix = StatisticsMatrix.createIdentity(2,true);
        statisticsMatrix.convertToSparse();
        StatisticsMatrix res = statisticsMatrix.invert();

    }

}