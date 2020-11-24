package Algorithms.Graph.Dynamic.Search;

import DS.Network.UndirectedGraph;
import IO.Reader.GraphFileReader;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BFSTest {
    @Test
    void dfs_longest() throws IOException {
        GraphFileReader<String, DefaultEdge> reader = new GraphFileReader<>(String.class,DefaultEdge.class);
        UndirectedGraph<String, DefaultEdge> udG = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph1.txt", false);
        // bsf
        BFS<String, DefaultEdge> bfs = new BFS<>(udG);
//        assertEquals();
    }
}