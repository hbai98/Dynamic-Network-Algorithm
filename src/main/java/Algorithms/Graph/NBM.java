package Algorithms.Graph;


<<<<<<< Updated upstream:src/main/java/Algorithms/Graph/NBM.java
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHashSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.AdjList.Graph;
import Algorithms.Graph.Utils.SimMat;
import org.jgrapht.alg.util.Triple;
=======
import Internal.Algorithms.DS.Network.SimMat;
import Internal.Algorithms.DS.Network.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
>>>>>>> Stashed changes:src/main/java/Internal/Algorithms/Graph/NBM.java

import java.util.*;
import java.util.stream.Collectors;

/**
 * H. He and A. K.Singh, “Closure-Tree: An index structure for graph
 * queries,” in ICDE’06: Proceedings of the 22nd International Conference
 * on Data Engineering. Washington, DC, USA: IEEE Computer Society,
 * 2006, p. 38, doi: http://dx.doi.org/10.1109/ICDE.2006.37
 * <p>
 * NBM
 *
 * @author Haotian Bai
 * Shanghai University, department of computer science
 * Time complexity:
 * <p>Let n be the number of vertices and d be the
 * maximum degree of vertices. The initial computation of
 * matrix W and insertions into the priority queue take O(n2)
 * time, assuming uniform distance measures. In each iteration,
 * the algorithm removes one pair from and inserts at
 * most d2 unmatched pairs into the priority queue. Totally,
 * there are O(n) iterations. Thus, the time complexity is O(n*d^2*logn).
 * </p>
 * <br>
 * <p>---------------------------------------------------------------------------</p>
 * <br>
 * <p>In order to find common substructures, we develop a new
 * graph mapping method called Neighbor Biased Mapping
 * (NBM) shown in Alg. 1. Initially, a weight matrix W is
 * computed where each entry Wu,v represents the similarity
 * of vertex u ∈ G1 and vertex v ∈ G2. A priority queue PQ
 * maintains pairs of vertices according to their weights. For
 * each vertex in G1, its most similar vertex is found in G2,
 * and the pair is added to PQ. At each iteration, the best pair
 * (u, v) of unmatched vertices in the priority queue is chosen
 * and marked as matched. Then, the neighboring unmatched
 * pairs of (u, v) are assigned higher weights, thus increasing
 * their chance of being chosen. The iterations continue until
 * all vertices in graph G1 have been matched.
 * </p>
 */
<<<<<<< Updated upstream:src/main/java/Algorithms/Graph/NBM.java
public class NBM {
    protected SimMat simList;
    // --------------------------------------------> alg variables
    protected HashMap<String, Node> mostSimPairMap;
    protected PriorityQueue<Edge> pqEdge;
    protected EdgeHashSet preMap;
    protected Graph graph1;
    protected Graph graph2;
=======
public class NBM<V, E> {
>>>>>>> Stashed changes:src/main/java/Internal/Algorithms/Graph/NBM.java

    private final UndirectedGraph<V, E> udG1;
    private final UndirectedGraph<V, E> udG2;
    private final HashMap<V, V> mapping;
    private SimMat<V> simMat;

    public NBM(UndirectedGraph<V, E> udG1,
               UndirectedGraph<V, E> udG2,
               SimMat<V> simMat,
               HashMap<V, V> mapping
    ) {
        this.udG1 = udG1;
        this.udG2 = udG2;
        this.simMat = simMat;
        this.mapping = mapping;
    }

    /**
     * Update only once for all neighbors of all the pairs ready.
     * reward is defined in HGA.
     * Notice : this method return the result associated with the order mapping is iterated.
     */
<<<<<<< Updated upstream:src/main/java/Algorithms/Graph/NBM.java
    public static void neighborSimAdjust(Graph graph1, Graph graph2, SimMat simMat, HashMap<String, String> mapping) {
        // distinguish the result simMat from the indexing one
        HashMap<String, HashSet<String>> neb1Map = graph1.getNeighborsMap();
        HashMap<String, HashSet<String>> neb2Map = graph2.getNeighborsMap();
=======
    public void neighborSimAdjust() {
>>>>>>> Stashed changes:src/main/java/Internal/Algorithms/Graph/NBM.java
        // sort the mapping pairs to add topological effect for the pair with higher similarity first,
        // and it will alleviate the impact brought by update similarity matrix in various orders.
        List<Map.Entry<V, V>> toAdjust = mapping.entrySet().stream()
                .sorted(Comparator.comparing(entry ->
                        simMat.getVal(entry.getKey(), entry.getValue()))).collect(Collectors.toList());
        Collections.reverse(toAdjust);
        // no parallel here
        for (Map.Entry<V, V> entry : toAdjust) {
            V node1 = entry.getKey();
            V node2 = entry.getValue();
            double simUV = simMat.getVal(node1, node2);
            // direct neighbors of the head node
<<<<<<< Updated upstream:src/main/java/Algorithms/Graph/NBM.java
            HashSet<String> neb1 = neb1Map.get(node1);
            HashSet<String> neb2 = neb2Map.get(node2);
            // no parallel here! stateful lambda
            neb1.forEach(s1 -> {
                int nebNumbNode1 = neb1Map.get(s1).size();
=======
            Set<V> neb1 = udG1.getNeb(node1);
            Set<V> neb2 = udG2.getNeb(node2);
            // no parallel here! stateful lambda
            neb1.forEach(n1 -> {
                int nebNumbNode1 = udG1.getNebNum(n1);
>>>>>>> Stashed changes:src/main/java/Internal/Algorithms/Graph/NBM.java
                double reward = simUV / nebNumbNode1;
                neb2.forEach(n2 -> {
                    double newWeight = simMat.getVal(n1, n2) + reward;
                    simMat.put(n1, n2, newWeight);
                });
            });
        }
    }


//    ---------------------------------PUBLIC-------------------------
//
//    public HashMap<V, Node> getMostSimPairMap() {
//        assert (mostSimPairMap != null);
//        return mostSimPairMap;
//    }
//
//    public EdgeHashSet getMappedEdges() {
//        assert (mappedEdges != null);
//        return mappedEdges;
//    }
}

