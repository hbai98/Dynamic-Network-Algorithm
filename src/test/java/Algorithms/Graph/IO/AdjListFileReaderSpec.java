package Algorithms.Graph.IO;

import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static Algorithms.Graph.IO.AdjListFileReader.*;
@DisplayName("SIFFileReader is")
class AdjListFileReaderSpec {
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
    @DisplayName("able to read a graph from the specific well-formatting file. ")
    @Test
    void ReadTest(){
        try {
            EdgeList graph = read("src/test/java/resources/IOTest/simpleGraph_1.txt");
            assertThat(graph).contains(new Edge("A","B",0.2),new Edge("A","C",0.3),
                    new Edge("A","D",0.4),new Edge("C","B",0.7));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            EdgeList graph = read("src/test/java/resources/IOTest/wrongForTest_1.txt");
        } catch (IOException e) {
            assertEquals("The file input format is not correct. Plus: some name-value pairs are incorrect!", e.getMessage());
        }
    }
}