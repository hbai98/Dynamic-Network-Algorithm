package Algorithms.Graph;
// Author: Haotian Bai
// Shanghai University, department of computer science

import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.AdjList;
import Algorithms.Graph.Utils.HNodeList;
import org.jgrapht.alg.util.Pair;

import java.util.*;

public class NBM {
    protected AdjList simList;
    private HashSet<String> graph_1;
    private HashSet<String> graph_2;
    private HashMap<String,Node> bestPairNodes;

    protected PriorityQueue<Edge> pqEdge;

    /**
     * NBM
     * @param simList AdjList to represent the similarity matrix
     */
    protected NBM(AdjList simList){
        init(simList);
        // step 1
//        findBestPairs();
        // step 2

    }

    /**
     * NBM
     * get HashSet from the reader.
     * @param simList AdjList to represent the similarity matrix
     * @param graph_1 HashSet from reader
     * @param graph_2 HashSet from reader
     */
    protected NBM(AdjList simList,HashSet<String> graph_1,HashSet<String> graph_2){
        init(simList,graph_1,graph_2);
        // step 1
//        findBestPairs();
    }

    private void init(AdjList simList, HashSet<String> graph_1, HashSet<String> graph_2) {
        this.graph_1 = graph_1;
        this.graph_2 = graph_2;
        this.simList = simList;
        // pq
        pqEdge = new PriorityQueue<>(Comparator.comparingDouble(Edge::getWeight));
        // bestPairNodes -> for indexing the best pair
        bestPairNodes = new HashMap<>();
    }

    private void init(AdjList simList){
        init(simList,simList.getRowSet(),simList.getColSet());
    }
    /**
     * iterate(nodeList) nodes in Graph1 and finds every node it's best pair( with the greatest weight )
     */
    protected void findBestPairs() {
        assert(simList!=null&&graph_1!=null&&graph_2!=null);
        for (int index = 0; index < simList.size(); index++) {
            HNodeList list = simList.get(index);
            String listHeadName = list.getSignName();
            // save the current result
            Node tgtNode = simList.findMaxOfList(index);
            pqEdge.add(new Edge(new Node(listHeadName),tgtNode,tgtNode.getValue()));
            bestPairNodes.put(listHeadName,tgtNode);
        }
    }


//    ---------------------------------PUBLIC-------------------------

    public HashMap<String, Node> getBestPairNodes() {
        assert (bestPairNodes!=null);
        return bestPairNodes;
    }
}
