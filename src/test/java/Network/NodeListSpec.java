package Network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DisplayName("This list")
class NodeListSpec {
    private NodeList nodeList;
    @Nested
    class functionTest{
        private Node node1;
        private Node node2;

        @BeforeEach
        void init(){
            nodeList = new NodeList();
            node1 = new Node("1",2.3,"1");
            node2 = new Node("2",2.5,"1");




        }
        @DisplayName("can only adds node with greater value.")
        @Test
        void add() {
            Node node3 = new Node("1",2.8,"1");
            Node node4 = new Node("1",2.0,"1");
            // prepare list with elements
            assertTrue(nodeList.add(node1));
            assertTrue(nodeList.add(node2));
            // test
            assertTrue(nodeList.add(node3));
            assertEquals(2,nodeList.size());
            assertFalse(nodeList.add(node4));
        }
    }
    @Nested
    class Other{
        @Test
        void test(){
            assertNull(nodeList);
        }
    }

}