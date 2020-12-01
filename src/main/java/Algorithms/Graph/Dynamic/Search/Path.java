package Algorithms.Graph.Dynamic.Search;

import DS.Network.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Path<V,E> {
    public Path(){

    }

    // Below are algorithms considering topological distances which means
    // it will not take edges' weights into account

    /**
     * d(c, t) is the shortest distance between node c and t in the network.
     * It calculates the shortest path for each node in graph1 to graph2.
     * @param graph1 g1
     * @param graph2 g2
     * @return a map of all shortest paths for each node in graph1 to reach graph2
     */
    public Map<V,Integer> betweenSPMap(Graph<V,E> graph1, Graph<V,E> graph2){
        int shortest = 0;
        HashMap<V, Integer> res = new HashMap<>();

        
        return res;
    }

    /**
     * @param graph connected graph
     * @return a map of all nodes' longest shortest distances within the graph
     */
    public Map<V, Integer> getLongestShortPath(Graph<V, E> graph) {
        int longest = 0;
        HashMap<V, Integer> res = new HashMap<>();

        for (V v : graph.vertexSet()) {
            DijkstraShortestPath<V, E> dijkstraAlg = new DijkstraShortestPath<>(graph);
            // find the max path
            ShortestPathAlgorithm.SingleSourcePaths<V, E> path = dijkstraAlg.getPaths(v);
            Set<V> others = new HashSet<>(graph.vertexSet());
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
