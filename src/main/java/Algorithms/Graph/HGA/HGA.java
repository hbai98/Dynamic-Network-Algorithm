package Algorithms.Graph.HGA;

import Algorithms.Graph.Hungarian;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHasSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.AdjList;
import org.jgrapht.alg.util.Pair;

import java.io.IOException;

/**
 *  Refer to An Adaptive Hybrid Algorithm for Global Network Alignment
 *  Article in IEEE/ACM Transactions on Computational Biology and Bioinformatics · January 2015
 *  DOI: 10.1109/TCBB.2015.2465957
 *  @author: Haotian Bai
 *  Shanghai University, department of computer science
 */

public class HGA {
    protected Hungarian hungarian;
    protected AdjList adjList;

    public HGA(AdjList adjList){
        this.adjList = adjList;
    }

    
    /**
     * This is the first step for HGA to initialize the mapping between two graph by HA
     * @return EdgeHashSet for the mapping result
     */
    protected EdgeHasSet getEdgeMapFromHA() throws IOException {
        hungarian = new Hungarian(adjList, Hungarian.ProblemType.maxLoc);
        int[] res = hungarian.getIndexResult();
        EdgeHasSet initMap = new EdgeHasSet();
        for (int i = 0; i < res.length; i++) {
            int j = res[i];
            Pair<Node,Node> tp = adjList.getNodeNameByMatrixIndex(i,j);
            initMap.add(new Edge(tp.getFirst(),tp.getSecond(),tp.getSecond().getValue()));
        }
        return initMap;
    }

}
