package Algorithms.Graph.Dynamic;

import DS.Network.Graph;
import com.google.common.primitives.Booleans;
import org.apache.commons.lang3.ArrayUtils;
import org.jblas.FloatMatrix;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * DK stands for Graph Diffusion Kernel, which is an algorithm aimed to
 * predict novel genetic interactions and co-complex membership.
 *
 * Please refer to the passage bellow for more information:
 * Finding friends and enemies in an enemies-only network: A graph diffusion kernel for predicting
 * novel genetic interactions and co-complex membership from yeast genetic interactions
 * DOI:10.1101/gr.077693.108
 *
 * @Author Haotian Bai
 * @Email bht98@i.shu.edu.cn
 * @Blog www.haotian.life
 */
public class DK<V,E> extends Scale<V,E>{
    // algorithm's params
    private FloatMatrix adjMat; // adjacent matrix of the Graph tgtG
    private FloatMatrix query; // a boolean vector to indicate query nodes(introducing the flow)
    private final float loss; // loss fluid within each node
    private FloatMatrix dia; //a diagonal matrix with Sii which is the degree of node i ∈ V

    // internal

    /**
     * @param srcG the graph to be handled
     * @param tgtG the connections needed to do the dynamic changes, normally a large graph
     * @param loss a constant non-zero value which means the fluid loss out of each node,
     *             larger loss leads to faster loss, hence short diffusive paths
     *
     */
    public DK(Graph<V, E> srcG, Graph<V, E> tgtG,float loss) {
        super(srcG, tgtG);
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
        int s = tgtN.size();
        float[] diaArray = new float[s*s];
        AtomicInteger i = new AtomicInteger();
        tgtN.forEach(t->{
            int i_ = i.get();
            diaArray[i_*s+i_] = tgtG.degreeOf(t);
            i.incrementAndGet();
        });
        dia = new FloatMatrix(diaArray);
    }

    /**
     * Set up a boolean vector to indicate query nodes(introducing the flow) and assign
     * true for them.
     */
    private void initQry() {
        Set<V> srcN = srcG.vertexSet();
        Set<V> tgtN = tgtG.vertexSet();
        float[] queryArray = new float[srcN.size()];
        AtomicInteger i = new AtomicInteger();
        srcN.forEach(s->{
            if(tgtN.contains(s)){
                queryArray[i.get()] = 1;
            }
            i.incrementAndGet();
        });
        query = new FloatMatrix(queryArray);
    }

    /**
     * Prepare the adjacent matrix using boolean to denote whether
     * A(i,j) is connected.
     *
     * <p>Notice:</p>
     * <p>
     *    the one dimensional array is organized by
     *    j*rows + i
     * </p>
     */
    private void initAdj() {
        Function<? super Boolean, ?> boolToFloat = (Function<Boolean, Object>) aBoolean -> aBoolean ? 1. : 0.;
        float[] adjArray = ArrayUtils.toPrimitive(Booleans.asList(tgtG.getAdjMat()).stream()
                .map(boolToFloat).toArray(Float[]::new));
        this.adjMat = new FloatMatrix(adjArray);
    }

    /**
     * Public interface for users to run Diffusion Kernel
     *
     */
    public void run(){
       // G is defined
       FloatMatrix G = getG();
    }

    private FloatMatrix getG() {
        // compute self-item G0
        FloatMatrix G0 = getSelfItem();
        return;
    }

    /**
     * as defined in the paper a linear expression of the self-item to compute the equilibrium status of the diffusion
     * by (dia + loss*I)^(-1)
     *
     * G0 is the inverse of a diagonal matrix and hence trivial to calculate.
     * @return self-item
     */
    private FloatMatrix getSelfItem() {
        return getIdMat().div(dia.add(getIdMat().mul(loss)));
    }

    /**
     * Get a identity matrix I based on the target graph
     * @return a new allocated identity matrix
     */
    private FloatMatrix getIdMat() {
        return FloatMatrix.eye(tgtG.vertexSet().size());
    }


}
