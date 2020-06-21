package Algorithms.Graph.HGA;


import Algorithms.Graph.Hungarian;
import Algorithms.Graph.NBM;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Utils.AdjList.Graph;
import Algorithms.Graph.Utils.SimMat;
import IO.AbstractFileWriter;
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
import org.apache.commons.io.FileUtils;
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
    protected SimMat originalMat;
    protected SimMat simMat;
    protected Graph graph1;
    protected Graph graph2;
    // parameters
    private boolean forcedMappingForSame;
    private double hAccount;
    protected double bioFactor;
    //---------------mapping result-------------
    private HashMap<String, String> mappingResult;
    //---------------mapping for iteration---------
    private HashMap<String, String> mapping;
    private double PE;
    private double ES;
    private double PS;
    private double EC;
    private double score;
    private double edgeScore;
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
    public HGA(SimMat simMat, Graph graph1, Graph graph2, double bioFactor, boolean forcedMappingForSame,double hAccount,double tolerance) throws IOException {

        this.graph1 = graph1;
        this.graph2 = graph2;
        this.originalMat = (SimMat) simMat.dup();
        this.simMat = simMat;
        this.edgeScore = 1.;
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
        hungarian = new Hungarian(simMat, Hungarian.ProblemType.maxLoc);
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
        HashMap<String, Integer> colMap = toMap.getColMap();
        HashSet<String> assign = new HashSet<>(preMap.values());
        // parallel here there is no interference and no stateful lambda
        //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        int n = toMap.getMat().rows;
        rowMap.keySet().parallelStream().forEach(i -> {
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
            // parallel here there is no interference
            //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
            nodes1.parallelStream().forEach(node1 -> nodes2.parallelStream().forEach(node2 -> {
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
            // parallel here there is no interference
            //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
            nei1.parallelStream().forEach(node1 -> nei2.parallelStream().forEach(node2 ->
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
        int iterSum = nodes1.size() * nodes2.size();
        AtomicInteger i = new AtomicInteger(1);
        // parallel here there is no interference and no stateful lambda
        // https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        // similarity matrix after the neighborhood adjustment
        SimMat preSimMat = (SimMat) simMat.dup();
        logInfo("AddTopology for all nodes pairs in two graphs:");
        nodes1.forEach(n1 -> {
            nodes2.forEach(n2 -> {
                addTopology(n1, n2, preSimMat);
                if(i.get()%(iterSum/10)==0){
                    logInfo(i.get()/(iterSum/10)*10+"%\t");
                }
                i.getAndIncrement();
            });
        });
    }

    private List<Triple<String, String, Double>> sortToPair(Set<String> nodes1, Set<String> nodes2) {
        Vector<Triple<String, String, Double>> sortPairForTopo = new Vector<>();
        // parallel here there is no interference and no stateful lambda
        // https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        nodes1.parallelStream().forEach(node1 -> nodes2.parallelStream().forEach(node2 ->
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
        // edge correctness EC
        Vector<Pair<Edge, Edge>> mappingEdges = setEC(mapping);
        // point and edge score PE
        ES = getES(mappingEdges);
        PS = getPS(mappingEdges);
        PE = ES / 2 + PS;
        score = 100 * EC + PE;
        // log
        logInfo("Scoring for mapping ...");
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
    protected boolean checkPassed(Stack<DoubleMatrix> stackMat, Stack<Double> stackScore, double tolerance) {

        if (stackMat.size() == 3) {
            if(iterCount>1000){
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

            return dif_1 < tolerance || dif_2 < tolerance ||
                    (score == score1 && score1 == score2);
        }
        // size = 2
        else {
            DoubleMatrix s = stackMat.peek();
            double dif = s.sub(stackMat.get(0)).normmax();
            return dif < tolerance;
        }

    }

    public void run() {
        HashMap<String, String> forcedPart;
        HashMap<String, String> remapPart;
        // stacks for simMat converge
        Stack<DoubleMatrix> stackMat = new Stack<>();
        Stack<Double> stackScore = new Stack<>();
        if(debugOut){
            cleanDebugResult();
        }
        boolean checkPassed;
        logInfo("Init mapping...");
        // forced mapping
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
        DoubleMatrix preMat = simMat.getMat();
        stackMat.push(preMat);
        // add to stack top
        // iterating begin---------------------------------------------------------
        double maxScore = score;
        do {
            // log if needed
            logInfo("------------Iteration "+iterCount+"/1000------------");
            // step 1 score mapping
            scoreMapping(mapping);
            // debug
            outDebug();
            // record score
            stackScore.push(score);
            iterCount++;

            // step 2 update based on mapped nodes
            updatePairNeighbors(mapping);
            // step 3 topo adjustment to similarity matrix
            addAllTopology();
            stackMat.push(simMat.getMat());
            // map again
            mapping = remap(toRemap, forcedPart);
            scoreMapping(mapping);
            // debug
            outDebug();
            // record score
            stackScore.push(score);
            // record best
            if(score > maxScore){
                mappingResult = mapping;
                maxScore = score;
            }
            checkPassed = checkPassed(stackMat, stackScore, tolerance);
        } while (!checkPassed);

        this.score = maxScore;
    }

    /**
     * @return full mapping result
     */
    private HashMap<String, String> remap(SimMat toRemap, HashMap<String, String> forced) {
        // hungarian account
        HashMap<String, String> res = remapping(toRemap, getHByAccount());
        // not forced
        if(forced == null){
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

        int limit = (int) (nonZerosNumbs.size()*(1-hAccount));
        return nonZerosNumbs.get(limit);
    }


    /**
     * @return mapping result hungarian ; forced
     */
    private Pair<HashMap<String, String>, HashMap<String, String>> forcedMap() {
        // row to map
        Set<String> rowToMap = simMat.getRowSet();
        rowToMap.removeAll(simMat.getColSet());
        // col to map
        Set<String> colToMap = simMat.getColSet();
        colToMap.removeAll(simMat.getRowSet());
        // set up force mapping
        HashMap<String, String> forceMap = new HashMap<>();
        Set<String> sameNodes = simMat.getRowSet();
        sameNodes.retainAll(simMat.getColSet());
        // parallel here there is no interference and no stateful lambda
        //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        sameNodes.parallelStream().forEach(n -> forceMap.put(n, n));
        // map rest of the matrix by Hungarian
        // tmpList matrix has to be updated to be synchronized
        return new Pair<>(getMappingFromHA(simMat.getPart(rowToMap, colToMap)), forceMap);
    }

    private void logInfo(String message){
        if(logger!=null){
            logger.info(message);
        }
    }

    public HashMap<String, String> getMappingResult() {
        return mappingResult;
    }

    public void outDebug() {
        if(debugOut){
            outPutMatrix();
            outPutScoring();
            outPutMapping();
        }
    }
    public void outPutMatrix()  {
        logInfo("output matrix");
        String path = debugOutputPath + "matrix//";
        Vector<String> matrixVec = new Vector<>();
        double[][] mat = simMat.getMat().toArray2();
        for (double[] doubles : mat) {
            for (int j = 0; j < mat[0].length; j++) {
                matrixVec.add(doubles[j] + " ");
            }
            matrixVec.add("\n");
        }
        try {
            writer.setPath(path + "matrix_" + iterCount + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write(matrixVec, false);
    }

    public void outPutScoring(){
        logInfo("output scores");
        String path = debugOutputPath + "scoring//";
        Vector<String> scoreVec = new Vector<>();

        scoreVec.add("Iteration "+iterCount+":\n");
        scoreVec.add("Score:" + score + "\n");
        scoreVec.add("PE:" + PE + "\n");
        scoreVec.add("EC:" + EC + "\n");
        scoreVec.add("ES:" + ES + "\n");
        scoreVec.add("PS:" + PS + "\n");
        try {
            writer.setPath(path + "scoring_" + iterCount + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write(scoreVec, false);
    }

    public void outPutMapping()  {
        logInfo("output mapping");
        String path = debugOutputPath + "mapping//";
        Vector<String> mappingVec = new Vector<>();

        mappingVec.add("Iteration "+iterCount+":\n");
        mapping.forEach((k,v)->{
            mappingVec.add(k+"->"+v+"\n");
        });
        try {
            writer.setPath(path + "scoring_" + iterCount + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write(mappingVec, false);
    }

    void cleanDebugResult(){
        String mapping = debugOutputPath + "mapping";
        String scoring = debugOutputPath + "scoring";
        String matrix = debugOutputPath + "matrix";
        deleteAllFiles(mapping);
        deleteAllFiles(scoring);
        deleteAllFiles(matrix);

    }
    private void deleteAllFiles(String directory){
        try {
            FileUtils.cleanDirectory(new File(directory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public double setEC() {
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

    public double getEdgeScore() {
        return edgeScore;
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

    public double getEC() {
        return EC;
    }
}
