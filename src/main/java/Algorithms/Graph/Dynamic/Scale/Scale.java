package Algorithms.Graph.Dynamic.Scale;

import Algorithms.Graph.Dynamic.Search.Path;
import DS.Network.Graph;
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
     * It accept a boolean which denotes forward or backward.
     * <p>
     * The mechanism of action beneath the algorithm is graph theory and Bell
     * analysis of peripheral problems solved by Chartrand G., Johns G., Oellermann O.R.
     * (1990) On Peripheral Vertices in Graphs. https://doi.org/10.1007/978-3-642-46908-4_22
     * <p>
     * time complexity : O(|E|*log|V|*|V|)
     */
    protected void propagate(boolean forward) {

        // get the maximum shortest path(length->topology) for every vertex in G
        Path<V,E> path = new Path<>();
        Map<V, Integer> max_sp = path.getLongestShortPath(resG);
        // periphery
        Set<V> periphery = getPeriphery(max_sp);
        // propagate
        if (forward) {
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
        }
        else{
            // delete nodes
            periphery.forEach(resG::removeVertex);
        }
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


    public Graph<V, E> getResG() {
        return resG;
    }
}