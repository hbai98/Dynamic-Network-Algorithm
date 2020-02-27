package Algorithms.Graph.HGA;

import Algorithms.Graph.Hungarian;
import Algorithms.Graph.NBM;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHasSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.AdjList;
import Algorithms.Graph.Utils.HNodeList;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import static Tools.MatrixFunctions.*;

import java.io.IOException;
import java.util.HashSet;

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
    protected AdjList originalSimList;
    protected AdjList simList;
    protected AdjList graph1;
    protected AdjList rev1;
    protected AdjList graph2;
    protected AdjList rev2;

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
            if (j == -1) {
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
     * @param graph1  adjacent list of graph1
     * @param graph2  adjacent list of graph2
     * @param simList similarity matrix, headNode->graph1, listNodes -> graph2
     */
    public HGA(AdjList simList, AdjList graph1, AdjList graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.originalSimList = simList;
        this.simList = simList;
    }

    /**
     * rev to make it faster
     */
    public HGA(AdjList simList, AdjList graph1, AdjList rev1, AdjList graph2, AdjList rev2) {
        this.graph1 = graph1;
        this.rev1 = rev1;
        this.graph2 = graph2;
        this.rev2 = rev2;
        this.originalSimList = simList;
        this.simList = simList;
    }

    /**
     * Step 2:
     * The similarities of neighbors for each pair of matching
     * nodes (up, vq) are then rewarded with a positive number
     * ω, leading to an updated similarity matrix
     * <br>
     * <p>w is defined as the sim(u,v)/NA(a)</p>
     * <p>where u,v  is nodes from graph1,and graph2</p>
     * <p>a is one of the neighbors of the node u</p>
     * <p>NA(a) represents the degree of the node a</p>
     * <br>
     *
     * <p>
     * NOTICE:The matrix of the adjList will be synchronized at the same time
     * </p>
     *
     * @param mappedEdges current mapping result, and one edge means the srcNode and tgtNode has already mapped, srcNode ->graph1, tgtNode -> graph2
     */
    protected void updatePairNeighbors(EdgeHasSet mappedEdges) throws IOException {
        NBM.neighborSimAdjust(graph1, graph2, simList, mappedEdges);
    }

    /**
     * Step 3.1:
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
     * <br>
     * <p>S(ij)n (one element of the matrix i row j col) in the n time's iteration :</p>
     * S(ij)t = s(ij)1 + 1/2*(θij 1 + θij 2)
     * <br>
     * <p>
     * θij 1:represents the average similarity between the neighbors of ui and vj,
     * </p>
     * <br>
     * <p>
     * θij 2:represents the average similarity between the non-neighbors of ui and vj.
     * </p>
     *
     * @param node1 one node from the graph1
     * @param node2 one node from the graph2
     */
    protected void addTopology(String node1, String node2) throws IOException {
        HNodeList neighbors_1;
        HNodeList neighbors_2;

        // init for both neighbors and nonNeighbors, if there're revs, make it faster.
        if (rev1 != null) {
            neighbors_1 = graph1.sortGetNeighborsList(node1, rev1);
        } else {
            neighbors_1 = graph1.sortGetNeighborsList(node1);
        }
        if (rev2 != null) {
            neighbors_2 = graph2.sortGetNeighborsList(node1, rev2);
        } else {
            neighbors_2 = graph2.sortGetNeighborsList(node2);
        }
        // compute topologyInfo
        double eNeighbors = getNeighborTopologyInfo(neighbors_1, neighbors_2);
        double eNonNeighbors = getNonNeighborTopologyInfo(neighbors_1, neighbors_2);
        // update both simList and mat
        double eTP = (eNeighbors + eNonNeighbors) / 2;
        double valToUpdate = originalSimList.getValByMatName(node1, node2) + eTP;
        simList.sortAddOneNode(node1, node2, valToUpdate);
        simList.updateMat(node1, node2, valToUpdate);
    }


    private double getNonNeighborTopologyInfo(HNodeList nei1, HNodeList nei2) {
        double res = 0;
        HashSet<String> nodes1 = graph1.getAllNodes();
        HashSet<String> nodes2 = graph2.getAllNodes();

        int nonNei1Size = nodes1.size() - nei1.size() - 1;
        int nonNei2Size = nodes2.size() - nei2.size() - 1;

        if (nonNei1Size != 0 && nonNei2Size != 0) {
            int size = (nonNei1Size + 1) * (nonNei2Size + 1);
            for (String node1 : nodes1) {
                for (String node2 : nodes2) {
                    if (!nei1.sortFind(node1) && !nei2.sortFind(node2)) {
                        res += simList.getValByMatName(node1, node2);
                    }
                }
            }
            return res / size;
        }

        if (nonNei1Size == 0 && nonNei2Size == 0) {
            int size = nodes1.size() * nodes2.size();
            for (String node1 : nodes1) {
                for (String node2 : nodes2) {
                    res += simList.getValByMatName(node1, node2);
                }
            }
            return res / size;
        }
        return res;
    }

    private double getNeighborTopologyInfo(HNodeList nei1, HNodeList nei2) {
        double res = 0;
        int nei1Size = nei1.size();
        int nei2Size = nei2.size();
        if (nei1Size != 0 && nei2Size != 0) {
            int size = nei1Size * nei2Size;
            for (Node node1 : nei1) {
                for (Node node2 : nei2) {
                    res += simList.getValByMatName(node1.getStrName(), node2.getStrName());
                }
            }
            return res / size;
        }
        if (nei1Size == 0 && nei2Size == 0) {
            HashSet<String> nodes1 = graph1.getAllNodes();
            HashSet<String> nodes2 = graph2.getAllNodes();
            int size = nodes1.size() * nodes2.size();
            for (String node1 : nodes1) {
                for (String node2 : nodes2) {
                    res += simList.getValByMatName(node1, node2);
                }
            }
            return res / size;
        }
        return res;
    }

    /**
     * Step 3 - integrated all steps in process 3(Topology info):
     * iterate all nodes pairs to add topological information
     */
    protected void addAllTopology() throws IOException {
        HashSet<String> nodes1 = graph1.getAllNodes();
        HashSet<String> nodes2 = graph2.getAllNodes();
        for (String node1 : nodes1) {
            for (String node2 : nodes2) {
                addTopology(node1, node2);
            }
        }
    }

    /**
     * Step 4: - check if the condition is passed
     * Continue to step 2 until one of the following conditions
     * is satisfied:
     * | Si - Si-1 | < r
     * | Si - Si-2 | < r
     * || -> determinant of matrix
     * ------------------------------------------
     * r = 0.01 to allow 1% error
     */
    protected boolean checkPassed(DoubleMatrix mat,DoubleMatrix preMat,double tolerance) throws IOException {
           double dif = mat.sub(preMat).normmax();
           if(dif < tolerance){
               return true;
           }
           else{
               return false;
           }
    }
}
