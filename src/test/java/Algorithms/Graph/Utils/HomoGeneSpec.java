package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.NodeList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("The homeGene list is")
class HomoGeneSpec {
    private Node node1;
    private Node node2;
    private Node node3;
    private Node node4;
    private HomoGene homoGene;

    @BeforeEach
    void init(){
        homoGene = new HomoGene("3232");
        node1 = new Node("1",2.3,"1");
        node2 = new Node("2",2.5,"1");
        node3 = new Node("1",2.8,"1");
        node4 = new Node("1",2.0,"1");
    }


    @DisplayName("able to add proteins with higher value(inheritance)")
    @Test
    void add(){
        // prepare list with elements
        assertTrue(homoGene.add(node1));
        assertTrue(homoGene.add(node2));
        // test
        assertTrue(homoGene.add(node3));
        assertEquals(2,homoGene.size());
        assertFalse(homoGene.add(node4));
    }
}