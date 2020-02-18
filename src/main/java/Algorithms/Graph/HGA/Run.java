package Algorithms.Graph.HGA;

import Algorithms.Graph.IO.GraphFileReader;
import Algorithms.Graph.Network.AdjList;

import java.io.IOException;
import java.util.HashSet;
import java.util.IllegalFormatException;

public class Run {
    HGA hga;
    AdjList graph1;
    AdjList graph2;
    AdjList simList;
    AdjList rev1;
    Run(String simPath,String graph_1Path,String graph_2Path) throws IOException {
        GraphFileReader reader = new GraphFileReader();
        simList = reader.readToAdjL(simPath,false);
        HashSet<String> headSim = reader.getHeadSet();
        HashSet<String> listSim = reader.getListSet();

        graph1 = reader.readToAdjL(graph_1Path,false);
        HashSet<String> set1 = reader.getHeadSet();
        set1.addAll(reader.getListSet());
        rev1 = reader.getRevAdjList();

        graph2 = reader.readToAdjL(graph_2Path,true);
        HashSet<String> set2 = reader.getHeadSet();
        set2.addAll(reader.getListSet());
        check(headSim,listSim,set1,set2);
        hga = new HGA(simList,graph1,graph2);
    }

    private void check(HashSet<String> headSim,HashSet<String> listSim,HashSet<String> set1,HashSet<String> set2) throws IllegalFormatException, IOException {
        if(headSim.equals(set1)||listSim.equals(set2)){
            throw new IOException("similarity matrix can't match with graphs");
        }
    }
}
