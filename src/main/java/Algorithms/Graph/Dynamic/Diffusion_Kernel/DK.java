package Algorithms.Graph.Dynamic.Diffusion_Kernel;

import Algorithms.Graph.Dynamic.Scale.Scale;
import DS.Matrix.StatisticsMatrix;
import DS.Network.Graph;
import com.google.common.primitives.Booleans;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * DK stands for Graph Diffusion Kernel, which is an algorithm aimed to
 * predict novel genetic interactions and co-complex membership.
 * <p>
 * Please refer to the passage bellow for more information:
 * Finding friends and enemies in an enemies-only network: A graph diffusion kernel for predicting
 * novel genetic interactions and co-complex membership from yeast genetic interactions
 * DOI:10.1101/gr.077693.108
 *
 * @Author Haotian Bai
 * @Email bht98@i.shu.edu.cn
 * @Blog www.haotian.life
 */
public class DK<V, E> {
    private final Set<V> nodes;
    private final Graph<V, E> tgtG;
    private final int tgtSize;
    // algorithm's params
    private StatisticsMatrix adjMat; // adjacent matrix of the Graph tgtG
    private StatisticsMatrix query; // a boolean vector to indicate query nodes(introducing the flow)
    private final double loss; // loss fluid within each node
    private StatisticsMatrix dia; //a diagonal matrix with Sii which is the degree of node i ∈ V
    private StatisticsMatrix result; // stable system

    // internal

    /**
     * @param nodes nodes in the subgraph
     * @param tgtG the connections needed to do the dynamic changes, normally a large graph
     * @param loss a constant non-zero value which means the fluid loss out of each node,
     *             larger loss leads to faster loss, hence short diffusive paths
     */
    public DK(Set<V> nodes, Graph<V, E> tgtG, double loss) {
        this.nodes = nodes;
        this.tgtG = tgtG;
        this.tgtSize = tgtG.vertexSet().size();
        // initialize loss
        this.loss = loss;

        init();
    }

    /**
     * initialize other data in need.
     */
    private void init() {
        // adjacent matrix
        initAdj();
        // query nodes vector
        initQry();
        // diagonal matrix
        initDia();
    }

    private void initDia() {
        Set<V> tgtN = tgtG.vertexSet();
        double[] diaArray = new double[tgtSize * tgtSize];
        AtomicInteger i = new AtomicInteger();
        tgtN.forEach(t -> {
            int x = i.get();
            diaArray[x+tgtSize*x] = tgtG.degreeOf(t);
            i.incrementAndGet();
        });
        dia = new StatisticsMatrix(tgtSize, tgtSize, true, diaArray);
    }

    /**
     * Set up a vector to indicate query nodes(introducing the flow) and assign
     * true for them.
     */
    private void initQry() {
        Set<V> tgtN = tgtG.vertexSet();
        Vector<V> tgt = new Vector<>(tgtN);
        double[] queryArray = new double[tgtSize];

        for (int i = 0; i < tgt.size(); i++) {
            V t = tgt.get(i);
            if(nodes.contains(t)){
                queryArray[i] = 1.;
            }
        }

        query = new StatisticsMatrix(queryArray);
    }

    /**
     * Prepare the adjacent matrix using boolean to denote whether
     * A(i,j) is connected.
     *
     * <p>Notice:</p>
     * <p>
     * the one dimensional array is organized by
     * j*rows + i
     * </p>
     */
    private void initAdj() {
        Function<? super Boolean, ?> boolToFloat = (Function<Boolean, Object>) aBoolean -> aBoolean ? 1. : 0.;
        double[] adjArray = ArrayUtils.toPrimitive(Booleans.asList(tgtG.getAdjMat()).stream()
                .map(boolToFloat).toArray(Double[]::new));
        this.adjMat = new StatisticsMatrix(tgtSize, tgtSize, true, adjArray);
    }

    /**
     * Public interface for users to run Diffusion Kernel
     */
    public void run() {
        // G is kernel
        StatisticsMatrix G = getG();
        // compute the equilibrium state
        // set result
        result = getStableP(G);
    }
    /**
     * as defined in the paper a linear expression of the self-item to compute the equilibrium status of the diffusion
     * by (dia + loss*I)^(-1)
     * <p>
     * G0 is the inverse of a diagonal matrix and hence trivial to calculate.
     * @return self-item
     */
    private StatisticsMatrix getSelfItem() {
        StatisticsMatrix I = StatisticsMatrix.createIdentity(tgtSize);
        return dia.plus(I.scale(loss)).inverseDig();
    }
    /**
     * G = L^-1 = (I-G0A)^(-1)G0
     *
     * @return G matrix or G kernel
     */
    private StatisticsMatrix getG() {
        // compute self-item G0
        StatisticsMatrix G0 = getSelfItem();
        StatisticsMatrix I = StatisticsMatrix.createIdentity(tgtSize);
        return I.minus(G0.mult(adjMat)).invert().mult(G0);
    }
/**
 * P_ss = Kernel * query vector
 * @return the score vector for every node in the graph
 **/
    private StatisticsMatrix getStableP(StatisticsMatrix G) {
        return G.mult(query);
    }


    public StatisticsMatrix getResult() {
        return result;
    }
}
