package Algorithms.Graph;

import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.NodeList;
import org.jblas.DoubleMatrix;

import java.util.PriorityQueue;

public class NBM {
    protected NodeList graph_1;
    protected NodeList graph_2;
    protected DoubleMatrix simMatrix;
    protected PriorityQueue<Edge> pqEdge;
    protected NBM(){
        // step 1
//        findBestPairs();
    }

    /**
     * iterate nodes in Graph1 and finds every node it's best pair( with the greatest weight )
     */
    protected void findBestPairs() {
        for (Node node :
                graph_1) {

        }
    }


}
