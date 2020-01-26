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
    private HashSet<String> graph_1;
    private HashSet<String> graph_2;

    protected PriorityQueue<Edge> pqEdge;

    /**
     * NBM
     * @param simList AdjList to represent the similarity matrix
     */
    protected NBM(AdjList simList){
        init(simList);
        // step 1
//        findBestPairs();
    }

    /**
     * NBM
     * get HashSet from the reader.
     * @param simList AdjList to represent the similarity matrix
     * @param graph_1 HashSet from reader
     * @param graph_2 HashSet from reader
     */
    protected NBM(AdjList simList,HashSet<String> graph_1,HashSet<String> graph_2){
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

    }


}
