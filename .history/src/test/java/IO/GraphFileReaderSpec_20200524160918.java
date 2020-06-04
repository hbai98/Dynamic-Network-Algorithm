package IO;

import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHasSet;
import Algorithms.Graph.Utils.HNodeList;
import Algorithms.Graph.Network.AdjList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SIFFileReader is")
public class GraphFileReaderSpec {
    AdjList graph;
    GraphFileReader reader;
    @DisplayName("able to use pattern to split. ")
    @Test
    // The first character cannot be space.-> test whether each element for it's length > 0
    void PatternTest() {
        String test = "TMA STD    BBQ";
        String test2 = " TMA STD    BBQ";
        Pattern splitter = Pattern.compile("\\s+");
        String[] tokens = splitter.split(test);
        String[] tokens_2 = splitter.split(test2);
        ArrayList<String> tokens_3 = new ArrayList<>();
        for (String str:tokens_2
             ) {
            if(str.length()!=0){
                tokens_3.add(str);
            }
        }
        assertEquals(Arrays.toString(new String[]{"TMA", "STD", "BBQ"}), Arrays.toString(tokens));
        assertNotEquals(Arrays.toString(new String[]{"TMA", "STD", "BBQ"}), Arrays.toString(tokens_2));
        assertEquals(Arrays.toString(new String[]{"TMA", "STD", "BBQ"}), tokens_3.toString());
    }
    @DisplayName("able to read a graph to EdgeList from the specific well-formatting file. ")
    @Test
    void ReadTest(){
        try {
            GraphFileReader reader = new GraphFileReader();
            EdgeHasSet graph = reader.readToEL("src/test/java/resources/IOTest/simpleGraph_1.txt");
            // the EdgeList graph is not a self-update graph (automatic replace edges with higher values)
            assertThat(graph).contains(new Edge("A","B",0.2),new Edge("A","C",0.6),new Edge("A","C",0.3),
                    new Edge("A","D",0.4),new Edge("C","B",0.7));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            GraphFileReader reader = new GraphFileReader();
            EdgeHasSet graph = reader.readToEL("src/test/java/resources/IOTest/wrongForTest_1.txt");
        } catch (IOException e) {
            assertEquals("The file input format is not correct. Plus: some name-value pairs are incorrect!", e.getMessage());
        }
    }

    @DisplayName("able to read a graph to ArrayList from the specific well-formatting file. ")
    @Test
    void ReadToArrayListTest(){
        try {
            // homoGeneMap is a self-update graph
            GraphFileReader reader = new GraphFileReader();
            AdjList graph = reader.readToAdjL("src/test/java/resources/IOTest/simpleGraph_1.txt");
            HNodeList forTestA = new HNodeList("A");
            HNodeList forTestB = new HNodeList("C");
            forTestA.add("B",0.2);
            forTestA.add("C",0.3);
            forTestA.add("D",0.4);
            forTestB.add("A",0.6);
            forTestB.add("B",0.7);

            assertThat(graph).contains(forTestA,forTestB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("able to read a medium-level graph")
    @Test
    void ReadMediumGraph() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        AdjList graph = reader.readToAdjL("src/test/java/resources/IOTest/mediumGraph.txt");
        assertEquals(0.986,graph.getValByName("RPS9A","RPS9"));
        assertEquals(0.756,graph.getValByName("YRF1-4","EIF4A3"));
    }
    @DisplayName("able to get a HashSet.")
    @Test
    void getHashSet() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        AdjList graph = reader.readToAdjL("src/test/java/resources/IOTest/mediumGraph.txt");
        HashSet<String> graph1 = reader.getHeadSet();
    }

    @DisplayName("TSV test")
    @Test
    void readTSV() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        AdjList graph = reader.readToAdjL("C:\\Users\\Haotian Bai\\Desktop\\cov19\\A1.tsv");
    }
    // ---------------------------
    // below are some base test class utility methods to validate if the outcome is consistent with your expectation.
    protected AdjList readAndAssertNodeNumber(int expected,String filePath) throws IOException {
        if(reader == null) reader = new GraphFileReader();
        if(reader == null) graph = reader.readToAdjL(filePath);
        assertEquals(expected,graph.getAllNodes().size(), "nodes' number is not right.");
        return graph;
    }

    protected AdjList readAndAssertEdgeNumber(int expected,String filePath) throws IOException {
        if(reader == null) reader = new GraphFileReader();
        AdjList graph = reader.readToAdjL(filePath);
        assertEquals(expected,graph.getAllEdges().size(), "edges' number is not right.");
        return graph;
    }

}
