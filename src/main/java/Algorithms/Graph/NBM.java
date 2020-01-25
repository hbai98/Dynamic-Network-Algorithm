package Algorithms.Graph;

import Algorithms.Graph.IO.AdjListFileReader;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.NodeList;
import Algorithms.Graph.Utils.AdjList;
import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class NBM {
    protected AdjList simList;
    private NodeList graph_1;
    private NodeList graph_2;

    protected PriorityQueue<Edge> pqEdge;
    protected NBM(AdjList simList){
        init(simList);
        // step 1
//        findBestPairs();
    }
    private void init(AdjList simList){
        this.simList = simList;

    }
    /**
     * iterate nodes in Graph1 and finds every node it's best pair( with the greatest weight )
     */
    protected void findBestPairs() {
        assert(simList!=null);
        for (Node node : graph_1) {

        }
    }


}
