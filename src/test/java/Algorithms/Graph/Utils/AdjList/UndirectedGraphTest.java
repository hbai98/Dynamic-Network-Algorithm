package Algorithms.Graph.Utils.AdjList;

import IO.GraphFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


class UndirectedGraphTest {
    UndirectedGraph graph;
    private UndirectedGraph undG1;
    private UndirectedGraph undG2;

    @BeforeEach
    void setUp() throws IOException {
        GraphFileReader reader = new GraphFileReader(true, false, false);
        undG1 = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph1.txt", false);
        undG2 = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/graph2.txt", true);
    }

    @Test
    void check(){
        assertEquals(15,undG1.getEdgeCount());
        assertEquals(10,undG1.getAllNodes().size());
        assertEquals(13,undG2.getEdgeCount());
        assertEquals(9,undG2.getAllNodes().size());
    }
}