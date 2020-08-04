package DS.Network;

import IO.GraphFileReader;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UndirectedGraphTest {
    private UndirectedGraph<Character, DefaultEdge> graph;

    @BeforeEach
    void init() throws IOException {
        GraphFileReader<Character, DefaultEdge> reader = new GraphFileReader<>(Character.class,DefaultEdge.class);
        graph = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/HGA/small/sGraph1.txt",true);
    }
    @DisplayName("Get a adjacent matrix representation of the graph")
    @Test
    void getAdjMat() {
        // vertex set: A C D E B F G H
        boolean[] adj = graph.getAdjMat();
    }
}