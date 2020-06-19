/*
 * @Author: Haotian Bai
 * @Github: https://github.com/164140757
 * @Date: 2020-05-24 15:54:00
 * @LastEditors: Haotian Bai
 * @LastEditTime: 2020-06-05 10:19:38
 * @FilePath: \Algorithms\src\test\java\TestModules\humanYeastTest\Human_YeastSub38NTest.java
 * @Description:
 */
package Algorithms.Graph.HGA;

import Algorithms.Graph.HGA.HGA;
import Algorithms.Graph.Utils.AdjList.Graph;
import Algorithms.Graph.Utils.SimMat;
import IO.AbstractFileWriter;
import IO.GraphFileReader;
import IO.GraphFileReaderSpec;
import Tools.Stopwatch;
import org.jblas.DoubleMatrix;
import tech.tablesaw.api.*;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tech.tablesaw.plotly.traces.ScatterTrace;

@DisplayName("Reader is able to ")
public class Human_YeastSub38NTest extends GraphFileReaderSpec {
    Graph yeast;
    Graph human;
    SimMat simMat;
    HGA hga;
    Stopwatch stopwatch;

    @BeforeEach
    void init() throws IOException {
        stopwatch = new Stopwatch();
        GraphFileReader reader = new GraphFileReader(true, true, false);
        yeast = reader.readToGraph("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt", false);
        human = reader.readToGraph("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt", false);
        reader.setRecordNonZeros(true);
        reader.setRecordNeighbors(false);
        simMat = reader.readToSimMat("src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/yeastHumanSimList_EvalueLessThan1e-10.txt", yeast.getAllNodes(), human.getAllNodes(), true);
        hga = new HGA(simMat, yeast, human, 0.4);
    }


    @DisplayName("Topo")
    @Test
    void topo(){
        hga.addAllTopology();
    }

    @DisplayName("read simList from local blast result.")
    @Test
    void readSimilarityMatrixFromLocalBlast() throws IOException {
        Table table = Table.read().csv("src\\test\\java\\resources\\TestModule\\HGATestData\\Human-Yeast\\fasta\\result.csv");
        HashMap<String, Integer> map = new HashMap<>();
        // record max count
        table.stream().forEach(
                row -> {
                    String yeast = row.getString(0).split("\\u007C")[2].split("_YEAST")[0];
                    // count
                    if (!map.containsKey(yeast)) {
                        map.put(yeast, 1);
                    } else {
                        int num = map.get(yeast) + 1;
                        map.put(yeast, num);
                    }
//                String human = row.getString(1);
                }
        );
        // find max
        // hashmap to visualize
        StringColumn names = StringColumn.create("yeast_name");
        IntColumn counts = IntColumn.create("max_hit");
        int max = -Integer.MAX_VALUE;
        for (Map.Entry<String, Integer> entry:map.entrySet()) {
            String k = entry.getKey();
            Integer v = entry.getValue();
            if(v>max){
                max = v;
            }
            names.append(k);
            counts.append(v);
        }
        IntColumn MAX = IntColumn.create("max");
        int total = map.size();
        while(total-->0) {
            MAX.append(max);
        }
        ScatterTrace lineTrace = ScatterTrace.builder(names, MAX)
                .mode(ScatterTrace.Mode.LINE)
                .name("max")
                .build();
        ScatterTrace scatterTrace = ScatterTrace.
                builder(names, counts)
                .mode(ScatterTrace.Mode.LINE)
                .name("count")
                .build();
        Axis xAxis = Axis.builder().title("yeast proteins").build();
        Axis yAxis = Axis.builder().title("hit counts").build();
        Layout layout = Layout.builder().title("E-value <= 10 filtering for yeast and human").xAxis(xAxis).yAxis(yAxis).build();
        Plot.show(new Figure(layout, scatterTrace,lineTrace));
        // yeast 6641; human 9588
    }


//    @DisplayName("HGA initialization.")
//    @Test
//    void PrepareHGATest() throws IOException {
//        String humanPath = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt";
//        String yeastPath = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt";
//        yeast = readAdjList(yeastPath);// 38
//        human = readAdjList(humanPath);// 9141
//        simList = getReadySimList("src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/yeastHumanSimList_EvalueLessThan1e-10.csv");
//        // simList 6612*9574(evalue <= 10)
//        // reassure it contains all nodes for two graphs
//        // yeast
//        assertTrue(simList.getUpdatedRowSet().containsAll(yeast.getAllNodes()));
//        assertTrue(simList.getColSet().containsAll(human.getAllNodes()));
//        System.out.println("yeast proteins number:" + simList.getRowSet().size() + "\nhuman proteins' number:" + simList.getColSet().size());
//        // save to disk
//        GraphFileWriter writer = new GraphFileWriter();
//        writer.writeToTxt(simList,"src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/yeastHumanSimList_EvalueLessThan1e-10.txt");
//    }
//
//    @DisplayName("HGA mapping.")
//    @Test
//    void HGATest() throws IOException {
//        String humanPath = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt";
//        String yeastPath = "src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt";
//        String simPath = "src\\test\\java\\resources\\TestModule\\HGATestData\\Human-Yeast\\fasta\\yeastHumanSimList_EvalueLessThan1e-10.txt";
//        yeast = readAdjList(yeastPath);// 38
//        human = readAdjList(humanPath);// 9141
//        simList = readAdjList(simPath);// 38*9141 1e-10
//        HGARun hgaRun = new HGARun(simList,yeast,human,true,0.4,0.01,5.);
//    }

    /**
     * Use after yeast and human networks have been initialized.
     * construct a simList with only nodes in yeast set and human set
     */
    SimMat getReadySimMat(String path) throws IOException {
        assert(human!=null);
        assert(yeast!=null);
        Table table = Table.read().csv(path);
        SimMat simMat = new SimMat(yeast.getAllNodes(),human.getAllNodes());
        table.stream().forEach(
                row -> {
                    String yeastNode = row.getString(0).split("\\u007C")[2].split("_YEAST")[0];
                    String humanNode = row.getString(1);
                    double evalue = 1/(1-1/Math.log(row.getDouble(2))) ;
                    if(human.getAllNodes().contains(humanNode) && yeast.getAllNodes().contains(yeastNode)){
                        simMat.put(yeastNode,humanNode,evalue);
                    }
                });
        return simMat;
    }
    public void outPutMatrix() throws FileNotFoundException {
        String debugOutputPath = "src\\test\\java\\TestModules\\humanYeastTest\\";
        AbstractFileWriter writer = new AbstractFileWriter() {
            @Override
            public void write(Vector<String> context, boolean closed) {
                super.write(context, true);
            }
        };
        Vector<String> matrixVec = new Vector<>();
        DoubleMatrix matrix = simMat.getMat();
        double[][] mat = matrix.toArray2();
        for (double[] doubles : mat) {
            for (int j = 0; j < mat[0].length; j++) {
                matrixVec.add(doubles[j] + " ");
            }
            matrixVec.add("\n");
        }
        writer.setPath(debugOutputPath + "matrix" + ".txt");
        writer.write(matrixVec, false);

    }

    void parallel() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("TIF34", "SMAD7");
        mapping.put("RVB2", "SOCS2");
        mapping.put("ARP4", "SLIT1");
        mapping.put("INO80", "UBE2D2");
        // distinguish the result simMat from the indexing one
        HashMap<String, HashSet<String>> neb1Map = yeast.getNeighborsMap();
        HashMap<String, HashSet<String>> neb2Map = human.getNeighborsMap();
        mapping.entrySet().parallelStream().forEach(entry -> {
            String node1 = entry.getKey();
            String node2 = entry.getValue();
            double simUV = simMat.getVal(node1, node2);
            // direct neighbors of the head node
            HashSet<String> neb1 = neb1Map.get(node1);
            HashSet<String> neb2 = neb2Map.get(node2);
            neb1.forEach(s1 -> {
                int nebNumbNode1 = neb1Map.get(s1).size();
                double reward = simUV / nebNumbNode1;
                neb2.forEach(s2 -> {
                    double newWeight = simMat.getVal(s1, s2) + reward;
                    simMat.put(s1, s2, newWeight);
                });
            });
        });
    }

    @Test
    void smallTest() {
        hga.addAllTopology();
    }
}
