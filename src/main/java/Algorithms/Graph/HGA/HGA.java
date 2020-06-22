package Algorithms.Graph.HGA;


import Algorithms.Graph.Hungarian;
import Algorithms.Graph.NBM;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Utils.AdjList.Graph;
import Algorithms.Graph.Utils.SimMat;
import IO.AbstractFileWriter;
import IO.GraphFileReader;
import org.apache.commons.io.FileUtils;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * Refer to An Adaptive Hybrid Algorithm for Global Network Algorithms.Alignment
 * Article in IEEE/ACM Transactions on Computational Biology and Bioinformatics · January 2015
 * DOI: 10.1109/TCBB.2015.2465957
 *
 * @author: Haotian Bai
 * Shanghai University, department of computer science
 */

public class HGA {
    protected SimMat originalMat;
    protected SimMat simMat;
    protected Graph graph1;
    protected Graph graph2;
    // parameters
    private boolean forcedMappingForSame;
    private double hAccount;
    protected double bioFactor;
    private double edgeScore = 1.;
    //---------------mapping result(best mapping)-------------
    private HashMap<String, String> mappingResult;
    private double PE_res;
    private double ES_res;
    private double PS_res;
    private double EC_res;
    private double score_res;
    private DoubleMatrix matrix_res;
    //---------------mapping for iteration---------
    private HashMap<String, String> mapping;
    private double PE;
    private double ES;
    private double PS;
    private double EC;
    private double score;
    private Stack<DoubleMatrix> stackMat;
    private Stack<Double> stackScore;

    //----------limit-----
    private int iterCount = 0;
    private int iterMax = 1000;
    //--------------debug---------------
    public String debugOutputPath = "src\\test\\java\\resources\\jupyter\\data\\";
    //--------------Logging-------------
    public Logger logger;
    private AbstractFileWriter writer;
    public boolean debugOut;
    private double tolerance;


    /**
     * Step 1:
     * using homologous coefficients of proteins
     * computed by alignment algorithms for PINs
     *
     * @param graph1               adjacent list of graph1
     * @param graph2               adjacent list of graph2
     * @param simMat               similarity matrix, headNode->graph1, listNodes -> graph2
     * @param bioFactor            sequence similarity account compared with topological effect
     * @param forcedMappingForSame whether force mapping
     * @param hAccount             hungarian matrix account
     */
    public HGA(SimMat simMat, Graph graph1, Graph graph2, double bioFactor, boolean forcedMappingForSame, double hAccount, double tolerance) throws IOException {

        this.graph1 = graph1;
        this.graph2 = graph2;
        this.originalMat = (SimMat) simMat.dup();
        this.simMat = simMat;
        this.forcedMappingForSame = forcedMappingForSame;
        this.tolerance = tolerance;
        // set up preferences
        setBioFactor(bioFactor);
        sethAccount(hAccount);
        simMat.updateNonZerosForRow = true;
        // set up logging
        setupLogger();
        debugOut = true;
    }

    /**
     * HGA to initialize the mapping between two graph by HA,
     * Notice before using this method, make sure matrix is updated, because Hungarian use matrix index directly
     *
     * @return the mapping result
     */
    protected HashMap<String, String> getMappingFromHA(SimMat simMat) {
        logInfo("Hungarian mapping...");
        Hungarian hungarian = new Hungarian(simMat, Hungarian.ProblemType.maxLoc);
        hungarian.setLogger(logger);
        hungarian.run();
        int[] res = hungarian.getResult();
        // map
        HashMap<Integer, String> rowIndexNameMap = simMat.getRowIndexNameMap();
        HashMap<Integer, String> colIndexNameMap = simMat.getColIndexNameMap();

        HashMap<String, String> initMap = new HashMap<>();
        for (int i = 0; i < res.length; i++) {
            int j = res[i];
            if (j == -1) {
                continue;
            }
            initMap.put(rowIndexNameMap.get(i), colIndexNameMap.get(j));
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
     * @param h     row has at least h nonzero entries
     */
    protected HashMap<String, String> remapping(SimMat toMap, int h) {
        assert (toMap != null);

        // check
        Pair<SimMat, SimMat> res = toMap.getSplit(h);
        SimMat H = res.getFirst();
        SimMat G = res.getSecond();
        // Hungarian alg
        HashMap<String, String> mapping = getMappingFromHA(H);
        // Greedy alg
        greedyMap(G, mapping);
        return mapping;
    }

    /**
     * Greedily map the maximum value for each rows in the G matrix.
     */
    public static void greedyMap(SimMat toMap, HashMap<String, String> preMap) {
        HashMap<Integer, String> rowMap = toMap.getRowIndexNameMap();
        HashSet<String> assign = new HashSet<>(preMap.values());
        // no parallel here, assign is stateful
        rowMap.keySet().forEach(i -> {
            String tgt = rowMap.get(i);
            if (!preMap.containsKey(tgt)) {
                String mapStr = toMap.getMax(i, assign);
                preMap.put(tgt, mapStr);
                // graph mapping finished
                if (mapStr != null) {
                    assign.add(mapStr);
                }
            }
        });
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
     * @param mapping current mapping result, and one edge means the srcNode and tgtNode has already mapped, srcNode ->graph1, tgtNode -> graph2
     */
    protected void updatePairNeighbors(HashMap<String, String> mapping) {
        logInfo("adjust neighborhood similarity based on mapping result...");
        NBM.neighborSimAdjust(graph1, graph2, simMat, mapping);
    }

    /**
     * Step 3.1:
     * Adding topology information:
     * Given any two nodes ui, vj in the networks A and B,
     * respectively, their topological similarities are computed
     * based on an approach previously used for the topological
     * similarity of bio-molecular networks.which we have
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
    protected void addTopology(String node1, String node2, SimMat preMat) {
        HashMap<String, HashSet<String>> neb1Map = graph1.getNeighborsMap();
        HashMap<String, HashSet<String>> neb2Map = graph2.getNeighborsMap();
        // there should be new objects!
        HashSet<String> neighbors_1 = neb1Map.get(node1);
        HashSet<String> neighbors_2 = neb2Map.get(node2);
        // compute topologyInfo
        double eNeighbors = getNeighborTopologyInfo(neighbors_1, neighbors_2, preMat);
        // add node1,node2
        neighbors_1.add(node1);
        neighbors_2.add(node2);
        double eNonNeighbors = getNonNeighborTopologyInfo(neighbors_1, neighbors_2, preMat);
        double eTP = (eNeighbors + eNonNeighbors) / 2;
        double valToUpdate = originalMat.getVal(node1, node2) * bioFactor + eTP * (1 - bioFactor);
        simMat.put(node1, node2, valToUpdate);
    }


    protected double getNonNeighborTopologyInfo(HashSet<String> nei1, HashSet<String> nei2, SimMat preMat) {
        AtomicReference<Double> res = new AtomicReference<>((double) 0);
        HashSet<String> nodes1 = graph1.getAllNodes();
        HashSet<String> nodes2 = graph2.getAllNodes();

        int nonNei1Size = nodes1.size() - nei1.size();
        int nonNei2Size = nodes2.size() - nei2.size();

        // get the rest nodes
        nodes1.removeAll(nei1);
        nodes2.removeAll(nei2);
        if (nonNei1Size != 0 && nonNei2Size != 0) {
            int size = (nonNei1Size + 1) * (nonNei2Size + 1);
            // shift parallel to the front
            nodes1.forEach(node1 -> nodes2.forEach(node2 -> {
                if (!nei1.contains(node1) && !nei2.contains(node2)) {
                    res.updateAndGet(v -> v + preMat.getVal(node1, node2));
                }
            }));
            return res.get() / size;
        }

        if (nonNei1Size == 0 && nonNei2Size == 0) {
            int size = nodes1.size() * nodes2.size();
            return preMat.sum() / size;
        }
        return res.get();
    }

    protected double getNeighborTopologyInfo(HashSet<String> nei1, HashSet<String> nei2, SimMat preMat) {
        AtomicReference<Double> res = new AtomicReference<>((double) 0);
        HashSet<String> nodes1 = graph1.getAllNodes();
        HashSet<String> nodes2 = graph2.getAllNodes();
        int nei1Size = nei1.size();
        int nei2Size = nei2.size();
        if (nei1Size != 0 && nei2Size != 0) {
            int size = nei1Size * nei2Size;
            // shift parallel to the front
            nei1.forEach(node1 -> nei2.forEach(node2 ->
                    res.updateAndGet(v -> v + preMat.getVal(node1, node2))));
            return res.get() / size;
        }
        if (nei1Size == 0 && nei2Size == 0) {
            int size = nodes1.size() * nodes2.size();
            return preMat.sum() / size;
        }
        return res.get();
    }

    /**
     * Step 3 - integrated all steps in process 3(Topology info):
     * iterate all nodes pairs to add topological information
     * Notice: the result would be different when
     */
    protected void addAllTopology() {
        Set<String> nodes1 = simMat.getRowMap().keySet();
        Set<String> nodes2 = simMat.getColMap().keySet();
        AtomicInteger i = new AtomicInteger(1);
        // parallel the row
        // https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        // similarity matrix after the neighborhood adjustment
        SimMat preSimMat = (SimMat) simMat.dup();
        logInfo("AddTopology for all nodes pairs in two graphs:");
        nodes1.parallelStream().forEach(n1 -> {
            nodes2.forEach(n2 -> addTopology(n1, n2, preSimMat));
            if (i.get() % (nodes1.size() / 10) == 0) {
                logInfo(i.get() / (nodes1.size() / 10) * 10 + "%\t");
            }
            i.getAndIncrement();
        });
    }

    private List<Triple<String, String, Double>> sortToPair(Set<String> nodes1, Set<String> nodes2) {
        Vector<Triple<String, String, Double>> sortPairForTopo = new Vector<>();
        // parallel here there is no interference and no stateful lambda
        // https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        nodes1.parallelStream().forEach(node1 -> nodes2.forEach(node2 ->
                sortPairForTopo.add(new Triple<>(node1, node2, simMat.getVal(node1, node2)))));
        List<Triple<String, String, Double>> res = sortPairForTopo.stream().sorted(Comparator.comparingDouble(Triple::getThird)).collect(Collectors.toList());
        Collections.reverse(res);
        return res;
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
     */
    protected void scoreMapping(HashMap<String, String> mapping) {
        logInfo("Scoring for mapping ...");

        // edge correctness EC
        Vector<Pair<Edge, Edge>> mappingEdges = setEC(mapping);
        // point and edge score PE
        ES = getES(mappingEdges);
        PS = getPS(mappingEdges);
        PE = ES / 2 + PS;
        score = 100 * EC + PE;
    }

    private double getES(Vector<Pair<Edge, Edge>> mappingEdges) {

        AtomicReference<Double> ES = new AtomicReference<>((double) 0);
        for (Iterator<Pair<Edge, Edge>> iterator = mappingEdges.iterator(); iterator.hasNext(); ) {
            Pair<Edge, Edge> map = iterator.next();
            Edge edge1 = map.getFirst();
            Edge edge2 = map.getSecond();
            if (simMat.getVal(edge1.getSource().getStrName(), edge2.getSource().getStrName()) > 0 &&
                    simMat.getVal(edge1.getTarget().getStrName(), edge2.getTarget().getStrName()) > 0) {
                ES.updateAndGet(v -> v + edgeScore);
            } else {
                iterator.remove();
            }
        }
        return ES.get();
    }

    /**
     * @param mappingEdges getES() filters out unqualified edges
     */
    protected double getPS(Vector<Pair<Edge, Edge>> mappingEdges) {
        // parallel here there is no interference and no stateful lambda
        //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        AtomicReference<Double> PS = new AtomicReference<>((double) 0);
        mappingEdges.parallelStream().forEach(map -> {
            Edge edge1 = map.getFirst();
            Edge edge2 = map.getSecond();
            String n1_1 = edge1.getSource().getStrName();
            String n1_2 = edge1.getTarget().getStrName();
            String n2_1 = edge2.getSource().getStrName();
            String n2_2 = edge2.getTarget().getStrName();
            PS.updateAndGet(v -> v + simMat.getVal(n1_1, n2_1) + simMat.getVal(n1_2, n2_2));
        });
        return PS.get();
    }


    /**
     * @return set edge correctness and mapping edges[Pair:{graph1Source,graph1Target},{graph2Source,graph2Target}]
     */
    protected Vector<Pair<Edge, Edge>> setEC(HashMap<String, String> mapping) {
        HashMap<String, HashSet<String>> neb1Map = graph1.getNeighborsMap();
        HashMap<String, HashSet<String>> neb2Map = graph2.getNeighborsMap();
        // toMap will decrease when nodes have been checked
        HashSet<String> toMap = new HashSet<>(mapping.keySet());
        AtomicInteger count = new AtomicInteger();
        Vector<Pair<Edge, Edge>> mappingEdges = new Vector<>();
        for (Iterator<String> iterator = toMap.iterator(); iterator.hasNext(); ) {
            String n1 = iterator.next();
            String n1_ = mapping.get(n1);
            iterator.remove();
            HashSet<String> nebs = neb1Map.get(n1);
            // parallel here there is no interference and no stateful lambda
            //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
            // overlap -> one edge in graph1(contains n1 as one node)
            Collection<String> edge1s = nebs.parallelStream().filter(toMap::contains).collect(Collectors.toList());
            if (edge1s.size() == 0) {
                continue;
            }
            // parallel here there is no interference and no stateful lambda
            //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
            // check graph2 -> have the corresponding "edge"
            edge1s.parallelStream().forEach(n2 -> {
                String n2_ = mapping.get(n2);
                if (neb2Map.get(n1_).contains(n2_)) {
                    count.getAndIncrement();
                    mappingEdges.add(new Pair<>(new Edge(n1, n2), new Edge(n1_, n2_)));
                }
            });
        }
        EC = (double) count.get() / graph1.getEdgeCount();
        return mappingEdges;
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
    protected boolean checkPassed(double tolerance) {

        if (stackMat.size() == 3) {
            if (iterCount > iterMax) {
                return true;
            }
            DoubleMatrix s1 = stackMat.get(1);
            DoubleMatrix s = stackMat.peek();
            // remove bottom which is the oldest for every iteration
            DoubleMatrix s2 = stackMat.remove(0);

            double score = stackScore.peek();
            double score1 = stackScore.get(1);
            double score2 = stackScore.remove(0);

            double dif_1 = s.sub(s1).normmax();
            double dif_2 = s.sub(s2).normmax();
            logInfo("Iteration:" + iterCount + "\tdif_1 " + dif_1 + "\t" + "dif_2 " + dif_2 + "\nScore:" + "score1 " + score1
                    + "\t" + "score2 " + score2);
            return dif_1 < tolerance || dif_2 < tolerance ||
                    (score == score1 && score1 == score2);

        }
        // size = 2
        else {
            DoubleMatrix s = stackMat.peek();
            double dif = s.sub(stackMat.get(0)).normmax();
            logInfo("Iteration:" + iterCount + "\tdif " + dif);
            return dif < tolerance;
        }

    }

    public void run() {
        HashMap<String, String> forcedPart;
        HashMap<String, String> remapPart;
        if (debugOut) {
            cleanDebugResult();
        }
        logInfo("Init mapping...");
        Pair<SimMat, HashMap<String, String>> init = initMap();
        // iterate
        hgaIterate(this.mapping, this.simMat, init.getFirst(), init.getSecond()
                , iterCount, score, PE, EC, PS, ES);

    }

    private Pair<SimMat, HashMap<String, String>> initMap() {
        // stacks for simMat converge
        stackMat = new Stack<>();
        stackScore = new Stack<>();
        HashMap<String, String> remapPart;
        HashMap<String, String> forcedPart;// forced mapping
        if (forcedMappingForSame) {
            Pair<HashMap<String, String>, HashMap<String, String>> res = forcedMap();
            // hungarian for the res
            remapPart = res.getFirst();
            // forced
            forcedPart = res.getSecond();
            // mapping
            mapping = new HashMap<>(remapPart);
            mapping.putAll(forcedPart);
        } else {
            // get the initial similarity matrix S0
            remapPart = getMappingFromHA(simMat);
            forcedPart = null;
            mapping = new HashMap<>(remapPart);
        }
        SimMat toRemap = simMat.getPart(remapPart.keySet(), remapPart.values());
        // getMatrix return the quoted mat from simMat, should be copied
        stackMat.push(simMat.getMat().dup());
        // add to stack top
        scoreMapping(mapping);
        // record score
        stackScore.push(score);
        // debug
        outDebug();
        return new Pair<>(toRemap, forcedPart);
    }

    private void hgaIterate(HashMap<String, String> mapping, SimMat simMat,
                            SimMat toRemap, HashMap<String, String> forcedPart, int iterCount, double... scores) {
        initScores(scores);
        score_res = score;
        this.mapping = mapping;
        this.simMat = simMat;
        this.iterCount = iterCount;
        boolean checkPassed;
        do {
            // log if needed
            logInfo("------------Iteration " + this.iterCount + "/1000------------");
            this.iterCount++;
            // step 2 update based on mapped nodes
            updatePairNeighbors(this.mapping);
            // step 3 topo adjustment to similarity matrix
            addAllTopology();
            stackMat.push(simMat.getMat().dup());
            // map again
            this.mapping = remap(toRemap, forcedPart);
            scoreMapping(this.mapping);
            // record score
            stackScore.push(score);
            // debug
            outDebug();
            // record best
            if (score > score_res) {
                setUpResult();
            }
            checkPassed = checkPassed(tolerance);
        } while (!checkPassed);
        // output result
        outPutResult();
    }

    /**
     * @return full mapping result
     */
    private HashMap<String, String> remap(SimMat toRemap, HashMap<String, String> forced) {
        int h = getHByAccount();
        logInfo("Remapping : select rows have at least " + h + " non-zero items based on your input account " + hAccount * 100 + "%");
        // regain from file, and there is no remap part, retain.
        if (toRemap == null) {
            toRemap = this.simMat;
        }
        // hungarian account
        HashMap<String, String> res = remapping(toRemap, h);
        // not forced
        if (forced == null) {
            return res;
        }
        res.putAll(forced);
        return res;
    }

    /**
     * get h standard based on the account for hungarian user has input
     *
     * @return non-zeros selection standard h
     */
    int getHByAccount() {
        // get non-zeros number by rows and sort it
        Vector<Integer> nonZeros = new Vector<>();
        simMat.getNonZerosIndexMap().values().parallelStream().forEach(set -> nonZeros.add(set.size()));
        List<Integer> nonZerosNumbs = nonZeros.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        int limit = (int) (nonZerosNumbs.size() * (1 - hAccount));
        return nonZerosNumbs.get(limit);
    }


    /**
     * @return mapping result hungarian ; forced
     */
    private Pair<HashMap<String, String>, HashMap<String, String>> forcedMap() {
        Triple<HashMap<String, String>, SimMat, Set<String>> res = getRemapForForced();
        HashMap<String, String> forceMap = res.getFirst();
        SimMat remap = res.getSecond();
        // map rest of the matrix by Hungarian
        // tmpList matrix has to be updated to be synchronized
        return new Pair<>(getMappingFromHA(remap), forceMap);
    }

    /**
     * @return forceMap, remap, sameNodes
     */
    private Triple<HashMap<String, String>, SimMat, Set<String>> getRemapForForced() {
        // row to map
        Set<String> rowToMap = simMat.getRowSet();
        rowToMap.removeAll(simMat.getColSet());
        // col to map
        Set<String> colToMap = simMat.getColSet();
        colToMap.removeAll(simMat.getRowSet());
        HashMap<String, String> forceMap;
        SimMat remap;
        Set<String> sameNodes;
        // set up force mapping
        forceMap = new HashMap<>();
        sameNodes = simMat.getRowSet();
        sameNodes.retainAll(simMat.getColSet());
        // parallel here there is no interference and no stateful lambda
        //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        HashMap<String, String> finalForceMap = forceMap;
        sameNodes.parallelStream().forEach(n -> finalForceMap.put(n, n));
        remap = simMat.getPart(rowToMap, colToMap);
        return new Triple<>(forceMap, remap, sameNodes);
    }

    private void logInfo(String message) {
        if (logger != null) {
            logger.info(message);
        }
    }


    public void outDebug() {
        if (debugOut) {
            outPutMatrix(simMat.getMat(), false);
            outPutScoring(false, score, PE, EC, ES, PS);
            outPutMapping(mapping, false);
        }
    }

    public void outPutMatrix(DoubleMatrix mat, boolean isResult) {
        logInfo("output matrix");
        String path = debugOutputPath + "matrix//";
        Vector<String> matrixVec = new Vector<>();
        double[][] mat_ = simMat.getMat().toArray2();
        for (double[] doubles : mat_) {
            for (int j = 0; j < mat_[0].length; j++) {
                matrixVec.add(doubles[j] + " ");
            }
            matrixVec.add("\n");
        }
        try {
            if (isResult) {
                writer.setPath(path + "matrixResult_" + iterCount + ".txt");
            } else {
                writer.setPath(path + "matrix_" + iterCount + ".txt");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write(matrixVec, false);
    }

    public void outPutScoring(boolean isResult, double... scores) {
        logInfo("output scores");
        String path = debugOutputPath + "scoring//";
        Vector<String> scoreVec = new Vector<>();

        scoreVec.add("Iteration " + iterCount + ":\n");
        scoreVec.add("Score:" + scores[0] + "\n");
        scoreVec.add("PE:" + scores[1] + "\n");
        scoreVec.add("EC:" + scores[2] + "\n");
        scoreVec.add("ES:" + scores[3] + "\n");
        scoreVec.add("PS:" + scores[4] + "\n");
        try {
            if (isResult) {
                writer.setPath(path + "scoringResult_" + iterCount + ".txt");
            } else {
                writer.setPath(path + "scoring_" + iterCount + ".txt");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write(scoreVec, false);
    }

    public void outPutMapping(HashMap<String, String> mapping, boolean isResult) {
        logInfo("output mapping");
        String path = debugOutputPath + "mapping//";
        Vector<String> mappingVec = new Vector<>();

        mappingVec.add("Iteration " + iterCount + ":\n");
        mapping.forEach((k, v) -> mappingVec.add(k + "->" + v + "\n"));
        try {
            if (isResult) {
                writer.setPath(path + "mappingResult_" + iterCount + ".txt");
            } else {
                writer.setPath(path + "mapping_" + iterCount + ".txt");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write(mappingVec, false);
    }

    void cleanDebugResult() {
        String mapping = debugOutputPath + "mapping";
        String scoring = debugOutputPath + "scoring";
        String matrix = debugOutputPath + "matrix";
        deleteAllFiles(mapping);
        deleteAllFiles(scoring);
        deleteAllFiles(matrix);

    }

    private void deleteAllFiles(String directory) {
        try {
            FileUtils.cleanDirectory(new File(directory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpResult() {
        ES_res = ES;
        PE_res = PE;
        PS_res = PS;
        EC_res = EC;
        score_res = score;
        mappingResult = (HashMap<String, String>) mapping.clone();
        matrix_res = simMat.getMat().dup();
    }

    private void initScores(double... scores) {
        score = scores[0];
        PE = scores[1];
        EC = scores[2];
        ES = scores[3];
        PS = scores[4];
    }

    public void outPutResult() {
        outPutMapping(mappingResult, true);
        outPutMatrix(matrix_res, true);
        outPutScoring(true, score_res, PE_res, EC_res, ES_res, PS_res);
    }
//TODO retain
//    /**
//     * If data has been lost for some reasons, user can
//     * retain the process they have been on going.
//     */
//    public void retain(String matrixPath,String scorePath,String mappingPath) throws IOException {
//        DoubleMatrixReader matrixReader = new DoubleMatrixReader(matrixPath);
//        DoubleMatrix mat = matrixReader.getMat();
//        SimMat simMat = new SimMat(,,mat);
//        hgaIterate();
//    }

    public double getES_res() {
        return ES_res;
    }

    public double getPE_res() {
        return PE_res;
    }

    public double getPS_res() {
        return PS_res;
    }

    public double getScore_res() {
        return score_res;
    }

    public DoubleMatrix getMatrix_res() {
        return matrix_res;
    }


    public double getEC_res() {
        return EC_res;
    }

    public double getES() {
        return ES;
    }

    public double getPS() {
        return PS;
    }

    public double getPE() {
        return PE;
    }

    public double getEC() {
        return EC;
    }

    public double getScore() {
        return score;
    }

    public HashMap<String, String> getMapping() {
        return mapping;
    }

    public HashMap<String, String> getMappingResult() {
        return mappingResult;
    }


    public void setBioFactor(double bioFactor) {
        assert (bioFactor >= 0 && bioFactor <= 1);
        this.bioFactor = bioFactor;
    }

    public void setForcedMappingForSame(boolean forcedMappingForSame) {
        this.forcedMappingForSame = forcedMappingForSame;
    }

    public void setEdgeScore(double edgeScore) {
        this.edgeScore = edgeScore;
    }

    public void sethAccount(double hAccount) {
        assert (hAccount >= 0 && hAccount <= 1);
        this.hAccount = hAccount;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public void setupLogger() throws IOException {
        logger = Logger.getLogger("MyLog");
        FileHandler fh;
        fh = new FileHandler("HGALogFile.log");
        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
        // output matrix, scoring and mapping result
        writer = new AbstractFileWriter() {
            @Override
            public void write(Vector<String> context, boolean closed) {
                super.write(context, true);
            }
        };
    }

    public void setIterMax(int iterMax) {
        this.iterMax = iterMax;
    }

    public static void main(String[] args) throws IOException {
        GraphFileReader reader = new GraphFileReader(true, true, false);
        Graph yeast = reader.readToGraph("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt", false);
        Graph human = reader.readToGraph("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt", false);
        reader.setRecordNonZeros(true);
        reader.setRecordNeighbors(false);
        SimMat simMat = reader.readToSimMat("src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/yeastHumanSimList_EvalueLessThan1e-10.txt", yeast.getAllNodes(), human.getAllNodes(), true);
        HGA hga = new HGA(simMat, yeast, human, 0.4,true,0.5,0.01);
        hga.run();
    }

}
