package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.List.HNodeList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("The homeGene list is")
class hNodeListSpec {
    private Node node1;
    private Node node2;
    private Node node3;
    private Node node4;
    private HNodeList hNodeList;

    @BeforeEach
    void init(){
        hNodeList = new HNodeList("3232");
        node1 = new Node("1",2.3,"1");
        node2 = new Node("2",2.5,"1");
        node3 = new Node("1",2.8,"1");
        node4 = new Node("1",2.0,"1");
    }


    @DisplayName("able to add proteins with higher value(inheritance)")
    @Test
    void add(){
        // prepare list with elements
        assertTrue(hNodeList.add(node1));
        assertTrue(hNodeList.add(node2));
        // test
        assertTrue(hNodeList.add(node3));
        assertEquals(2, hNodeList.size());
        assertFalse(hNodeList.add(node4));
    }
}