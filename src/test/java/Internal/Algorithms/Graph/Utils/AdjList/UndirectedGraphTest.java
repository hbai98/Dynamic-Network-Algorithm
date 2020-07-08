package Internal.Algorithms.Graph.Utils.AdjList;

import Internal.Algorithms.IO.GraphFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


class UndirectedGraphTest {
    UndirectedGraph graph;
    private UndirectedGraph undG1;
    private UndirectedGraph undG2;
    private GraphFileReader reader;

    private final String g1Path = "src/test/java/resources/AlgTest/HGA/graph1.txt";
    private final String g2Path = "src/test/java/resources/AlgTest/HGA/graph2.txt";

    @BeforeEach
    void setUp() throws IOException {
        reader = new GraphFileReader(true, false, false);
        undG1 = reader.readToUndirectedGraph(g1Path, false);
        undG2 = reader.readToUndirectedGraph(g2Path, false);
    }

    @Test
    void check() throws IOException {
        DirectedGraph G1 = reader.readToDirectedGraph(g1Path,true);
        assertEquals(15,undG1.getEdgeCount());
        assertEquals(10,undG1.getAllNodes().size());
        assertEquals(13,undG2.getEdgeCount());
        assertEquals(9,undG2.getAllNodes().size());
        assertEquals(G1.getRowSet(),undG1.getRowSet());
        assertEquals(G1.getColSet(),undG1.getColSet());
    }

    @Test
    void ToDirectTest() throws IOException {
        DirectedGraph G1 =  reader.readToDirectedGraph(g1Path,true);
        DirectedGraph test = undG1.toDirect();
        assertEquals(G1.getRowSet(),test.getRowSet());
        assertEquals(G1.getColSet(),test.getColSet());
    }
}