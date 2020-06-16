package Algorithms.Graph;



import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHashSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.AdjList.Graph;
import Algorithms.Graph.Utils.AdjList.SimList;
import Algorithms.Graph.Utils.List.HNodeList;
import Algorithms.Graph.Utils.SimMat;

import java.io.IOException;
import java.util.*;
/**
 * H. He and A. K.Singh, “Closure-Tree: An index structure for graph
 * queries,” in ICDE’06: Proceedings of the 22nd International Conference
 * on Data Engineering. Washington, DC, USA: IEEE Computer Society,
 * 2006, p. 38, doi: http://dx.doi.org/10.1109/ICDE.2006.37
 *
 * NBM
 * @author Haotian Bai
 * Shanghai University, department of computer science
 * Time complexity:
 * <p>Let n be the number of vertices and d be the
 * maximum degree of vertices. The initial computation of
 * matrix W and insertions into the priority queue take O(n2)
 * time, assuming uniform distance measures. In each iteration,
 * the algorithm removes one pair from and inserts at
 * most d2 unmatched pairs into the priority queue. Totally,
 * there are O(n) iterations. Thus, the time complexity is O(n*d^2*logn).
 * </p>
 * <br>
 * <p>---------------------------------------------------------------------------</p>
 * <br>
 * <p>In order to find common substructures, we develop a new
 * graph mapping method called Neighbor Biased Mapping
 * (NBM) shown in Alg. 1. Initially, a weight matrix W is
 * computed where each entry Wu,v represents the similarity
 * of vertex u ∈ G1 and vertex v ∈ G2. A priority queue PQ
 * maintains pairs of vertices according to their weights. For
 * each vertex in G1, its most similar vertex is found in G2,
 * and the pair is added to PQ. At each iteration, the best pair
 * (u, v) of unmatched vertices in the priority queue is chosen
 * and marked as matched. Then, the neighboring unmatched
 * pairs of (u, v) are assigned higher weights, thus increasing
 * their chance of being chosen. The iterations continue until
 * all vertices in graph G1 have been matched.
 * </p>
 */
public class NBM {
    protected SimMat simList;
    // --------------------------------------------> alg variables
    protected HashMap<String, Node> mostSimPairMap;
    protected PriorityQueue<Edge> pqEdge;
    protected EdgeHashSet mappedEdges;
    protected Graph graph1;
    protected Graph graph2;

    private double reward;
    protected  NBM(){

    }
    /**
     * NBM
     *
     * @param graph1  the adjList to represent the graph1
     * @param graph2  the adjList to represent the graph2
     * @param simList     AdjList to represent the similarity matrix
     * @param mappedEdges the initial mapping result
     * @param reward the reward value for every turn to update the simMat
     */
    protected NBM(SimList graph1, SimList graph2, SimList simList, EdgeHashSet mappedEdges, double reward) throws IOException {
        init(graph1,graph2,simList, mappedEdges,reward);
        // step 1
        findBestPairs();
        // step 2,3
        priMatch();

    }

    private void init(SimList graph1, SimList graph2, SimList simList, EdgeHashSet mappedEdges, double reward) {
        assert (graph1!=null && graph2!=null && simList != null && mappedEdges != null);
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.reward = reward;
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
    private void addInitMappedEdge(EdgeHashSet hasSet) {
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
            Node tgtNode = simList.sortFindMaxOfList(index);
            pqEdge.add(new Edge(new Node(listHeadName), tgtNode, tgtNode.getValue()));
            mostSimPairMap.put(listHeadName, tgtNode);
        }
    }

    /**
     * Step 2:
     * consider higher-value edges of unmatched nodes to align first with it's best pair.
     */
    protected void priMatch() throws IOException {
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
        HNodeList neb1 = graph1.sortGetNeighborsList(srcNode.getStrName());
        HNodeList neb2 = graph2.sortGetNeighborsList(tgtNode.getStrName());
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

    /**
     * Update only once for all neighbors of all the pairs ready.
     * reward is defined in HGA.
     */
    public static void  neighborSimAdjust(SimList graph1, SimList graph2, SimList simSimList, EdgeHashSet mappedEdges) {
        for (Edge edge : mappedEdges) {
            Node srcNode = edge.getSource();
            Node tgtNode = edge.getTarget();
            double simUV = simSimList.getValByMatName(srcNode.getStrName(),tgtNode.getStrName());
            // direct neighbors of the head node
            HNodeList neb1 = graph1.sortGetNeighborsList(srcNode.getStrName());
            HNodeList neb2 = graph2.sortGetNeighborsList(tgtNode.getStrName());
            for (Node node1 : neb1) {
                int nebNumb = graph1.sortGetNeighborsList(node1.getStrName()).size();
                double reward = simUV/nebNumb;
                for (Node node2 : neb2) {
                    double newWeight = simSimList.getValByMatName(node1.getStrName(),node2.getStrName())+reward;
                    simSimList.sortAddOneNode(node1.getStrName(),node2.getStrName(),newWeight);
                    simSimList.updateMat(node1.getStrName(),node2.getStrName(),newWeight);
                }
            }
        }

    }

    public static void neighborSimAdjust(SimList graph1, SimList rev1, SimList graph2, SimList simSimList, EdgeHashSet mappedEdges) {
        for (Edge edge : mappedEdges) {
            Node srcNode = edge.getSource();
            Node tgtNode = edge.getTarget();
            // direct neighbors of the head node
            double simUV = simSimList.getValByMatName(srcNode.getStrName(),tgtNode.getStrName());
            HNodeList neb1 = graph1.sortGetNeighborsList(srcNode.getStrName());
            HNodeList neb2 = graph2.sortGetNeighborsList(tgtNode.getStrName());
            for (Node node1 : neb1) {
                int nebNumb = graph1.sortGetNeighborsList(node1.getStrName(),rev1).size();
                double reward = simUV/nebNumb;
                for (Node node2 : neb2) {
                    double newWeight = simSimList.getValByMatName(node1.getStrName(),node2.getStrName())+reward;
                    simSimList.sortAddOneNode(node1.getStrName(),node2.getStrName(),newWeight);
                    simSimList.updateMat(node1.getStrName(),node2.getStrName(),newWeight);
                }
            }
        }
    }
//    ---------------------------------PUBLIC-------------------------

    public HashMap<String, Node> getMostSimPairMap() {
        assert (mostSimPairMap != null);
        return mostSimPairMap;
    }

    public EdgeHashSet getMappedEdges() {
        assert (mappedEdges != null);
        return mappedEdges;
    }
}
