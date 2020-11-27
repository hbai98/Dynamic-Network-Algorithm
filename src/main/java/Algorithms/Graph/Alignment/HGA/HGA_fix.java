package Algorithms.Graph.Alignment.HGA;

import Algorithms.Graph.Hungarian;
import Algorithms.Graph.NBM;
import DS.Matrix.SimMat;
import DS.Matrix.StatisticsMatrix;
import DS.Network.Graph;
import DS.Network.UndirectedGraph;
import IO.Writer.AbstractFileWriter;
import com.aparapi.Range;
import org.apache.commons.io.FileUtils;
import org.jgrapht.alg.util.Pair;

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
 * The difference between the fix version with previous one is the mapping strategy,
 * as HGA_fix only update selected nodes in index network(matrix rows).The criteria is how many non_zero entries
 * in the rows of similarity matrix. Say, when non_zero items > m, m is an integer value, then the row will be sent to
 * the Hungarian matrix and the Hungarian matrix would not change, which means Hungarian allocation will always be implemented
 * in those selected rows. In the contrary, HGA would select the rows with high average similarity score so the hungarian matrix would always
 * filled with top ranking rows.
 * @param <V>
 * @param <E>
 */
public class HGA_fix<V,E>{
    private static final int NON_ZERO_ROW_ = 5;
    public static boolean GPU = false;

    protected SimMat<V> simMat;
    protected Graph<V, E> udG1;
    protected Graph<V, E> udG2;
    // parameters
    private boolean forcedMappingForSame;
    protected double bioFactor;
    private double edgeScore = 1.;
    //---------------mapping result(best mapping)-------------
    public HashMap<V, V> mappingResult;
    private double PE_res;
    private double ES_res;
    private double PS_res;
    private double EC_res;
    private double score_res;
    private StatisticsMatrix matrix_res;
    //---------------mapping for iteration---------
    public HashMap<V, V> mapping;
    private double PE;
    private double ES;
    private double PS;
    private double EC;
    private double score;
    //---------------mapping for iteration---------
    private SimMat<V> originalMat;
    private Stack<StatisticsMatrix> stackMat;
    private Stack<Double> stackScore;

    //----------limit-----
    private final int splitLimit = 20;
    private int iterCount = 0;
    private int iterMax = 1000;
    //--------------debug---------------
    public static String debugOutputPath = "src\\test\\java\\resources\\jupiter\\data\\";
    //--------------Logging-------------
    public Logger logger;
    private AbstractFileWriter writer;
    public static boolean debugOut = true;
    private double tolerance;
    public int iter_res;
    private Vector<Pair<E, E>> mappingEdges;
    private double sumPreSimMat;


    /**
     * Step 1:
     * Initialize with the homologous coefficients of proteins
     * computed by alignment algorithms for PINs
     *
     * @param udG1                 graph1
     * @param udG2                 graph2
     * @param simMat               similarity matrix
     * @param nodalFactor          nodal compared with topological effect
     * @param forcedMappingForSame whether force mapping
     * @param hAccount             hungarian matrix account
     * @param tolerance            the limit to check whether the matrix has converged
     */
    public HGA_fix(SimMat<V> simMat,
               UndirectedGraph<V, E> udG1,
               UndirectedGraph<V, E> udG2,
               double nodalFactor, boolean forcedMappingForSame, double tolerance) throws IOException {

        this.udG1 = udG1;
        this.udG2 = udG2;
        this.originalMat = simMat.dup();
        this.simMat = simMat;
        this.forcedMappingForSame = forcedMappingForSame;
        this.tolerance = tolerance;
        // set up preferences
        setBioFactor(nodalFactor);
        // set up logging
        if (debugOut) {
            setupLogger();
        }
    }

    /**
     * HGA to initialize the mapping between two graph by HA,
     * Notice before using this method, make sure matrix is updated, because Hungarian use matrix index directly
     *
     * @return the mapping result
     */
    protected HashMap<V, V> getMappingFromHA(SimMat<V> simMat) {
        logInfo("Hungarian mapping...");
        Hungarian<V> hungarian = new Hungarian<>(simMat, Hungarian.ProblemType.maxLoc);
        hungarian.setLogger(logger);
        hungarian.run();
        int[] res = hungarian.getResult();
        // map
        HashMap<Integer, V> rowIndexNameMap = simMat.getRowIndexNameMap();
        HashMap<Integer, V> colIndexNameMap = simMat.getColIndexNameMap();
        HashMap<V, V> initMap = new HashMap<>();
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
     * into two matrices: the H-matrix, in which each row
     * has at least h nonzero entries, and the G-matrix, which
     * collects the remaining entries of S(t)
     *
     * @param toMap  matrix for mapping
     * @param forced forced mapping
     * @param hLimit if index graph nodes is less than this limit, use the hungarian directly
     * @return initial mapping result
     */
    protected HashMap<V, V> initMapping(SimMat<V> toMap, HashMap<V, V> forced, int hLimit) {
        logInfo("Initialize mapping to split the matrix.");
        assert (toMap != null);

        if (udG1.vertexSet().size() < hLimit && udG2.vertexSet().size() < hLimit) {
            logInfo("Map directly using Hungarian allocation strategy.");
            // simMat's parameters initialize -> hungRows + hungRowsLeft = toMap rows)
            simMat.setHungRows(toMap.getRowSet());
            simMat.setHungRowsLeft(new HashSet<>());
            HashMap<V,V> mapping = getMappingFromHA(toMap);
            mapping.putAll(forced);
            return mapping;
        } else {
            Pair<SimMat<V>, SimMat<V>> res = toMap.splitByNoneZeros(NON_ZERO_ROW_);
            logInfo("Selecting rows with at least " + NON_ZERO_ROW_ + " for Hungarian allocation, and the left rows" +
                    "for Greedy mapping.");
            // check
            SimMat<V> Hmatrix = res.getFirst();
            SimMat<V> Gmatrix = res.getSecond();
            // Hungarian takes priority, and the left columns will be allocated by Greedy mapping
            HashMap<V, V> mapping = getMappingFromHA(Hmatrix);
            greedyMap(Gmatrix, mapping);
            // add forced mapping
            mapping.putAll(forced);

            return mapping;
        }
    }

    /**
     * Greedily map the maximum value for each rows in the G matrix using the left columns after Hungarian allocation.
     */
    protected void greedyMap(SimMat<V> toMap, HashMap<V, V> preMap) {
        HashMap<Integer, V> rowMap = toMap.getRowIndexNameMap();
        HashSet<V> assign = new HashSet<>(preMap.values());
        // no parallel here, assign is stateful
        rowMap.keySet().forEach(i -> {
            V tgt = rowMap.get(i);
            if (!preMap.containsKey(tgt)) {
                V mapStr = toMap.getMax(i, assign);
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
    protected void updatePairNeighbors(HashMap<V, V> mapping) {
        logInfo("adjust neighborhood similarity based on mapping result...");
        NBM<V, E> nbm = new NBM<>(udG1, udG2, simMat, mapping);
        nbm.neighborSimAdjust();
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
    protected void addTopology(V node1, V node2, SimMat<V> preMat) {
        Set<V> neighbors_1 = udG1.getNeb(node1);
        Set<V> neighbors_2 = udG2.getNeb(node2);
        // compute topologyInfo
        double eNeighbors = getNeighborTopologyInfo(neighbors_1, neighbors_2, preMat);
        // tmp Set
        Set<V> tmp1 = new HashSet<>(neighbors_1);
        Set<V> tmp2 = new HashSet<>(neighbors_2);
        // add node1,node2
        tmp1.add(node1);
        tmp2.add(node2);
        double eNonNeighbors = getNonNeighborTopologyInfo(tmp1, tmp2, preMat);
        double eTP = (eNeighbors + eNonNeighbors) / 2;
        double valToUpdate = originalMat.getVal(node1, node2) * bioFactor + eTP * (1 - bioFactor);
        simMat.put(node1, node2, valToUpdate);
    }

    protected double getNonNeighborTopologyInfo(Set<V> nei1, Set<V> nei2, SimMat<V> preMat) {
        AtomicReference<Double> res = new AtomicReference<>((double) 0);
        Set<V> nodes1 = new HashSet<>(udG1.vertexSet());
        Set<V> nodes2 = new HashSet<>(udG2.vertexSet());
        int nonNei1Size = nodes1.size() - nei1.size();
        int nonNei2Size = nodes2.size() - nei2.size();
        // get the rest nodes
        nodes1.removeAll(nei1);
        nodes2.removeAll(nei2);
        if (nonNei1Size != 0 && nonNei2Size != 0) {
            int size = nonNei1Size * nonNei2Size;
            nodes1.forEach(node1 ->
                    nodes2.forEach(node2 -> res.updateAndGet(v -> v + preMat.getVal(node1, node2))));
            return res.get() / size;
        }

        if (nonNei1Size == 0 && nonNei2Size == 0) {
            int size = nodes1.size() * nodes2.size();
            return sumPreSimMat / size;
        }
        return res.get();
    }

    // return score and sum of neighbors
    protected double getNeighborTopologyInfo(Set<V> nei1, Set<V> nei2, SimMat<V> preMat) {
        AtomicReference<Double> score = new AtomicReference<>((double) 0);
        Set<V> nodes1 = udG1.vertexSet();
        Set<V> nodes2 = udG2.vertexSet();
        int nei1Size = nei1.size();
        int nei2Size = nei2.size();
        if (nei1Size != 0 && nei2Size != 0) {
            int size = nei1Size * nei2Size;
            // shift parallel to the front
            nei1.forEach(node1 -> nei2.forEach(node2 ->
                    score.updateAndGet(v -> v + preMat.getVal(node1, node2))));
            return score.get() / size;
        }
        if (nei1Size == 0 && nei2Size == 0) {
            int size = nodes1.size() * nodes2.size();
            return sumPreSimMat / size;
        }
        return score.get();
    }

    /**
     * Step 3 - integrated all steps in process 3(Topology info):
     * iterate all nodes pairs to add topological information
     * Notice: the result would be different when
     */
    protected void addAllTopology() {
        Set<V> nodes1 = simMat.getRowMap().keySet();
        Set<V> nodes2 = simMat.getColMap().keySet();
        // parallel the rows
        // https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        // similarity matrix after the neighborhood adjustment
        SimMat<V> preSimMat = simMat.dup();
        sumPreSimMat = preSimMat.getMat().elementSum();
        // when index graph nodes scale is less than LIMIT then HGA uses parallel CPU instead
        // && nodes1.size() > LimitOfIndexGraph
        if (GPU) {
            logInfo("AddTopology for all nodes pairs in two graphs with the GPU programming:");
            gpuForHGA(preSimMat);
        } else {
            logInfo("AddTopology for all nodes pairs in two graphs with the CPU parallel programming:");
            nodes1.parallelStream().forEach(n1 -> nodes2.forEach(n2 -> addTopology(n1, n2, preSimMat)));
        }

    }

    private void gpuForHGA(SimMat<V> preMat) {
        // get nodes in order that double[] out can be visit by out[i,j]
        Set<V> nodes1 = preMat.getRowMap().keySet();
        Set<V> nodes2 = preMat.getColMap().keySet();
        // prepare the input indexes for all neighbors and all non neighbors
        // map
        HashMap<V, Integer> rowMap = preMat.getRowMap();
        HashMap<V, Integer> colMap = preMat.getColMap();
        // positions for neighbors, non neighbors make a complement
        Vector<Integer> nei_x = new Vector<>();
        Vector<Integer> nei_y = new Vector<>();
        // every node possibly share various number of neighbors, record the start
        final int[] start_x = new int[nodes1.size() + 1];
        final int[] start_y = new int[nodes2.size() + 1];
        // so (i,j) in simMat -> int[] neighbors = nei_x[start_x[i],start_x[i+1]) and nei_y[start_y[j],start_y[j+1])
        // int[] non-neighbors = [0,n-1]-nei_x[start_x[i],start_x[i+1]) and [0,m-1]-nei_y[start_y[j],start_y[j+1])
        // result to be polished
        double[] out = simMat.getMat().data();
        // preMat data
        final double[] pre = preMat.getMat().data();
        // original data
        final double[] ori = originalMat.getMat().data();
        // initialize nei_x
        initNeighborToArray(nodes1, udG1, rowMap, nei_x, start_x);
        // initialize nei_y
        initNeighborToArray(nodes2, udG2, colMap, nei_y, start_y);

        GPUKernelForHGA kernel = new GPUKernelForHGA(
                pre, ori, out, // 3 matrix
                nei_x, start_x, // graph1 neighbors
                nei_y, start_y, // graph2 neighbors
                sumPreSimMat, // sum of mat
                bioFactor);
        Range range = Range.create(nodes1.size() * nodes2.size(), 1);
        kernel.execute(range).get(out);
        simMat.setData(out);
        kernel.dispose();
    }

    private void initNeighborToArray(Set<V> nodes, Graph<V, E> g,
                                     HashMap<V, Integer> map, Vector<Integer> neighbors,
                                     int[] starts) {
        starts[0] = 0;
        AtomicInteger c = new AtomicInteger(1);
        nodes.forEach(n1 -> {
            Set<V> nei = g.getNeb(n1);
            nei.forEach(n -> neighbors.add(map.get(n)));
            starts[c.get()] = nei.size() + starts[c.get() - 1];
            c.getAndIncrement();
        });
    }


    /**
     * This step is used to score current mapping to indicate whether there's
     * a need to adjust and map again
     * <br>
     * <p>The following params for evaluate the whole network mapping</p>
     *     <ol>
     *         <li>EC : E Correctness (EC) is often used to measure
     * the degree of topological similarity and
     * can be estimated as the percentage of matched edges</li>
     *
     *          <li>
     *              PE: Point and E Score(PE) is clearly stricter than EC because it reflects the status
     *              of both the node and edge matches in the mapping.
     *          </li>
     *
     *          <li>
     *               The score for an edge (the E Score, ES) equals zero if any of its nodes does not match
     *               with its similar nodes, and the score for a node (the Point Score, PS) equals zero if none of its edges has a score.
     *          </li>
     *     </ol>
     */

    protected void scoreMapping(HashMap<V, V> mapping) {
        logInfo("Scoring for mapping ...");
        // edge correctness EC
        mappingEdges = setEC(mapping);
        // point and edge score PE
        ES = getES(mappingEdges);
        PS = getPS(mappingEdges);
        PE = ES / 2 + PS;
        score = 100 * EC + PE;
    }

    private double getES(Vector<Pair<E, E>> mappingEdges) {
        AtomicReference<Double> ES = new AtomicReference<>((double) 0);
        for (Iterator<Pair<E, E>> iterator = mappingEdges.iterator(); iterator.hasNext(); ) {
            Pair<E, E> map = iterator.next();
            E edge1 = map.getFirst();
            E edge2 = map.getSecond();
            if (simMat.getVal(udG1.getEdgeSource(edge1), udG2.getEdgeSource(edge2)) > 0 &&
                    simMat.getVal(udG1.getEdgeTarget(edge1), udG2.getEdgeTarget(edge2)) > 0) {
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
    protected double getPS(Vector<Pair<E, E>> mappingEdges) {
        // parallel here there is no interference and no stateful lambda
        //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        AtomicReference<Double> PS = new AtomicReference<>((double) 0);
        mappingEdges.parallelStream().forEach(map -> {
            E edge1 = map.getFirst();
            E edge2 = map.getSecond();
            V n1_1 = udG1.getEdgeSource(edge1);
            V n1_2 = udG1.getEdgeTarget(edge1);
            V n2_1 = udG2.getEdgeSource(edge2);
            V n2_2 = udG2.getEdgeTarget(edge2);
            PS.updateAndGet(v -> v + simMat.getVal(n1_1, n2_1) + simMat.getVal(n1_2, n2_2));
        });
        return PS.get();
    }


    /**
     * @return set edge correctness and mapping edges[Pair:{graph1Source,graph1Target},{graph2Source,graph2Target}]
     */
    public Vector<Pair<E, E>> setEC(HashMap<V, V> mapping) {
        mappingEdges = new Vector<>();
        // toMap will decrease when nodes have been checked
        HashSet<V> toMap = new HashSet<>(mapping.keySet());
        AtomicInteger count = new AtomicInteger();
        for (Iterator<V> iterator = toMap.iterator(); iterator.hasNext(); ) {
            V n1 = iterator.next();
            V n1_ = mapping.get(n1);
            iterator.remove();
            Set<V> neighbors = udG1.getNeb(n1);
            // parallel here there is no interference and no stateful lambda
            //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
            // overlap -> one edge in graph1(contains n1 as one node)
            Collection<V> edge1s = neighbors.parallelStream().filter(toMap::contains).collect(Collectors.toList());
            if (edge1s.size() == 0) {
                continue;
            }
            // parallel here there is no interference and no stateful lambda
            //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
            // check graph2 -> have the corresponding "edge"
            edge1s.parallelStream().forEach(n2 -> {
                V n2_ = mapping.get(n2);
                if (udG2.getNeb(n1_).contains(n2_)) {
                    count.getAndIncrement();
                    mappingEdges.add(new Pair<>(udG1.getEdge(n1, n2), udG2.getEdge(n1_, n2_)));
                }
            });
        }
        EC = (double) count.get() / udG1.edgeSet().size();
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
            StatisticsMatrix s1 = stackMat.get(1);
            StatisticsMatrix s = stackMat.peek();
            // remove bottom which is the oldest for every iteration
            StatisticsMatrix s2 = stackMat.remove(0);

            double score = stackScore.peek();
            double score1 = stackScore.get(1);
            double score2 = stackScore.remove(0);

            double dif_1 = s.minus(s1).elementMaxAbs();
            double dif_2 = s.minus(s2).elementMaxAbs();
            logInfo("Iteration:" + iterCount + "\tdif_1 " + dif_1 + "\t" + "dif_2 " + dif_2 + "\nScore:" + "score1 " + score1
                    + "\t" + "score2 " + score2);
            return dif_1 < tolerance || dif_2 < tolerance ||
                    (score == score1 && score1 == score2);

        }
        // size = 2
        else if (stackMat.size() == 2) {
            StatisticsMatrix s = stackMat.peek();
            double dif = s.minus(stackMat.get(0)).elementMaxAbs();
            logInfo("Iteration:" + iterCount + "\tdif " + dif);
            return dif < tolerance;
        }
        //size = 1
        return false;
    }

    public void run() {
        if (debugOut) {
            cleanDebugResult();
        }
        logInfo("Init mapping...");
        Pair<HashMap<V, V>, SimMat<V>> init = getRemapForForced();
        // iterate
        hgaIterate(this.mapping, this.simMat, init.getSecond(), init.getFirst()
                , iterCount, score, PE, EC, PS, ES);

    }


    private void hgaIterate(HashMap<V, V> mapping, SimMat<V> simMat,
                            SimMat<V> toRemap, HashMap<V, V> forcedPart, int iterCount, double... scores) {
        initScores(scores);
        score_res = score;
        this.mapping = mapping;
        this.simMat = simMat;
        this.iterCount = iterCount;

        stackMat = new Stack<>();
        stackScore = new Stack<>();
        // only select once for rows which have more than 5 non-zero items
        this.mapping = initMapping(toRemap, forcedPart, splitLimit);

        boolean checkPassed;
        do {
            // log if needed
            logInfo("------------Iteration " + this.iterCount + "/1000------------");
            // step 1 score the mapping
            scoreMapping(this.mapping);
            // record
            stackMat.push(this.simMat.getMat().copy());
            stackScore.push(score);
            outDebug();
            // step 2 update based on mapped nodes
            updatePairNeighbors(this.mapping);
            // step 3 topo adjustment to similarity matrix
            addAllTopology();
            this.iterCount++;
            // record best
            if (score > score_res) {
                setUpResult();
            }
            checkPassed = checkPassed(tolerance);
            // step 4 map again using the updated H matrix,
            this.mapping = remap(forcedPart);

        } while (!checkPassed);
        // output result
        logInfo("HGA mapping finish!With iteration " + this.iterCount + " times.");
        outPutResult();

    }

    /**
     * Hungarian takes priority, and the left columns will be allocated by Greedy mapping
     *
     * @return full mapping result
     */
    protected HashMap<V, V> remap(HashMap<V, V> forced) {
        logInfo("Remap based on the updated matrix.");
        // G matrix -> left rows
        SimMat<V> Gmatrix = simMat.getPart(simMat.getHungRowsLeft(), simMat.getColSet());
        // H matrix
        SimMat<V> Hmatrix = simMat.getPart(simMat.getHungRows(), simMat.getColSet());

        // hungarian
        HashMap<V, V> mapping = getMappingFromHA(Hmatrix);
        // result
        HashMap<V,V> res = new HashMap<>(mapping);

        greedyMap(Gmatrix, mapping);

        // not forced
        if (forced == null) {
            return res;
        }
        // full mapping
        res.putAll(forced);
        return res;
    }


    protected Pair<HashMap<V, V>, SimMat<V>> getRemapForForced() {
        // row to map
        Set<V> rowToMap = simMat.getRowSet();
        rowToMap.removeAll(simMat.getColSet());
        // col to map
        Set<V> colToMap = simMat.getColSet();
        colToMap.removeAll(simMat.getRowSet());
        SimMat<V> remap = simMat.getPart(rowToMap, colToMap);
        HashMap<V, V> forceMap = null;
        if (forcedMappingForSame) {
            // set up force mapping
            HashSet<V> sameNodes = simMat.getRowSet();
            sameNodes.retainAll(simMat.getColSet());
            forceMap = new HashMap<>();
            HashMap<V, V> finalForceMap1 = forceMap;
            sameNodes.parallelStream().forEach(n -> finalForceMap1.put(n, n));
        }
        return new Pair<>(forceMap, remap);
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

    public void outPutMatrix(StatisticsMatrix mat, boolean isResult) {
        logInfo("output matrix");
        String path = debugOutputPath + "matrix/";
        Vector<String> matrixVec = new Vector<>();
        for (int i = 0; i < mat.numRows(); i++) {
            for (int j = 0; j < mat.numCols(); j++) {
                matrixVec.add(mat.get(i, j) + " ");
            }
        }
        try {
            if (isResult) {
                writer.setPath(path + "matrixResult_" + iter_res + ".txt");
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
        String path = debugOutputPath + "scoring/";
        Vector<String> scoreVec = new Vector<>();

        scoreVec.add("Iteration: " + iterCount + "\n");
        scoreVec.add("Score: " + scores[0] + "\n");
        scoreVec.add("PE: " + scores[1] + "\n");
        scoreVec.add("EC: " + scores[2] + "\n");
        scoreVec.add("ES: " + scores[3] + "\n");
        scoreVec.add("PS: " + scores[4] + "\n");
        try {
            if (isResult) {
                writer.setPath(path + "scoringResult_" + iter_res + ".txt");
            } else {
                writer.setPath(path + "scoring_" + iterCount + ".txt");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write(scoreVec, false);
    }

    public void outPutMapping(HashMap<V, V> mapping, boolean isResult) {
        logInfo("output mapping");
        String path = debugOutputPath + "mapping/";
        Vector<String> mappingVec = new Vector<>();
        mapping.forEach((k, v) -> mappingVec.add(k + "->" + v + "\n"));
        try {
            if (isResult) {
                writer.setPath(path + "mappingResult_" + iter_res + ".txt");
            } else {
                writer.setPath(path + "mapping_" + iterCount + ".txt");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write(mappingVec, false);
    }

    void cleanDebugResult() {
        debugOutputPath = System.getProperty("user.dir").replace('/', '\\') + "\\" + debugOutputPath;
        // use '\' to fit with linux
        debugOutputPath = debugOutputPath.replace('\\', '/');
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
        iter_res = iterCount;

        score_res = score;
        mappingResult = new HashMap<>(mapping);
        matrix_res = simMat.getMat().copy();
    }

    private void initScores(double... scores) {
        score = scores[0];
        PE = scores[1];
        EC = scores[2];
        ES = scores[3];
        PS = scores[4];
    }

    public void outPutResult() {
        if (debugOut) {
            outPutMapping(mappingResult, true);
            outPutMatrix(matrix_res, true);
            outPutScoring(true, score_res, PE_res, EC_res, ES_res, PS_res);
        }
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

    public StatisticsMatrix getMatrix_res() {
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

    public double getScore() {
        return score;
    }

    public int getIter_res() {
        return iter_res;
    }

    public HashMap<V, V> getMapping() {
        return mapping;
    }

    public HashMap<V, V> getMappingResult() {
        return mappingResult;
    }

    public void setUdG1(Graph<V, E> udG1) {
        this.udG1 = udG1;
    }

    public void setUdG2(Graph<V, E> udG2) {
        this.udG2 = udG2;
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

    public int getNON_ZERO_ROW_() {
        return NON_ZERO_ROW_;
    }


    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public Vector<Pair<E, E>> getMappingEdges() {
        return mappingEdges;
    }

    public void setupLogger() throws IOException {
        logger = Logger.getLogger("MyLog");
        FileHandler fh;
        fh = new FileHandler("HGALog.txt");
        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
        // output matrix, scoring and mapping result
        writer = new AbstractFileWriter() {
            @Override
            public void write(Vector<String> context, boolean closed) {
                super.write(context, false);
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
