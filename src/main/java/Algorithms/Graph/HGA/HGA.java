package Algorithms.Graph.HGA;

/**
 * @Author: Haotian Bai
 * Shanghai University, department of computer science
 */

import Algorithms.Graph.Hungarian;
import Algorithms.Graph.NBM;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHashSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.AdjList;
import Algorithms.Graph.Utils.HNodeList;
import Algorithms.Graph.Utils.PairedEdges;
import IO.GraphFileReader;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;

import java.io.IOException;
import java.util.*;

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
    //---------------mapping result-------------
    private EdgeHashSet mappingFinalResult;
    private double PE;
    private double ES;
    private double PS;
    private double EC;
    private double score;
    //-----------------------store temporary paired edges
    private PairedEdges pairedEdges;

    /**
     * HGA to initialize the mapping between two graph by HA
     *
     * @return EdgeHashSet for the mapping result
     */
    protected EdgeHashSet getEdgeMapFromHA(AdjList simList) throws IOException {
        hungarian = new Hungarian(simList, Hungarian.ProblemType.maxLoc);
        int[] res = hungarian.getResult();
        EdgeHashSet initMap = new EdgeHashSet();
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
     * @param toMap matrix for hga mapping
     * @param h row has at least h nonzero entries
     */
    protected EdgeHashSet remapping(AdjList toMap,int h) throws IOException {
        assert(toMap!=null);
        // check
        AdjList H = toMap.getSplit(h);
        // Hungarian alg
        EdgeHashSet mapping = getEdgeMapFromHA(H);
        // Greedy alg
        greedyMap(toMap,mapping);
        return mapping;
    }

    private void greedyMap(AdjList toMap,EdgeHashSet preMap) {
        HashMap<String, Integer> colMap = toMap.getColMap();
        HashMap<String, Integer> rowMap = toMap.getRowMap();
        boolean[] assignedGraph2 = new boolean[toMap.getColSet().size()];
        boolean[] assignedGraph1 = new boolean[toMap.getRowSet().size()];

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
        for (HNodeList list : toMap) {
            // not in mapping result, run Greedy alg
            if (!assignedGraph1[i++]) {
                Node toMatch = list.findMax(assignedGraph2);
                if (toMatch == null) {
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
    protected void updatePairNeighbors(EdgeHashSet mappedEdges) throws IOException {
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
     * @param node1     one node from the graph1
     * @param node2     one node from the graph2
     * @param bioFactor bioInfo's taken (0-1)
     */
    protected void addTopology(String node1, String node2, double bioFactor) throws IOException {
        HNodeList neighbors_1;
        HNodeList neighbors_2;
        assert (bioFactor >= 0 && bioFactor <= 1);
        // init for both neighbors and nonNeighbors, if there're revs, make it faster.
        if (rev1 != null) {
            neighbors_1 = graph1.sortGetNeighborsList(node1, rev1);
        } else {
            neighbors_1 = graph1.sortGetNeighborsList(node1);
        }
        if (rev2 != null) {
            neighbors_2 = graph2.sortGetNeighborsList(node2, rev2);
        } else {
            neighbors_2 = graph2.sortGetNeighborsList(node2);
        }
        // compute topologyInfo
        double eNeighbors = getNeighborTopologyInfo(neighbors_1, neighbors_2);
        double eNonNeighbors = getNonNeighborTopologyInfo(neighbors_1, neighbors_2);
        // update both simList and mat
        double eTP = (eNeighbors + eNonNeighbors) / 2;
//        double valToUpdate = originalSimList.getValByMatName(node1, node2) * bioFactor + eTP * (1 - bioFactor);
        double valToUpdate = simList.getValByMatName(node1, node2) * bioFactor + eTP * (1 - bioFactor);
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
     *
     * @param factor weight of sequence information, 0 <= factor <=1
     */
    protected void addAllTopology(double factor) throws IOException {
        HashSet<String> nodes1 = simList.getRowSet();
        HashSet<String> nodes2 = simList.getColSet();
        for (String node1 : nodes1) {
            for (String node2 : nodes2) {
                addTopology(node1, node2, factor);
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
     *
     *          <li>
     *              PE: Point and Edge Score(PE) is clearly stricter than EC because it reflects the status
     *              of both the node and edge matches in the mapping.
     *          </li>
     *
     *          <li>
     *               The score for an edge (the Edge Score, ES) equals zero if any of its nodes does not match
     *               with its similar nodes, and the score for a node (the Point Score, PS) equals zero if none of its edges has a score.
     *          </li>
     *     </ol>
     *
     * @return score, PE, EC, ES, PS
     */
    protected ArrayList<Double> scoreMapping(EdgeHashSet mapping) {
        // edge correctness EC
        double EC = getEC(mapping);
        // point and edge score PE
        ArrayList<Double> res = getES_PS(mapping);
        double ES = res.get(0);
        double PS = res.get(1);
        double PE = ES + PS;
        double score = 100 * EC + PE;
        // result
        return new ArrayList<>(Arrays.asList(score, PE, EC, ES, PS));
    }

    private ArrayList<Double> getES_PS(EdgeHashSet mapping) {
        // ES
        EdgeHashSet edges1 = graph1.getAllEdges();
        EdgeHashSet edges2 = graph2.getAllEdges();
        // edgeScore set to 1.0
        double ES = getES(edges1, edges2, mapping, 1.);
        double PS = getPS(mapping);
        return new ArrayList<>(Arrays.asList(ES, PS));
    }

    private double getPS(EdgeHashSet mapping) {
        double PS = 0;
        if (pairedEdges == null) {
            pairedEdges = getPairedEdges(graph1.getAllEdges(), graph2.getAllEdges(), mapping).getFirst();
        }
        for (Edge edge : mapping) {
            // ui node's edges
            EdgeHashSet res = graph1.getEdgesHasNode(edge.getSource());
            boolean hasPairedEdge = false;

            for (Pair<Edge, Edge> e : pairedEdges) {
                // if one of the ui node's edges is within pairedEdges
                if (res.contains(e.getFirst())) {
                    hasPairedEdge = true;
                    break;
                }
            }
            if (hasPairedEdge) {
                PS += edge.getWeight();
            }
        }
        return PS;
    }


    private double getES(EdgeHashSet edges1, EdgeHashSet edges2, EdgeHashSet mapping, double edgeScore) {
        double ES = 0;
        // check edge size to find a better algorithm
        if (edges1.size() <= edges2.size()) {
            for (Edge edge : edges1) {
                // find relevant edge in the mapping
                // edge equals graph1Node -> graph2Node
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
        return ES / 2;
    }


    public double getEC(EdgeHashSet mapping) {
        EdgeHashSet edges1 = graph1.getAllEdges();
        EdgeHashSet edges2 = graph2.getAllEdges();
        Pair<PairedEdges, Integer> res = getPairedEdges(edges1, edges2, mapping);
        int count = res.getSecond();
        pairedEdges = res.getFirst();
        return (float) count / edges1.size();
    }

    private Pair<PairedEdges, Integer> getPairedEdges(EdgeHashSet edges1, EdgeHashSet edges2, EdgeHashSet mapping) {
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
     * A sum score does not change in three continuous iterations.
     * || -> determinant of matrix
     * ------------------------------------------
     * r = 0.01 to allow 1% error
     */
    protected boolean checkPassed(Stack<DoubleMatrix> stackMat, double tolerance) {
        if(stackMat.size() == 3){
            DoubleMatrix s1 = stackMat.get(1);
            DoubleMatrix s = stackMat.peek();
            // remove button which is the oldest +1 every iteration
            DoubleMatrix s2 = stackMat.remove(0);
            double dif_1 = s.sub(s1).normmax();
            double dif_2 = s.sub(s2).normmax();
            return dif_1 < tolerance || dif_2 < tolerance;
        }
        // size = 2
        else{
            DoubleMatrix s = stackMat.peek();
            double dif = s.sub(stackMat.get(0)).normmax();
            return dif < tolerance;
        }
    }

    /**
     *
     * @param factor  weight of sequence information, 0 <= factor <=1
     * @param tolerance error tolerance compared with the last matrix
     * @param h row has at least h nonzero entries
     * @throws IOException
     */
    public void run(double factor, double tolerance, int h, boolean forcedMappingForSame) throws IOException {
        assert (simList != null);
        EdgeHashSet mapping;
        EdgeHashSet forcedPart = null;
        AdjList restForForced = null;
        // forced mapping
        if(forcedMappingForSame){
            Triple<EdgeHashSet, EdgeHashSet, AdjList> res = forcedMap(simList);
            // hungarian for the res
            mapping = res.getFirst();
            // forced
            forcedPart = res.getSecond();
            // rest
            restForForced = res.getThird();
        }
        else{
            // get the initial similarity matrix S0
            mapping = getEdgeMapFromHA(simList);
        }
        // final mapping result
        EdgeHashSet tmp = (EdgeHashSet) mapping.clone();
        tmp.addAll(forcedPart);
        EdgeHashSet finalMapping = tmp;
        score = getScoreInfo(forcedMappingForSame, mapping, forcedPart).get(0);

        // iterate
        double maxScore = score;
        boolean checkPassed;

        // deque for simMat converge
        Stack<DoubleMatrix> stackMat = new Stack<>();
        // clone Matrix, matrix is synchronized in every steps below, so it's fast
        DoubleMatrix preMat = simList.getMatrix();
        // add to top
        stackMat.push(preMat);
        // update similarity matrix
        do {
            // step 2
            updatePairNeighbors(mapping);
            // step 3 (heavy)
            addAllTopology(factor);
            // add to top
            stackMat.push(simList.getMatrix());
            // map again
            EdgeHashSet nextMapping = remap(forcedMappingForSame,forcedPart,h);
            // score mapping
            ArrayList<Double> scoreInfo = getScoreInfo(forcedMappingForSame, nextMapping, forcedPart);
            score = scoreInfo.get(0);
            // same mapping, the later will get higher score as simList val is larger
            if (score > maxScore) {
                // get best result recorded
                maxScore = score;
                PE = scoreInfo.get(1);
                EC = scoreInfo.get(2);
                ES = scoreInfo.get(3);
                PS = scoreInfo.get(4);
                finalMapping = (EdgeHashSet) nextMapping.clone();
            }
            // step 4
            checkPassed = checkPassed(stackMat, tolerance);
        } while (!checkPassed);
        // record mapping result
        mappingFinalResult = finalMapping;
        this.score = maxScore;

    }

    /**
     * for forced map
     */
    private EdgeHashSet remap(boolean forcedMappingForSame,EdgeHashSet forcedPart, int h) throws IOException {
        if(forcedMappingForSame){
            // get restForForced update because the simList has already changed
            AdjList restForForced = new AdjList();
            simList.forEach(hNodeList->{
                if(!forcedPart.contains(new Edge(hNodeList.signName,hNodeList.signName))){
                    restForForced.add(hNodeList);
                }
            });
            EdgeHashSet tmp = remapping(restForForced,h);
            tmp.addAll(forcedPart);
            return tmp;
        }
        else{
            return remapping(simList,h);
        }
    }

    private ArrayList<Double> getScoreInfo(boolean forcedMappingForSame, EdgeHashSet mapping, EdgeHashSet forcedPart) {
        // score the mapping
        ArrayList<Double>  score;
        if(forcedMappingForSame){
            EdgeHashSet tmpMapping = (EdgeHashSet) mapping.clone();
            tmpMapping.addAll(forcedPart);
            score = scoreMapping(tmpMapping);
        }
        else{
            score = scoreMapping(mapping);
        }
        return score;
    }

    /**
     * @return mapping result hungarian ; forced ; updated rest AdjList
     */
    private Triple<EdgeHashSet, EdgeHashSet, AdjList> forcedMap(AdjList simList) throws IOException {
        HashSet<String> graph1Set = simList.getRowSet();
        HashSet<String> graph2Set = simList.getColSet();
        AdjList tmpList = (AdjList) simList.clone();
        EdgeHashSet forcedPart = new EdgeHashSet();

        // remove hNodeLists that have been mapped for the same nodes
        for (String s1:graph1Set) {
            for (String s2 : graph2Set) {
                if (s1.equals(s2)) {
                    forcedPart.add(s1, s1);
                    tmpList.remove(simList.sortGetHeadNodesList(s1));
                    break;
                }
            }
        }
        // map rest of the matrix by Hungarian
        // tmpList matrix has to be updated to be synchronized
        tmpList.updateMatrix();
        return new Triple<>(getEdgeMapFromHA(tmpList),forcedPart,tmpList);
    }

    public EdgeHashSet getMappingFinalResult() {
        assert (mappingFinalResult != null);
        return mappingFinalResult;
    }

    /**
     * Get SimList, Graph1, Graph2
     * @param filePaths SimList1, Graph1, Graph2, SimList2 paths
     * @return result : graph1,rev1,graph2,rev2,simList
     * @throws Exception ioe
     */
    public List<AdjList> io(String... filePaths) throws Exception {
        GraphFileReader reader = new GraphFileReader();
        String graph_2Path = filePaths[2];
        AdjList graph2 = reader.readToAdjL(graph_2Path,false);
        if(graph2.size()==0){
            throw new IOException("graph2 has not been loaded.");
        }
        AdjList rev2 = reader.getRevAdjList();
        String graph_1Path = filePaths[1];
        AdjList graph1 = reader.readToAdjL(graph_1Path,false);
        if(graph1.size()==0){
            throw new IOException("graph1 has not been loaded.");
        }
        AdjList rev1 = reader.getRevAdjList();
        AdjList simList = reader.readToAdjL(filePaths[0]);
        // result
        List<AdjList> result = new ArrayList<>();
        result.addAll(Arrays.asList(graph1,rev1,graph2,rev2,simList));
        return result;
    }
    public double getEC() {
        return EC;
    }

    public double getES() {
        return ES;
    }

    public double getPE() {
        return PE;
    }

    public double getPS() {
        return PS;
    }

    public double getScore() {
        return score;
    }
}
