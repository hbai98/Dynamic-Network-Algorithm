package Algorithms.Graph.Dynamic.Scale;

import DS.Network.UndirectedGraph;
import IO.Reader.GraphFileReader;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


class ScaleTest {

    private UndirectedGraph<String, DefaultWeightedEdge> udG;

    @BeforeEach
    void setUp() throws IOException {
        GraphFileReader<String, DefaultWeightedEdge> reader = new GraphFileReader<>(String.class,DefaultWeightedEdge.class);
        udG = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph1.txt", true);
    }
    @Test
    void propagate(){
        Set<String> nodes = new HashSet<>(Arrays.asList("G", "B","F","Q","W"));
        Scale<String, DefaultWeightedEdge> scale = new Scale<>(nodes, udG);
        boolean res = scale.propagate(1);
    }

}