<<<<<<< Updated upstream:src/test/java/Algorithms/Graph/Network/NodeSpec.java
package Algorithms.Graph.Network;
=======
package Internal.Algorithms.DS.Network.Extend.Network;
>>>>>>> Stashed changes:src/test/java/DS/Network/Extend/Network/NodeSpec.java
// Author: Haotian Bai
// Shanghai University, department of computer science
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("The node")
class NodeSpec {
    @DisplayName(" is comparable")
    @org.junit.jupiter.api.Test
    void compareTo() {
        Node node1 =new Node("CC",2.0,"K");
        Node node2 =new Node("GC",2.0,"K");
        assertTrue(node1.compareTo(node2)<0,"The nodes should be comparable.");
    }
    @DisplayName("equal() has been overloaded. ")
    @Test
    void NodeTest(){
        assertEquals(new Node("1"), new Node("1"));
    }
}