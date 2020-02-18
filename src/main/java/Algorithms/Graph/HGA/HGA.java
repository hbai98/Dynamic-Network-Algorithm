package Algorithms.Graph.HGA;

import Algorithms.Graph.Hungarian;
import Algorithms.Graph.NBM;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHasSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.AdjList;
import Algorithms.Graph.Utils.HNodeList;
import org.jgrapht.alg.util.Pair;

import java.io.IOException;

/**
 * Refer to An Adaptive Hybrid Algorithm for Global Network Algorithms.Alignment
 * Article in IEEE/ACM Transactions on Computational Biology and Bioinformatics · January 2015
 * DOI: 10.1109/TCBB.2015.2465957
 *
 * @author: Haotian Bai
 * Shanghai University, department of computer science
 */

public class HGA {
    protected Hungarian hungarian;
    protected AdjList simList;
    protected AdjList graph1;
    protected AdjList graph2;

    /**
     * This is the first step for HGA to initialize the mapping between two graph by HA
     *
     * @return EdgeHashSet for the mapping result
     */
    protected EdgeHasSet getEdgeMapFromHA() throws IOException {
        hungarian = new Hungarian(simList, Hungarian.ProblemType.maxLoc);
        int[] res = hungarian.getResult();
        EdgeHasSet initMap = new EdgeHasSet();
        for (int i = 0; i < res.length; i++) {
            int j = res[i];
            if(j == -1){
                continue;
            }
            Pair<Node, Node> tp = simList.getNodeNameByMatrixIndex(i, j);
            initMap.add(new Edge(tp.getFirst(), tp.getSecond(), tp.getSecond().getValue()));
        }
        return initMap;
    }
    /**
     * Step 1:
     * using homologous coefficients of proteins
     * computed by alignment algorithms for PINs
     *
     * @param graph1 adjacent list of graph1
     * @param graph2 adjacent list of graph2
     * @param simList similarity matrix, headNode->graph1, listNodes -> graph2
     */
    public HGA(AdjList simList, AdjList graph1, AdjList graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.simList = simList;

    }

    /**
     * Step 2:
     * The similarities of neighbors for each pair of matching
     * nodes (up, vq) are then rewarded with a positive number
     * ω, leading to an updated similarity matrix
     * <br>
     *     <p>w is defined as the sim(u,v)/NA(a)</p>
     *     <p>where u,v  is nodes from graph1,and graph2</p>
     *     <p>a is one of the neighbors of the node u</p>
     *     <p>NA(a) represents the degree of the node a</p>
     * <br>
     *
     * <p>
     *     NOTICE:The matrix of the adjList will not be synchronized at the same time
     * </p>
     *
     * @param mappedEdges current mapping result, and one edge means the srcNode and tgtNode has already mapped, srcNode ->graph1, tgtNode -> graph2
     */
    protected void updatePairNeighbors(EdgeHasSet mappedEdges) throws IOException {
        NBM.neighborSimAdjust(graph1,graph2,simList,mappedEdges);
    }

    /**
     * Step 3:
     * Adding topology information:
     * Given any two nodes ui, vj in the networks A and B,
     * respectively, their topological similarities are computed
     * based on an approach previously used for the topological
     * similarity of biomolecular networks.which we have
     * called the topological similarity parameter (TSP). The
     * TSP includes θij 1 and θij 2 , which are updated according
     * to the rule that two nodes are similar if they link or do
     * not link to similar nodes
     * <br>
     *     <br>
     * <p>
     *     θij 1:represents the average similarity between the neighbors of ui and vj,
     * </p>
     * <br>
     * <p>
     *     θij 2:represents the average similarity between the non-neighbors of ui and vj.
     * </p>
     *
     * @param node1 one node from the graph1
     * @param node2 one node from the graph2
     */
    protected void addTopologyInfo(String node1,String node2){
        HNodeList neighbor1 = graph1.getNeighborsList(node1);
        HNodeList neighbor2 = graph2.getNeighborsList(node2);
    }



}
