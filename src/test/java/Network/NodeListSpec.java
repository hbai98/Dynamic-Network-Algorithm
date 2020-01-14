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
        private Node node3;
        private Node node4;

        @BeforeEach
        void init(){
            nodeList = new NodeList();
            node1 = new Node("1",2.3,"1");
            node2 = new Node("2",2.5,"1");
            node3 = new Node("1",2.8,"1");
            node4 = new Node("1",2.0,"1");
        }
        @DisplayName("can only adds node with greater value.")
        @Test
        void add() {

            // prepare list with elements
            assertTrue(nodeList.add(node1));
            assertTrue(nodeList.add(node2));
            // test
            assertTrue(nodeList.add(node3));
            assertEquals(2,nodeList.size());
            assertFalse(nodeList.add(node4));
        }
        @DisplayName("sorts based on names")
        @Test
        void sort(){
            nodeList.add(node2);
            nodeList.add(node1);

            nodeList.sort();
            assertThat(nodeList).containsSequence(Arrays.asList(node1,node2));
        }
        @DisplayName("adds according to node ascending value.")
        @Test
        void vAscdAdd(){
            nodeList.add(node1);
            nodeList.add(node2);

            nodeList.vAscAdd(node3);
            assertThat(nodeList).containsSequence(Arrays.asList(node1,node2,node3));
            nodeList.vAscAdd(node4);
            assertThat(nodeList).containsSequence(Arrays.asList(node4,node1,node2,node3));


        }
    }
    @Nested
    class Other{
        @DisplayName("should be null is nothing has been added. ")
        @Test
        void nullTest(){
            assertNull(nodeList);
        }
    }

}