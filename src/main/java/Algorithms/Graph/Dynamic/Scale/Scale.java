package Algorithms.Graph.Dynamic.Scale;

import DS.Network.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.*;

/**
 * This abstract class is written for the analysis of dynamic changes, especially the morphological characteristics
 * such as size, shape and etc.
 *
 * @Author Haotian Bai
 * @Email bht98@i.shu.edu.cn
 * @Blog www.haotian.life
 */
public class Scale<V, E> {
    protected final Graph<V, E> tgtG;
    protected Graph<V, E> resG;

    /**
     * @param nodes nodes to propagate
     * @param tgtG  the connections needed to do the dynamic changes, normally a large graph
     *              that contains @param nodes as its subgraph
     */
    public Scale(Set<V> nodes, Graph<V, E> tgtG) {
        this.tgtG = tgtG;
        // extract subgraph from the target network based on give nodes
        this.resG = tgtG.getSub(nodes);
    }


    /**
     * This propagate function is to scale the network by steps, which means
     * It accept an integer which denotes how long the peripheral nodes should
     * step out, when a positive integer is accepted, its scale become larger, and
     * on the other hand, a negative will shrink it.
     * <p>
     * The mechanism of action beneath the algorithm is graph theory and Bell
     * analysis of peripheral problems solved by Chartrand G., Johns G., Oellermann O.R.
     * (1990) On Peripheral Vertices in Graphs. https://doi.org/10.1007/978-3-642-46908-4_22
     * <p>
     * time complexity : O(|E|∗|V|)
     * <p>
     * pseudocode :
     * <p>
     * (get the maximum shortest path for every vertex in G)
     * [Greedy algorithm => DFS]
     * for v in V do
     * Dijkstra's(v)
     * Let  the maximum shortest path be max(d)
     * e(v)←max(d)
     * <p>
     * let P(G) be the induced subgraph with vertexes whose e(v) equals diameter<p>
     * <p>
     * <p>
     * (propagate)<p>
     * let N(u) be the immediate neighbors of vertex u, forward(v) be {v` | v`∈N(u) & d(v`) > d(v)}, backward(v) be {v` | v`∈N(u) & d(v`)< d(v)}<p>
     * let set store new nodes in the next P(G)<p>
     * <p>
     * (forward)
     * if(s >= 0)<p>
     * for 1 to step do<p>
     * for each v in P(G) do<p>
     * add forward(v) to set<p>
     * add forward(v) and edges to G<p>
     * P(G)←set<p>
     * clean set<p>
     * (backward)<p>
     * else<p>
     * for 1 to |step| do<p>
     * for each v in P(G) do<p>
     * add backward(v) to set<p>
     * delete backward(v) and edges in G<p>
     * P(G)←set<p>
     * clean set<p>
     * <p>
     * <p>
     * return whether P(G) has changed
     *
     * @param step an integer to denote the distance to propagate the network
     * @return a bool value to indicate whether the process should continue
     */
    protected boolean propagate(int step) {
        boolean change = false;

        if (step == 0) {
            return change;
        }
        // get the maximum shortest path(length->topology) for every vertex in G
        Map<V, Integer> max_sp = getMapForSP();
        // periphery
        Set<V> periphery = getPeriphery(max_sp);
        // propagate
        if (step > 0) {
            periphery.forEach(v -> {
                // unvisited neighbors
                tgtG.getNeb(v).forEach(resG::addVertex);
            });
            // add edges
            periphery.forEach(v-> tgtG.edgesOf(v).forEach(e->{
                V source = tgtG.getEdgeSource(e);
                V target = tgtG.getEdgeTarget(e);
                if(tgtG.containsVertex(source)&&tgtG.containsVertex(target)){
                    resG.addEdge(source, target);
                }
            }));
            // update periphery
            
        }
        else{

        }
        return change;
    }

    private Set<V> getPeriphery(Map<V, Integer> max_sp) {
        Optional<Map.Entry<V, Integer>> max = max_sp.entrySet().stream().max(Map.Entry.comparingByValue());
        int maxSp = max.get().getValue();
        // result
        Set<V> res = new HashSet<>();
        // iterate again
        max_sp.entrySet().stream().forEach(e -> {
            if (e.getValue() == maxSp) {
                res.add(e.getKey());
            }
        });
        return res;
    }

    private Map<V, Integer> getMapForSP() {
        int longest = 0;
        HashMap<V, Integer> res = new HashMap<>();

        for (V v : resG.vertexSet()) {
            DijkstraShortestPath<V, E> dijkstraAlg = new DijkstraShortestPath<>(resG);
            // find the max path
            ShortestPathAlgorithm.SingleSourcePaths<V, E> path = dijkstraAlg.getPaths(v);
            Set<V> others = new HashSet<>(resG.vertexSet());
            others.remove(v);
            // propagate by length
            for (V o : others) {
                int l = path.getPath(o).getLength();
                if (l > longest) {
                    longest = l;
                }
            }
            // record the longest for each node
            res.put(v, longest);
            // refresh longest
            longest = 0;
        }
        return res;
    }


}