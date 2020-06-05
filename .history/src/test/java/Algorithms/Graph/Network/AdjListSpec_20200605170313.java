package Algorithms.Graph.Network;

import Algorithms.Graph.Utils.HNodeList;
import IO.GraphFileReader;
import org.jblas.DoubleMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DisplayName("This AdjList is")
class AdjListSpec {
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
        assertEquals(3, graph.size());
    }
    @DisplayName("able to remove one Node.")
    @Test
    void removeOneTest(){
        graph.add(homo_1);
        graph.add(homo_3);
        graph.removeAllNode("3232","1");
        assertEquals(1,graph.get(1).size());
    }
    @DisplayName("able to output the matrix.")
    @Test
    void OutPutTest(){
        graph.add(homo_1);
        homo_2 = new HNodeList("0000");
        homo_2.addAll(Arrays.asList(new Node("1",0.3),new Node("2",0.1)));
        graph.add(homo_2);
        graph.add(homo_3);
        DoubleMatrix matrix = graph.getMatrix();
        DoubleMatrix test = new DoubleMatrix(new double[][]{
                {0.3,0.1},{0.3,0.6},{0.2,0.2}
        } );
        assertEquals(test,matrix);
    }
    @DisplayName("able to output the matrix.")
    @Test
    void OutPut_2Test(){
        homo_1 = new HNodeList("3232");
        homo_1.addAll(Arrays.asList(new Node("5",0.2),new Node("2",0.2)));
        graph.add(homo_1);
        homo_2 = new HNodeList("0000");
        homo_2.addAll(Arrays.asList(new Node("1",0.3),new Node("4",0.1),new Node("3",0.4)));
        graph.add(homo_2);
        homo_3 = new HNodeList("1211");
        homo_3.addAll(Arrays.asList(new Node("1",0.3),new Node("2",0.6)));
        graph.add(homo_3);
        DoubleMatrix matrix = graph.getMatrix();
        DoubleMatrix test = new DoubleMatrix(new double[][]{
                {0.3,0.,0.4,0.1,0.},{ 0.3,0.6,0.,0.,0.},{0.,0.2,0.,0.,0.2}

        } );
        assertEquals(test,matrix);
    }
    @DisplayName("can't find matrix's value if the arrayList don't have a order.")
    @Test
    void findMatrixValTest_1() throws IOException {
        graph.add(homo_1);
        homo_2 = new HNodeList("0000");
        homo_2.addAll(Arrays.asList(new Node("1",0.3),new Node("2",0.1),new Node("3",0.4)));
        graph.add(homo_2);
        graph.add(homo_3);
        assertNotEquals(0.1,graph.getValByName("0000","1"));
    }
    @DisplayName("can find matrix's value if the arrayList has a order.")
    @Test
    void findMatrixValTest_2() throws IOException {
        homo_1.sortAddAll(new Node("5",0.2),new Node("3",0.6));
        graph.add(homo_1);
        homo_2 = new HNodeList("0000");
        homo_2.sortAddAll(new Node("1",0.3),new Node("2",0.1),new Node("3",0.4));
        graph.add(homo_2);
        assertEquals(0.3,graph.getValByName("0000","1"));
    }
    @DisplayName("can find the row's max value.")
    @Test
    void findMaxTest(){
        homo_1.addAll(Arrays.asList(new Node("1",0.2),new Node("2",0.8)));
        graph.add(homo_1);
        assertEquals(0.8,graph.findMaxOfList("3232").getSecond().getValue());
    }
    @DisplayName("can find all neighbors of a node.")
    @Test
    void sortGetNeighborsListSpec() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        AdjList graph = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/graph1.txt");
        HNodeList list = graph.sortGetNeighborsList("B");
        HNodeList res = new HNodeList("B");
        res.sortAddAll("F","G","M","Q","A");
        // check components even the equal() fpr HNodeList has been overloaded
        assertEquals(res,list);

    }

}
