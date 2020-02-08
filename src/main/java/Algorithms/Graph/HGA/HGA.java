package Algorithms.Graph.HGA;

import Algorithms.Graph.Hungarian;
import Algorithms.Graph.NBM;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHasSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.AdjList;
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
    protected AdjList adjList;

    /**
     * Step 1:
     * using homologous coefficients of proteins
     * computed by alignment algorithms for PINs
     *
     * @param adjList similarity matrix, headNode->graph1, listNodes -> graph2
     */
    public HGA(AdjList adjList) {
        this.adjList = adjList;
    }

    /**
     * Step 2:
     * The similarities of neighbors for each pair of matching
     * nodes (up, vq) are then rewarded with a positive number
     * ω, leading to an updated similarity matrix
     * <br>
     * <p>
     *     The matrix of the adjList will be synchronized at the same time
     * </p>
     *
     * @param simList     similarity matrix, headNode->graph1, listNodes -> graph2
     * @param revSimList  reversion of the similarity matrix, headNode->graph2, listNodes -> graph1
     * @param mappedEdges current mapping result, and one edge means the srcNode and tgtNode has already mapped, srcNode ->graph1, tgtNode -> graph2
     * @param reward w a positive number to reward the matrix
     */
    protected void updatePairNeighbors(AdjList simList, AdjList revSimList, EdgeHasSet mappedEdges, double reward) {
        NBM.neighborSimAdjust(simList,revSimList,mappedEdges,reward);
    }

    /**
     * This is the first step for HGA to initialize the mapping between two graph by HA
     *
     * @return EdgeHashSet for the mapping result
     */
    protected EdgeHasSet getEdgeMapFromHA() throws IOException {
        hungarian = new Hungarian(adjList, Hungarian.ProblemType.maxLoc);
        int[] res = hungarian.getIndexResult();
        EdgeHasSet initMap = new EdgeHasSet();
        for (int i = 0; i < res.length; i++) {
            int j = res[i];
            Pair<Node, Node> tp = adjList.getNodeNameByMatrixIndex(i, j);
            initMap.add(new Edge(tp.getFirst(), tp.getSecond(), tp.getSecond().getValue()));
        }
        return initMap;
    }


}
