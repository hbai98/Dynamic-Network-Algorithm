package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DisplayName("This homoGeneMap is")
class hNodeListMapSpec {
    private HNodeList homo_1;
    private HNodeList homo_2;
    private HNodeList homo_3;
    AdjList graph;
    @BeforeEach
    void init(){
        graph = new AdjList();
        homo_1 = new HNodeList("3232");
        homo_2 = new HNodeList("3232");
        homo_3 = new HNodeList("1211");
        homo_1.addAll(Arrays.asList(new Node("1",0.2),new Node("2",0.2)));
        homo_2.addAll(Arrays.asList(new Node("1",0.3),new Node("2",0.1)));
        homo_3.addAll(Arrays.asList(new Node("1",0.3),new Node("2",0.6)));
    }


    @DisplayName("able to add proteins with higher value(inheritance)")
    @Test
    void add(){
        graph.add(homo_1);
        graph.add(homo_2);
        assertThat(graph.get(0)).containsSequence(new Node("1",0.3),new Node("2",0.2));
        graph.add(homo_3);
        assertEquals(2, graph.size());
    }
    @DisplayName("able to remove one HG")
    @Test
    void removeOneHGTest(){
        graph.add(homo_1);
        graph.add(homo_3);
        graph.removeOneNode("3232","1");
        assertEquals(1,graph.get(1).size());
    }
}