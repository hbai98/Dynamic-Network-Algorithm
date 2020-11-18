package Algorithms.Graph.Dynamic.Scale;

import DS.Network.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultEdge;

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
     * <p>
     * time complexity : O(|E|∗|V|)
     * <p>
     * pseudocode :
     * <p>
     *	(get the maximum shortest path for every vertex in G)
     * 	[Greedy algorithm => DFS]
     * 	for v in V do
     * 		DFS(v)
     * 		Let  the maximum shortest path be max(d)
     * 		e(v)←max(d)
     *
     * 	let P(G) be the induced subgraph with vertexes whose e(v) equals diameter<p>
     * <p>
     * <p>
     * 	(propagate)<p>
     * 	let N(u) be the immediate neighbors of vertex u, forward(v) be {v` | v`∈N(u) & d(v`) > d(v)}, backward(v) be {v` | v`∈N(u) & d(v`)< d(v)}<p>
     * 	let set store new nodes in the next P(G)<p>
     * 	<p>
     * 	(forward)
     * 	if(s >= 0)<p>
     * 		for 1 to step do<p>
     * 			for each v in P(G) do<p>
     * 				add forward(v) to set<p>
     * 				add forward(v) and edges to G<p>
     * 			P(G)←set<p>
     * 			clean set<p>
     * 	(backward)<p>
     * 	else<p>
     * 		for 1 to |step| do<p>
     * 			for each v in P(G) do<p>
     * 				add backward(v) to set<p>
     * 				delete backward(v) and edges in G<p>
     * 			P(G)←set<p>
     * 			clean set<p>
     * <p>
     * <p>
     * 	return whether P(G) has changed
     * @param step an integer to denote the distance to propagate the network
     * @return a bool value to indicate whether the process should continue
     */
    private boolean propagate(int step) {
        boolean change = false;
        if(step == 0){
            return change;
        }
        // get the maximum shortest path for every vertex in G
        srcG.vertexSet().forEach(v->{
            int longest = dfs(v);
        });

        return change;
    }

    private int dfs(V v) {
        // longest path
        int res = 0;
        // set status: 0->init; 1->discovered; 2->finished

        return 0;
    }
}