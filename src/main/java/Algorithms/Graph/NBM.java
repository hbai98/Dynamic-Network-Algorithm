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

/**
 * Let n be the number of vertices and d be the
 * maximum degree of vertices. The initial computation of
 * matrix W and insertions into the priority queue take O(n2)
 * time, assuming uniform distance measures. In each iteration,
 * the algorithm removes one pair from and inserts at
 * most d2 unmatched pairs into the priority queue. Totally,
 * there are O(n) iterations. Thus, the time complexity is O(n*d^2*logn).
 */
public class NBM {
    protected AdjList simList;
    // --------------------------------------------> alg variables
    protected HashMap<String, Node> mostSimPairMap;
    protected PriorityQueue<Edge> pqEdge;
    protected EdgeHasSet mappedEdges;

    private AdjList revSimList;
    private double reward;

    /**
     * NBM
     *
     * @param simList     AdjList to represent the similarity matrix
     * @param mappedEdges the initial mapping result
     * @param reward the reward value for every turn to update the simMat
     */
    protected NBM(AdjList simList, EdgeHasSet mappedEdges,double reward) {
        init(simList, mappedEdges,reward);
        // step 1
//        findBestPairs();
        // step 2

    }

    /**
     * NBM
     * @param simList     AdjList to represent the similarity matrix
     * @param mappedEdges the initial mapping result
     * @param reward the reward value for every turn to update the simMat
     */
    protected NBM(AdjList simList,AdjList revSimList, EdgeHasSet mappedEdges,double reward) {
        init(simList,revSimList,mappedEdges,reward);
        // step 1
//        findBestPairs();
    }

    private void init(AdjList simList, AdjList revSimList, EdgeHasSet mappedEdges,double reward) {
        assert (revSimList!=null);
        this.revSimList = revSimList;
        init(simList,mappedEdges,reward);
    }

    private void init(AdjList simList, EdgeHasSet mappedEdges,double reward) {
        assert (simList != null && mappedEdges != null);
        this.reward = reward;
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
        assert (simList != null );
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
                mappedEdges.add(new Edge(srcNode,pairNodeSrc,pairNodeSrc.getValue()));
                mostSimPairMap.put(srcNode.getStrName(),tgtNode);
                continue;
            }
            neighborAdjust(srcNode,tgtNode);
        }
    }

    /**
     * Step 3:
     * the neighboring unmatched pairs of (u, v) are assigned higher weights,
     * thus increasing their chance of being chosen.
     * @param srcNode node form the query graph
     * @param tgtNode node form the subject graph
     */
    private void neighborAdjust(Node srcNode,Node tgtNode) {
        // direct neighbors of the head node
        HNodeList neb1 = simList.sortGetNeighborsList(srcNode.getStrName());
        HNodeList neb2 = revSimList.sortGetNeighborsList(tgtNode.getStrName());
        // boolean sign for node1 pair update
        boolean sign;
        for (Node node1 : neb1) {
            // after iterate all nodes in neb2, set the sign again
            sign = false;
            // Edge used to copy the new pair
            Edge edge = null;
            for (Node node2 : neb2) {
                if(!mappedEdges.findNodeEdgeSrc(node1)&&!mappedEdges.findNodeEdgeTgt(node2)){
                    double newWeight = node2.getValue()+reward;
                    double preBestWeight = mostSimPairMap.get(node1.getStrName()).getValue();
                    simList.sortAddOneNode(node1.getStrName(),node2.getStrName(),newWeight);
                    // synchronize the matrix
                    simList.updateMat(node1.getStrName(),node2.getStrName(),newWeight);
                    // when the neighbor's weight is larger than the best pair's, change the best pair record.
                    if(newWeight > preBestWeight){
                        mostSimPairMap.put(node1.getStrName(),new Node(node2.getStrName(),newWeight));
                        // new Edge used to update
                        Edge tpEdge = new Edge(node1,node2,newWeight);
                        mappedEdges.updateEdge(tpEdge);
                        // set the sign and copy it to outside
                        sign = true;
                        edge = tpEdge;
                    }
                }
            }
            // the best pair has changed, add the new pair into priority queue
            if(sign){
                pqEdge.add(edge);
            }
        }
    }



//    ---------------------------------PUBLIC-------------------------

    public HashMap<String, Node> getMostSimPairMap() {
        assert (mostSimPairMap != null);
        return mostSimPairMap;
    }

    public EdgeHasSet getMappedEdges() {
        assert (mappedEdges != null);
        return mappedEdges;
    }
}
