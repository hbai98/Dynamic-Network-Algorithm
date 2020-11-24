package Algorithms.Graph.Dynamic.Search;

import DS.Network.Graph;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BFS<V,E> {

    private final Graph<V, E> graph;

    // status
    enum Status {Init, Discovered, Finished}

    public BFS(Graph<V,E> graph){
        this.graph = graph;
        // set status: init
        Map<V, Status> statusMap = new HashMap<>();
        graph.vertexSet().parallelStream().forEach(v -> statusMap.put(v, Status.Init));
    }
//    /**
//     * dfs_longest from the source node and return the longest distance from the source
//     * Time Complexity: O(|E|)
//     *
//     * @param source    source node in Vertex type
//     * @param statusMap a map that contains all nodes' status
//     * @return the longest path regarding the source through the source graph
//     */
//    private double dfs_longest(V source, Map<V, Status> statusMap) {
//        AtomicReference<Double> result = new AtomicReference<>((double) 0);
//        // discover
//        statusMap.put(source, Status.Discovered);
//        // iterate adjacent vertexes => deep search
//        graph.getNeb(source).forEach(v -> {
//            // undiscovered nodes => continue
//            if (statusMap.get(v).equals(Status.Init)) {
//                double res = dfs_longest(v, statusMap) + 1;
//                if(res > result.get()) result.set(res);
//            }
//        });
//        // all nodes have been discovered
//        statusMap.put(source, Status.Finished);
//        return result.get();
//    }
}
