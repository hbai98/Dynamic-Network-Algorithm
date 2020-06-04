/*
 * @Author: Haotian Bai
 * @Github: https://github.com/164140757
 * @Date: 2020-05-24 15:54:00
 * @LastEditors: Haotian Bai
 * @LastEditTime: 2020-05-24 17:03:29
 * @FilePath: \Algorithms\src\test\java\IO\humanYeastTest\Human_YeastSub38NTest.java
 * @Description:  
 */
package IO.humanYeastTest;

import IO.GraphFileReaderSpec;
import Tools.Stopwatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Algorithms.Graph.Network.AdjList;

@DisplayName("Reader is able to ")
public class Human_YeastSub38NTest extends GraphFileReaderSpec {
    AdjList yeast38;
    AdjList human9141;

    @DisplayName("read yeast38 and it has 38 nodes and 131 edges.")
    @Test
    void yeast38Input() throws IOException {
        Stop
        String yeast38path = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt";
        yeast38 = readAndAssertNodeNumber(38, yeast38path);
        yeast38 = readAndAssertEdgeNumber(131, yeast38path);
        
    }
    @DisplayName("read human9141 and it has 9141 nodes and 41456 edges.")
    @Test
    void humanNetworkInput() throws IOException {
        String human9141path = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt";
        human9141 = readAndAssertNodeNumber(9141, human9141path);
        System.out.println(human9141.getAllEdges().size());
    }
    @DisplayName("IO optimize.")
    @Test
    void IPOptimizeTest() throws IOException {
        
    }
        
    
    

}
