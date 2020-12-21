package Algorithms.Graph.Dynamic.Diffusion_Kernel;

import DS.Matrix.DenseMatrix;
import DS.Matrix.SparseMatrix;
import DS.Matrix.StatisticsMatrix;
import DS.Network.Graph;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.data.Matrix;
import org.ejml.sparse.csc.CommonOps_DSCC;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final Set<V> nodes; // nodes to propagate
    private final Graph<V, E> tgtG;
    private final int tgtSize;
    private Vector<V> tgtNodes; // vector of nodes to propagate

    // algorithm's params
    private StatisticsMatrix adjMat; // adjacent matrix of the Graph tgtG
    private StatisticsMatrix query; // a boolean vector to indicate query nodes(introducing the flow)
    private final double loss; // loss fluid within each node
    private StatisticsMatrix dia; //a diagonal matrix with Sii which is the degree of node i ∈ V
    private StatisticsMatrix result; // stable system
    private HashMap<V, Integer> nodesMap;

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
        this.tgtNodes = new Vector<>(tgtG.vertexSet());
        init();
    }

    /**
     * initialize other data in need.
     */
    private void init() {
        // get maps of nodes' names to indexes in the matrix
        initMap();
        // adjacent matrix
        initAdj();
        // query nodes vector
        initQry();
        // diagonal matrix
        initDia();
    }

    private void initMap() {
        this.nodesMap = new HashMap<>();
        for (int i = 0; i < tgtSize; i++) {
            nodesMap.put(tgtNodes.get(i), i);
        }
    }

    private void initDia() {
        Set<V> tgtN = tgtG.vertexSet();
        double[] data = new double[tgtSize];
        AtomicInteger i = new AtomicInteger(-1);
        tgtN.forEach(t -> data[i.addAndGet(1)] = tgtG.degreeOf(t));
        dia = SparseMatrix.createDia(data);
    }

    /**
     * Set up a vector to indicate query nodes(introducing the flow) and assign
     * true for them.
     */
    private void initQry() {

        // fill array
        double[] queryArray = new double[nodes.size()];
        Arrays.fill(queryArray, 1.);
        // check source
        // prepare col to store the source annotation
        Vector<Integer> rows = new Vector<>();
        for (int i = 0; i < tgtNodes.size(); i++) {
            V t = tgtNodes.get(i);
            if(nodes.contains(t)){
                rows.add(i);
            }
        }
        int[] columns = new int[nodes.size()];
        query = new SparseMatrix(tgtSize, 1,rows.stream().mapToInt(i->i).toArray(), columns, queryArray);
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
        adjMat = new SparseMatrix(tgtSize, tgtSize, 2*tgtG.edgeSet().size());
        tgtG.edgeSet().forEach(e->{
            V v1 = tgtG.getEdgeSource(e);
            V v2 = tgtG.getEdgeTarget(e);
            int i = nodesMap.get(v1);
            int j = nodesMap.get(v2);
            adjMat.set(i, j, 1.);
            adjMat.set(j, i, 1.);
        });
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
        SparseMatrix I = SparseMatrix.createIdentity(tgtSize);
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
        SparseMatrix I = SparseMatrix.createIdentity(tgtSize);
        DMatrixRMaj inverse = new DMatrixRMaj();
        StatisticsMatrix mat = I.minus(G0.mult(adjMat));
        // invert
        CommonOps_DSCC.invert(mat.getMatrix(), inverse);
        StatisticsMatrix mat_ = new StatisticsMatrix();
        mat_.setMat(inverse);
        return mat_.mult(G0);
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
