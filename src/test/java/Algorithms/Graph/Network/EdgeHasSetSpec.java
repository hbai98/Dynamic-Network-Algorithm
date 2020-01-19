package Algorithms.Graph.Network;
// Author: Haotian Bai
// Shanghai University, department of computer science
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DisplayName("EdgeList is")
class EdgeHasSetSpec {
    EdgeHasSet edges = new EdgeHasSet();
    private Edge edge_1;
    private Edge edge_2;
    private Edge edge_3;
    private Edge edge_4;

    @BeforeEach
    void init(){
        edge_1 = new Edge("1","2",1.0, Edge.Type.UNDIRECTED);
        edge_2 = new Edge("3","4",1.0, Edge.Type.UNDIRECTED);
        edge_3 = new Edge("2","1",1.0, Edge.Type.UNDIRECTED);
        edge_4 = new Edge("5","3",1.0, Edge.Type.UNDIRECTED);

        edges.addAll(Arrays.asList(edge_1,edge_2,edge_3));
    }
    @DisplayName("consist of UNDIRECTED edges")
    @Test
    void undirectedTest(){
        assertEquals(edge_1,edge_3);
    }
    @DisplayName("capable of adding distinct edges.")
    @Test
    void add(){
        edges.add(new Edge("1","2",1.0, Edge.Type.UNDIRECTED));
        assertEquals(2, edges.size());
        edges.add(edge_1);
        assertEquals(2,edges.size());
    }
    @Nested
    class FuncTest {
        @BeforeEach
        void init(){
            edge_1 = new Edge("1","2",1.0, Edge.Type.UNDIRECTED);
            edge_2 = new Edge("3","4",1.0, Edge.Type.UNDIRECTED);
            edge_3 = new Edge("2","1",1.0, Edge.Type.UNDIRECTED);
            // keep ascending order or it will be reverted.
            edge_4 = new Edge("3","5",1.0, Edge.Type.UNDIRECTED);
            edges.addAll(Arrays.asList(edge_1,edge_2,edge_4));
        }
        @DisplayName("able to get edges containing the certain node")
        @Test
        void getVetEdg() {
            EdgeHasSet tpList = edges.getVetEdg(new Node("3"));
            assertThat(tpList).contains(edge_2,edge_4);
        }
        @DisplayName("able to print all edges")
        @Test
        void printTest(){
            edges.printAll();
        }
    }
}