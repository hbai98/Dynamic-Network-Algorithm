package Algorithms.Graph.HGA;

import Algorithms.Alignment.align.Smith_Waterman;
import Algorithms.Graph.Network.AdjList;
import IO.AbstractFileWriter;
import IO.CsvToAdjList;
import IO.GraphFileReader;
import IO.GraphFileWriter;
import Tools.Stopwatch;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class HGARunSpec {
    @Test
    void Test() throws Exception {

        GraphFileReader reader = new GraphFileReader();
        String graph_2Path = "src/main/java/resources/Test/sars_S.txt";
        AdjList graph2 = reader.readToAdjL(graph_2Path,false);
        if(graph2.size()==0){
            throw new IOException("graph2 has not been loaded.");
        }
        AdjList rev2 = reader.getRevAdjList();
        String graph_1Path = "src/main/java/resources/Test/cov_S.txt";
        AdjList graph1 = reader.readToAdjL(graph_1Path,false);
        if(graph1.size()==0){
            throw new IOException("graph1 has not been loaded.");
        }
        AdjList rev1 = reader.getRevAdjList();
        AdjList simList = new Smith_Waterman(graph1.getAllNodes(),graph2.getAllNodes()).run("src/main/java/resources/Test/cov_S.fa", "src/main/java/resources/Test/sars_S.fa");
        HGARun run = new HGARun(simList,graph1,rev1,graph2,rev2);
        run.getHga().getMappingFinalResult().forEach(edge ->
                System.out.println("Edge:" + edge.getSource().getStrName() + " to " + edge.getTarget().getStrName() + " is matched.")
        );
    }

    @Test
    void Test_1() throws Exception {
        CsvToAdjList c1 = new CsvToAdjList("C:\\Users\\Haotian Bai\\Desktop\\cov19\\A1.csv", "node1", "Node2");
        CsvToAdjList c2 = new CsvToAdjList("C:\\Users\\Haotian Bai\\Desktop\\cov19\\B1.csv", "node1", "Node2");
        Stopwatch w1 = new Stopwatch();

        AdjList simList = new Smith_Waterman(c1.getTarget().getAllNodes(),
                c2.getTarget().getAllNodes()).run("C:\\Users\\Haotian Bai\\Desktop\\cov19\\A1.fa", "C:\\Users\\Haotian Bai\\Desktop\\cov19\\B1.fa");
        GraphFileWriter writer = new GraphFileWriter();
        writer.writeToTxt(simList,"C:\\Users\\Haotian Bai\\Desktop\\cov19\\sm_res.txt");
        System.out.println(simList.getRowSet().size()+"* "+simList.getColSet().size()+"\nSM run time:"
                +w1.elapsedTime()+" second(s).\n");
        w1.start();
//        GraphFileReader reader = new GraphFileReader();
//        AdjList simList = reader.readToAdjL("C:\\Users\\Haotian Bai\\Desktop\\cov19\\sm_res.txt");
        HGARun run = new HGARun(simList, c1.getTarget(), c2.getTarget());
        System.out.println("HGA run time:"+w1.elapsedTime()+" second(s).");
        List<String> s = new ArrayList<>();
        List<String> t = new ArrayList<>();
        run.getHga().getMappingFinalResult().forEach(edge -> {
            s.add(edge.getSource().getStrName());
            t.add(edge.getTarget().getStrName());
                }
        );
        Table result = Table.create("HGA Match").addColumns(
                StringColumn.create("source Node",s),
                StringColumn.create("Target Node",t)
                );
        result.write().csv("C:\\Users\\Haotian Bai\\Desktop\\cov19\\result.cvs");
    }

}
