package Algorithms.Graph.Dynamic;

import DS.Network.Graph;

/**
 * This abstract class is written for the analysis of dynamic changes, especially the morphological characteristics
 * such as size, shape and etc.
 *
 * @Author Haotian Bai
 * @Email bht98@i.shu.edu.cn
 * @Blog www.haotian.life
 */
public abstract class Scale<V, E> {
    protected final Graph<V, E> srcG;
    protected final Graph<V, E> tgtG;
    protected Graph<V,E> periphery;
    protected int srcSize;
    protected int tgtSize;

    /**
     * @param srcG the graph to be handled
     * @param tgtG the connections needed to do the dynamic changes, normally a large graph
     *             that contains @param srcG as its subgraph
     */
    public Scale(Graph<V, E> srcG, Graph<V, E> tgtG) {
        this.srcG = srcG;
        this.tgtG = tgtG;
        // initialize sizes
        this.srcSize = srcG.vertexSet().size();
        this.tgtSize = tgtG.vertexSet().size();
    }

    /**
     * This propagate function is to scale the network by steps, which means
     * It accept an integer which denotes how long the peripheral nodes should
     * step out, when a positive integer is accepted, its scale become larger, and
     * on the other hand, a negative will shrink it.
     * <p>
     * The mechanism of action beneath the algorithm is Bellman-Ford and graph theory
     * analysis of peripheral problems solved by Chartrand G., Johns G., Oellermann O.R.
     * (1990) On Peripheral Vertices in Graphs. https://doi.org/10.1007/978-3-642-46908-4_22
     *
     * time complexity : O(|E|∗|V|)
     * pseudocode :
     * (get the maximum shortest path for every vertex)
     * 	for each u in V do
     * 		Bellman-Ford(v)
     * 		Let the maximum shortest path be max(d)
     * 		e(v)←max(d)
     * @param step an integer to denote the distance to propagate the network
     * @return a bool value to indicate whether the process should continue
     */
    //TODO
    private boolean propagate(int step) {
        boolean change = false;


        if(step == 0){
            return change;
        }

        return change;
    }

}
