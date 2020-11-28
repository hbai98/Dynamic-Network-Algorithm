package Algorithms.Graph.Alignment.HGA;

import DS.Matrix.SimMat;
import DS.Matrix.StatisticsMatrix;
import DS.Network.Graph;
import DS.Network.UndirectedGraph;
import IO.Writer.AbstractFileWriter;
import org.jgrapht.alg.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * The difference between the fix version with previous one is the mapping strategy,
 * as HGA_fix only update selected nodes in index network(matrix rows).The criteria is how many non_zero entries
 * in the rows of similarity matrix. Say, when non_zero items > m, m is an integer value, then the row will be sent to
 * the Hungarian matrix and the Hungarian matrix would not change, which means Hungarian allocation will always be implemented
 * in those selected rows. In the contrary, HGA would select the rows with high average similarity score so the hungarian matrix would always
 * filled with top ranking rows.
 *
 * @param <V>
 * @param <E>
 */
public class HGA_fix<V, E> extends HGA<V, E> {
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
     * @param tolerance            the limit to check whether the matrix has converged
     */
    public HGA_fix(SimMat<V> simMat,
                   UndirectedGraph<V, E> udG1,
                   UndirectedGraph<V, E> udG2,
                   double nodalFactor, boolean forcedMappingForSame, double tolerance) throws IOException {
        super(simMat, udG1, udG2, nodalFactor, forcedMappingForSame, 0, tolerance);
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
            HashMap<V, V> mapping = getMappingFromHA(toMap);
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
        HashMap<V, V> res = new HashMap<>(mapping);

        greedyMap(Gmatrix, mapping);

        // not forced
        if (forced == null) {
            return res;
        }
        // full mapping
        res.putAll(forced);
        return res;
    }

    public int getNON_ZERO_ROW_() {
        return NON_ZERO_ROW_;
    }


}
