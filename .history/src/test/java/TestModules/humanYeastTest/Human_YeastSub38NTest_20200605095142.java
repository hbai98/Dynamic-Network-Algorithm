/*
 * @Author: Haotian Bai
 * @Github: https://github.com/164140757
 * @Date: 2020-05-24 15:54:00
 * @LastEditors: Haotian Bai
 * @LastEditTime: 2020-06-05 09:51:41
 * @FilePath: \Algorithms\src\test\java\TestModules\humanYeastTest\Human_YeastSub38NTest.java
 * @Description:  
 */
package TestModules.humanYeastTest;

import IO.GraphFileReaderSpec;
import Tools.Stopwatch;
import tech.tablesaw.api.Table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Vector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Algorithms.Graph.Network.AdjList;

@DisplayName("Reader is able to ")
public class Human_YeastSub38NTest extends GraphFileReaderSpec {
    AdjList yeast;
    AdjList human;
    AdjList simList;
    Stopwatch stopwatch;
    @BeforeEach
    void init(){
        stopwatch = new Stopwatch();
    }
    @DisplayName("read yeast38 and it has 38 nodes and 131 edges.")
    @Test
    void yeast38Input() throws IOException {
        stopwatch.start();
        String yeast38path = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt";
        yeast = readAndAssertNodeNumber(38, yeast38path);
        yeast = readAndAssertEdgeNumber(131, yeast38path);
        System.out.println("Elapsed :"+ stopwatch.elapsedTime()+" second."); 
        //Elapsed :0.014 second.   before adding the AQ    
    }
    @DisplayName("read human9141 and it has 9141 nodes and 41456 edges.")
    @Test
    void humanNetworkInput() throws IOException {
        stopwatch.start();
        String human9141path = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt";
        human = readAndAssertNodeNumber(9141, human9141path);
        human = readAndAssertEdgeNumber(41456, human9141path);
        System.out.println("Elapsed :"+ stopwatch.elapsedTime()+" second.");     
        //Elapsed :159.722 second.   before adding the AQ       
    }
    @DisplayName("read simList and it has 50597 nodes.")
    @Test
    void readSimilarityMatrix() throws IOException {
        String humanPath = "src/test/java/resources/TestModule/HGATestData/Human-Yeast/HumanNet.txt";
        String yeastPath = "src/test/java/resources/TestModule/HGATestData/Human-Yeast/YeastNet.txt";
        yeast = readAdjList(yeastPath); // 2390
        human = readAdjList(humanPath); // 9141

        stopwatch.start();
        String simPath = "src/test/java/resources/TestModule/HGATestData/Human-Yeast/table.Yeast-Human.txt";
        simList = readAdjList(simPath);
        // 4476*6144[which is not correct!]
        System.out.println("Elapsed :"+ stopwatch.elapsedTime()+" second.");   
        // Elapsed :12.05 second.
        // reassure it contains all nodes for two graphs
        // yeast
        assertTrue(simList.getRowSet().containsAll(yeast.getAllNodes()));
        assertTrue(simList.getColSet().containsAll(human.getAllNodes()));
        System.out.println("yeast proteins number:"+simList.getRowSet().size()+"\nhuman proteins' number:"+simList.getColSet().size());
    }
    @DisplayName("read simList from local blast result.")
    @Test
    void readSimilarityMatrixFromLocalBlast() throws IOException {
        Table table = Table.read().csv("src\\test\\java\\resources\\TestModule\\HGATestData\\Human-Yeast\\resoult.out");
        HashMap<String,Integer> map = new HashMap<>();
        table.stream().
    }
    
    
    @DisplayName("HGA mapping.")
    @Test
    void HGATest() throws IOException {
        String humanPath = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt";
        String yeastPath = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt";
        yeast = readAdjList(yeastPath);
        human = readAdjList(humanPath);
    }
    // @DisplayName("optimize IO.")
    // @Test
    // void IPOptimizeTest() throws IOException {
        
    // }
        
    
    

}
