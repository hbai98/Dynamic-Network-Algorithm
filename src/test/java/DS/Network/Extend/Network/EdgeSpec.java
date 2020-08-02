<<<<<<< Updated upstream:src/test/java/Algorithms/Graph/Network/EdgeSpec.java
package Algorithms.Graph.Network;
=======
package Internal.Algorithms.DS.Network.Extend.Network;
>>>>>>> Stashed changes:src/test/java/DS/Network/Extend/Network/EdgeSpec.java
// Author: Haotian Bai
// Shanghai University, department of computer science
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("This edge is")
class EdgeSpec {
    Edge edge_1;
    Edge edge_2;
    Edge edge_3;

    @BeforeEach
    void init(){
        edge_1 = new Edge("1","2",1.0, Edge.Type.UNDIRECTED);
        edge_2 = new Edge("3","4",1.0, Edge.Type.UNDIRECTED);
        edge_3 = new Edge("1","1",1.0, Edge.Type.UNDIRECTED);

    }
//    @DisplayName("comparable according to the rules predefined.")
//    @Test
//    void compareTo() {
//        assertTrue(edge_1.compareTo(edge_2)<0);
//        assertTrue(edge_1.compareTo(edge_3)>0);
//    }
    @DisplayName("able to construct in 3 parameters")
    @Test
    void constructorTest() {
        Edge edgeTest = new Edge("1","2",1.0);
        assertEquals(edgeTest,edge_1);
    }
}