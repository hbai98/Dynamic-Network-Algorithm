package Algorithms.Graph;
// Author: Haotian Bai
// Shanghai University, department of computer science

import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHasSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.AdjList;
import Algorithms.Graph.Utils.HNodeList;

import java.util.*;

public class NBM {
    protected AdjList simList;
    protected HashSet<String> graph_1;
    protected HashSet<String> graph_2;
    // --------------------------------------------> alg variables
    protected HashMap<String, Node> mostSimPairMap;
    protected PriorityQueue<Edge> pqEdge;
    protected EdgeHasSet mappedEdges;


    /**
     * NBM
     *
     * @param simList     AdjList to represent the similarity matrix
     * @param mappedEdges the initial mapping result
     */
    protected NBM(AdjList simList, EdgeHasSet mappedEdges) {
        init(simList, mappedEdges);
        // step 1
//        findBestPairs();
        // step 2

    }

    /**
     * NBM
     * get HashSet from the reader.
     *
     * @param simList     AdjList to represent the similarity matrix
     * @param mappedEdges the initial mapping result
     */
    protected NBM(AdjList simList, HashSet<String> graph_1, HashSet<String> graph_2, EdgeHasSet mappedEdges) {
        init(simList, mappedEdges);
        // step 1
//        findBestPairs();
    }

    private void init(AdjList simList, EdgeHasSet mappedEdges) {
        assert (simList != null && mappedEdges != null);
        this.simList = simList;
        // pq
        pqEdge = new PriorityQueue<>(Comparator.comparingDouble(Edge::getWeight));
        // bestPairNodes -> for indexing the best pair
        mostSimPairMap = new HashMap<>();
        // mapped edges
        this.mappedEdges = mappedEdges;
    }

    /**
     * Add the initial mapping
     *
     * @param hasSet the initial alignment for nodes from two graph
     */
    private void addInitMappedEdge(EdgeHasSet hasSet) {
        assert (hasSet != null);
        this.mappedEdges = hasSet;
    }

    /**
     * Step 1:
     * iterate(nodeList) nodes in Graph1 and finds every node it's best pair( with the greatest weight )
     */
    protected void findBestPairs() {
        assert (simList != null && graph_1 != null && graph_2 != null);
        for (int index = 0; index < simList.size(); index++) {
            HNodeList list = simList.get(index);
            String listHeadName = list.getSignName();
            // save the current result
            Node tgtNode = simList.findMaxOfList(index);
            pqEdge.add(new Edge(new Node(listHeadName), tgtNode, tgtNode.getValue()));
            mostSimPairMap.put(listHeadName, tgtNode);
        }
    }

    /**
     * Step 2:
     * consider higher-value edges of unmatched nodes to align first.
     */
    protected void priMatch() {
        while(!pqEdge.isEmpty()){
            Edge edge = pqEdge.poll();
            
        }
    }


//    ---------------------------------PUBLIC-------------------------

    public HashMap<String, Node> getMostSimPairMap() {
        assert (mostSimPairMap != null);
        return mostSimPairMap;
    }
}
