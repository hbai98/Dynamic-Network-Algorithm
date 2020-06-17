package Algorithms.Graph.HGA;

import Algorithms.Graph.Utils.AdjList.Graph;
import Algorithms.Graph.Utils.AdjList.SimList;
import Algorithms.Graph.Utils.SimMat;
import IO.GraphFileReader;
import Tools.Stopwatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HGARunSpec {
    HGA hga;
    @BeforeEach
    void init() throws IOException {
        GraphFileReader reader = new GraphFileReader(true,true,false);
        Graph graph1 = reader.readToGraph("src/test/java/resources/AlgTest/HGA/graph1.txt",false);
        Graph graph2 = reader.readToGraph("src/test/java/resources/AlgTest/HGA/graph2.txt",false);
        reader.setRecordNonZeros(true);
        reader.setRecordNeighbors(false);
        SimMat simMat = reader.readToSimMat("src/test/java/resources/AlgTest/HGA/simMat.txt",graph1.getAllNodes(),graph2.getAllNodes(),true);
        hga = new HGA(simMat,graph1,graph2);
    }
    @DisplayName("Greedily map")
    @Test
    void greedMap(){
        HashMap<String,String> preMap = new HashMap<>();
        preMap.put("D","F");
        preMap.put("F","I");
        HGA.greedyMap(hga.simMat,preMap);
    }
    @DisplayName("Edge correctness")
    @Test
    void getEC(){
        HashMap<String,String> mapping = new HashMap<>();
        mapping.put("W","A");
        mapping.put("Q","W");
        mapping.put("I","H");
        mapping.put("G","M");
        assertEquals((double)2/15,hga.getEC(mapping));
    }
//    @Test
//    void Test() throws Exception {
//
//        GraphFileReader reader = new GraphFileReader();
//        String graph_2Path = "src/main/java/resources/Test/sars_S.txt";
//        SimList graph2 = reader.readToSimList(graph_2Path,false);
//        if(graph2.size()==0){
//            throw new IOException("graph2 has not been loaded.");
//        }
//        SimList rev2 = reader.getRevAdjList();
//        String graph_1Path = "src/main/java/resources/Test/cov_S.txt";
//        SimList graph1 = reader.readToSimList(graph_1Path,false);
//        if(graph1.size()==0){
//            throw new IOException("graph1 has not been loaded.");
//        }
//        SimList rev1 = reader.getRevAdjList();
////        AdjList simList = new Smith_Waterman(graph1.getAllNodes(),graph2.getAllNodes()).run("src/main/java/resources/Test/cov_S.fa", "src/main/java/resources/Test/sars_S.fa");
////        HGARun run = new HGARun(simList,graph1,rev1,graph2,rev2);
////        run.getHga().getMappingFinalResult().forEach(edge ->
////                System.out.println("Edge:" + edge.getSource().getStrName() + " to " + edge.getTarget().getStrName() + " is matched.")
////        );
//    }
//
//    @Test
//    void Test_1() throws Exception {
//        Stopwatch w1 = new Stopwatch();
//        GraphFileReader reader = new GraphFileReader();
//        HGARun run = new HGARun("src/test/java/resources/AlgTest/HGA/simMat.txt",
//                "src/test/java/resources/AlgTest/HGA/graph1.txt",
//                "src/test/java/resources/AlgTest/HGA/graph2.txt",true,0.5,0.01,5.);
//        System.out.println("HGA run time:"+w1.elapsedTime()+" second(s).");
//
//        List<String> s = new ArrayList<>();
//        List<String> t = new ArrayList<>();
//        run.getHga().getMapping().forEach(edge -> {
//            s.add(edge.getSource().getStrName());
//            t.add(edge.getTarget().getStrName());
//                }
//        );
//        Table result = Table.create("HGA Match").addColumns(
//                StringColumn.create("source Node",s),
//                StringColumn.create("Target Node",t)
//                );
//        result.write().csv("src/test/java/resources/AlgTest/HGA/result.csv");
//    }

}
