package Algorithms.Graph.HGA;

import Algorithms.Graph.Hungarian;
import Algorithms.Graph.NBM;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHasSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.AdjList;
import Algorithms.Graph.Utils.HNodeList;
import Algorithms.Graph.Utils.PairedEdges;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.io.IOException;
import java.util.HashMap;
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
    //-------------------------------
    private AdjList mappingFinalResult;

    //-----------------------store temporary paired edges
    private PairedEdges pairedEdges;

    /**
     * HGA to initialize the mapping between two graph by HA
     *
     * @return EdgeHashSet for the mapping result
     */
    protected EdgeHasSet getEdgeMapFromHA(AdjList simList) throws IOException {
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
     * divide S(t)
     * into two matrixes: the H-matrix, in which each row
     * has at least h nonzero entries, and the G-matrix, which
     * collects the remaining entries of S(t)
     *
     * @param h row has at least h nonzero entries
     */
    protected EdgeHasSet remapping(int h) throws IOException {
        // check
        AdjList H = simList.getSplit(h);
        // Hungarian alg
        EdgeHasSet mapping = getEdgeMapFromHA(H);
        // Greedy alg
        greedyMap(mapping);
        return mapping;
    }

    private void greedyMap(EdgeHasSet preMap) {
        boolean[] assignedGraph2 = new boolean[graph2.getAllNodes().size()];
        boolean[] assignedGraph1 = new boolean[graph1.getAllNodes().size()];
        HashMap<String, Integer> colMap = simList.getColMap();
        HashMap<String, Integer> rowMap = simList.getRowMap();

        // init assigned array by preMap
        preMap.forEach(edge -> {
            Node tgtNode = edge.getTarget();
            Node srcNode = edge.getSource();
            int indexCol = colMap.get(tgtNode.getStrName());
            int indexRow = rowMap.get(srcNode.getStrName());
            assignedGraph1[indexRow] = true;
            assignedGraph2[indexCol] = true;
        });

        int i = 0;
        for (HNodeList list : simList) {
            // not in mapping result, run Greedy alg
            if (!assignedGraph1[i++]) {
                Node toMatch = list.findMax(assignedGraph2);
                if(toMatch == null) {
                    break;
                }
                // add mapping result
                preMap.add(list.getSignName(), toMatch.getStrName(), toMatch.getValue());
            }
        }
    }

    /**
     * Step 1:
     * using homologous coefficients of proteins
     * computed by alignment algorithms for PINs
     * SimList is cloned
     *
     * @param graph1  adjacent list of graph1
     * @param graph2  adjacent list of graph2
     * @param simList similarity matrix, headNode->graph1, listNodes -> graph2
     */
    public HGA(AdjList simList, AdjList graph1, AdjList graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.originalSimList = (AdjList) simList.clone();
        this.simList = (AdjList) simList.clone();
    }

    /**
     * rev to make it faster
     */
    public HGA(AdjList simList, AdjList graph1, AdjList rev1, AdjList graph2, AdjList rev2) {
        this.graph1 = graph1;
        this.rev1 = rev1;
        this.graph2 = graph2;
        this.rev2 = rev2;
        this.originalSimList = (AdjList) simList.clone();
        this.simList = (AdjList) simList.clone();
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
     * @param bioFactor bioInfo's taken (0-1)
     */
    protected void addTopology(String node1, String node2,double bioFactor) throws IOException {
        HNodeList neighbors_1;
        HNodeList neighbors_2;
        assert(bioFactor>=0 && bioFactor<=1);
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
        double valToUpdate = originalSimList.getValByMatName(node1, node2)*bioFactor + eTP*(1-bioFactor);
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
     * @param factor weight of sequence information, 0 <= factor <=1
     */
    protected void addAllTopology(double factor) throws IOException {
        HashSet<String> nodes1 = graph1.getAllNodes();
        HashSet<String> nodes2 = graph2.getAllNodes();
        for (String node1 : nodes1) {
            for (String node2 : nodes2) {
                addTopology(node1, node2,factor);
            }
        }
    }

    /**
     * This step is used to score current mapping to indicate whether there's
     * a need to adjust and map again
     * <br>
     * <p>The following params for evaluate the whole network mapping</p>
     *     <ol>
     *         <li>EC : Edge Correctness (EC) is often used to measure
     * the degree of topological similarity and
     * can be estimated as the percentage of matched edges</li>
     *          <li></li>
     *     </ol>
     *
     * @return score
     */
    protected double scoreMapping(EdgeHasSet mapping) {
        // edge correctness EC
        double EC = getEC(mapping);
        // point and edge score PE
        double PE = getPE(mapping);
        return 100*EC+PE;
    }

    private double getPE(EdgeHasSet mapping) {
        // ES
        EdgeHasSet edges1 = graph1.getAllEdges();
        EdgeHasSet edges2 = graph2.getAllEdges();
        // edgeScore set to 1.0
        double ES = getES(edges1, edges2, mapping, 1.);
        double PS = getPS(mapping);
        return ES+PS;
    }

    private double getPS(EdgeHasSet mapping) {
        double PS = 0;
        if (pairedEdges == null) {
            pairedEdges = getPairedEdges(graph1.getAllEdges(), graph2.getAllEdges(), mapping).getFirst();
        }
        for (Edge edge : mapping) {
            // ui node's edges
            EdgeHasSet res = graph1.getEdgesHasNode(edge.getSource());
            boolean hasPairedEdge = false;

            for (Pair<Edge, Edge> e : pairedEdges) {
                // if one of the ui node's edges is within pairedEdges
                if (res.contains(e.getFirst())) {
                    hasPairedEdge = true;
                    break;
                }
            }
            if(hasPairedEdge){
                PS += edge.getWeight();
            }
        }
        return PS;
    }



    private double getES(EdgeHasSet edges1, EdgeHasSet edges2, EdgeHasSet mapping, double edgeScore) {
        double ES = 0;
        // check edge size to find a better algorithm
        if (edges1.size() <= edges2.size()) {
            for (Edge edge : edges1) {
                // find relevant edge in the mapping
                Edge srcEdge = mapping.findSrcEdge(edge.getSource());
                Edge tgtEdge = mapping.findSrcEdge(edge.getTarget());

                if (srcEdge != null && tgtEdge != null) {
                    // node in graph to map
                    Node src = srcEdge.getTarget();
                    Node tgt = tgtEdge.getTarget();
                    if (edges2.contains(new Edge(src, tgt))) {
                        // mapped
                        // and check positive similarity value
                        if (srcEdge.getWeight() >= 0 && tgtEdge.getWeight() >= 0) {
                            ES += edgeScore;
                        }
                    }
                }
            }
        } else {
            for (Edge edge : edges2) {
                // find relevant edge in the mapping
                Edge srcEdge = mapping.findTgtEdge(edge.getSource());
                Edge tgtEdge = mapping.findTgtEdge(edge.getTarget());
                if (srcEdge != null && tgtEdge != null) {
                    // node in graph to map
                    Node src = srcEdge.getSource();
                    Node tgt = tgtEdge.getSource();
                    if (edges1.contains(new Edge(src, tgt))) {
                        // mapped
                        // and check positive similarity value
                        if (srcEdge.getWeight() >= 0 && tgtEdge.getWeight() >= 0) {
                            ES += edgeScore;
                        }
                    }
                }
            }
        }
        return ES/2;
    }


    public double getEC(EdgeHasSet mapping) {
        EdgeHasSet edges1 = graph1.getAllEdges();
        EdgeHasSet edges2 = graph2.getAllEdges();
        Pair<PairedEdges, Integer> res = getPairedEdges(edges1, edges2, mapping);
        int count = res.getSecond();
        pairedEdges = res.getFirst();
        return (float) count / edges1.size();
    }

    private Pair<PairedEdges, Integer> getPairedEdges(EdgeHasSet edges1, EdgeHasSet edges2, EdgeHasSet mapping) {
        int count = 0;
        PairedEdges edges = new PairedEdges();
        // check edge size to find a better algorithm
        if (edges1.size() <= edges2.size()) {
            for (Edge edge1 : edges1) {
                // find relevant edge in the mapping
                Edge srcEdge = mapping.findSrcEdge(edge1.getSource());
                Edge tgtEdge = mapping.findSrcEdge(edge1.getTarget());
                if (srcEdge != null && tgtEdge != null) {
                    // node in graph to map
                    Node src = srcEdge.getTarget();
                    Node tgt = tgtEdge.getTarget();
                    Edge edge2 = new Edge(src, tgt);
                    if (edges2.contains(edge2)) {
                        // mapped
                        count++;
                        edges.add(new Pair<>(edge1, edge2));
                    }
                }
            }
        } else {
            for (Edge edge2 : edges2) {
                // find relevant edge in the mapping
                Edge srcEdge = mapping.findTgtEdge(edge2.getSource());
                Edge tgtEdge = mapping.findTgtEdge(edge2.getTarget());
                if (srcEdge != null && tgtEdge != null) {
                    // node in graph to map
                    Node src = srcEdge.getSource();
                    Node tgt = tgtEdge.getSource();
                    Edge edge1 = new Edge(src, tgt);
                    if (edges1.contains(edge1)) {
                        // mapped
                        count++;
                        edges.add(new Pair<>(edge1, edge2));
                    }
                }
            }
        }
        return new Pair<>(edges, count);
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
    protected boolean checkPassed(DoubleMatrix mat, DoubleMatrix preMat, double tolerance) throws IOException {
        double dif = mat.sub(preMat).normmax();
        return dif < tolerance;
    }

    public void run(int iterateNumber,double factor,double tolerance,int h) throws IOException {
        assert(simList!=null);
        // get the initial similarity matrix S0
        EdgeHasSet mapping = getEdgeMapFromHA(simList);
        // score the mapping
        double score = scoreMapping(mapping);
        // iterate
        int iter = 0;
        double maxScore = score;
        boolean checkPassed;
        while(iter < iterateNumber){
            iter ++;
            // update similarity matrix
            do{
                // clone Matrix, matrix is synchronized in every steps below, so it's fast
                DoubleMatrix preMat = simList.toMatrix();
                // step 2
                updatePairNeighbors(mapping);
                // step 3
                addAllTopology(factor);
                // step 4
                checkPassed = checkPassed(simList.toMatrix(),preMat,tolerance);
            }while(!checkPassed);
            // map again
            mapping = remapping(h);
            // score mapping
            score = scoreMapping(mapping);
            if(score > maxScore){
                maxScore = score;
            }
        }
    }

    public AdjList getMappingFinalResult() {
        assert (mappingFinalResult != null);
        return mappingFinalResult;
    }
}
