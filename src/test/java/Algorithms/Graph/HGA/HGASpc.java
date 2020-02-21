package Algorithms.Graph.HGA;

import Algorithms.Graph.IO.GraphFileReader;
import Algorithms.Graph.NBM;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHasSet;
import Algorithms.Graph.Network.AdjList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("The HGA is able to ")
class HGASpc {
    HGA hga;
    AdjList graph1;
    AdjList graph2;
    AdjList simList;
    AdjList rev1;
    @BeforeEach
    void init() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        simList = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/simMat.txt",false);
        graph1 = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/graph1.txt",false);
        rev1 = reader.getRevAdjList();
        graph2 = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/graph2.txt",true);
        hga = new HGA(simList,graph1,graph2);
    }
    @DisplayName("init mapping.")
    @Test
    void initMap() throws IOException {
        EdgeHasSet initMap = hga.getEdgeMapFromHA();
        EdgeHasSet res = new EdgeHasSet();
        res.add(new Edge("A","C",0.8));
        res.add(new Edge("B","B",0.8));
        res.add(new Edge("C","I",0.7));
        res.add(new Edge("D","A",0.7));
        res.add(new Edge("F","D",0.9));
        res.add(new Edge("G","Q",0.2));
        res.add(new Edge("I","M",0.6));
        res.add(new Edge("M","W",0.9));
        res.add(new Edge("Q","F",0.9));
        assertEquals(res,initMap);
    }
    @DisplayName("update matrix.")
    @Test
    void updateMap() throws IOException {
        NBM.neighborSimAdjust(graph1,rev1,graph2,simList,hga.getEdgeMapFromHA());
        assertEquals(1.4,simList.getValByName("W","A"));
        assertEquals(1.4,simList.getValByMatName("W","A"));
    }
    @DisplayName("add topological effect.")
    @Test
    void addTopologicalEffect() throws IOException {
        hga.addAllTopology();
    }


}
