package Algorithms.Graph.Dynamic;

import DS.Network.Graph;

/**
 * This class is for dynamic changes, especially the morphological characteristics
 * such as size, shape and etc.
 *
 * @Author Haotian Bai
 * @Email bht98@i.shu.edu.cn
 * @Blog haotian.life
 */
public class Scale<V,E> {
    protected final Graph<V, E> srcG;
    protected final Graph<V, E> tgtG;

    /**
     *
     * @param srcG the graph to be handled
     * @param tgtG the connections needed to do the dynamic changes
     */
    public Scale(Graph<V,E> srcG,Graph<V,E> tgtG){
        this.srcG = srcG;
        this.tgtG = tgtG;
    }
    /**
     *  This propagate function is to scale the network by steps, which means
     *  It accept an integer which denotes how long the peripheral nodes should
     *  step out, when a positive integer is accepted, its scale become larger, and
     *  on the other hand, a negative will shrink it.
     *
     *  The mechanism of action beneath the algorithm is Bellman-Ford and graph theory
     *  analysis of peripheral problems solved by Chartrand G., Johns G., Oellermann O.R.
     *  (1990) On Peripheral Vertices in Graphs. https://doi.org/10.1007/978-3-642-46908-4_22
     *
     * @param step
     */
    private boolean propagate(int step){
       boolean change = false;

       return change;
    }

}
