package Algorithms.Graph;
/**
 * H. He and A. K.Singh, “Closure-Tree: An index structure for graph
 * queries,” in ICDE’06: Proceedings of the 22nd International Conference
 * on Data Engineering. Washington, DC, USA: IEEE Computer Society,
 * 2006, p. 38, doi: http://dx.doi.org/10.1109/ICDE.2006.37
 *
 * NBM
 * @author Haotian Bai
 * Shanghai University, department of computer science
 */


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
    private AdjList revSimList;

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
    protected NBM(AdjList simList,AdjList revSimList, EdgeHasSet mappedEdges) {
        init(simList,revSimList,mappedEdges);
        // step 1
//        findBestPairs();
    }

    private void init(AdjList simList, AdjList revSimList, EdgeHasSet mappedEdges) {
        assert (revSimList!=null);
        this.revSimList = revSimList;
        init(simList,mappedEdges);
    }

    private void init(AdjList simList, EdgeHasSet mappedEdges) {
        assert (simList != null && mappedEdges != null);
        if(this.revSimList == null){
            this.revSimList = simList.getRevList();
        }
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
     * O(mn+mn*log(mn))
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
     * consider higher-value edges of unmatched nodes to align first with it's best pair.
     */
    protected void priMatch() {

        while(!pqEdge.isEmpty()){
            Edge edge = pqEdge.poll();
            Node srcNode = edge.getSource();
            Node tgtNode = edge.getTarget();
            // u is matched
            if(mappedEdges.findNodeEdgeSrc(srcNode)){
                continue;
            }
            // v is matched
            if(mappedEdges.findNodeEdgeTgt(tgtNode)){
                // find the best pair for unmatched u
                Node pairNodeSrc = mostSimPairMap.get(srcNode.getStrName());
                // if this tgt is not matched go on
                if(mappedEdges.findNodeEdgeTgt(pairNodeSrc)){
                    continue;
                }
                // add new pair
                pqEdge.add(new Edge(srcNode,pairNodeSrc,pairNodeSrc.getValue()));
                mostSimPairMap.put(srcNode.getStrName(),tgtNode);
                continue;
            }
            // direct neighbors of the head node
            HNodeList neb1 = simList.sortGetNeighborsList(srcNode.getStrName());
        }
    }


//    ---------------------------------PUBLIC-------------------------

    public HashMap<String, Node> getMostSimPairMap() {
        assert (mostSimPairMap != null);
        return mostSimPairMap;
    }
}
