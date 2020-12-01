package Algorithms.Graph.Alignment.HGA;

import DS.Matrix.SimMat;
import DS.Network.UndirectedGraph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Using shortest path in scoring
 * @param <V>
 * @param <E>
 */
public class HGA_sp<V,E> extends HGA<V,E>{
    /**
     * Step 1:
     * Initialize with the homologous coefficients of proteins
     * computed by alignment algorithms for PINs
     *
     * @param simMat               similarity matrix
     * @param udG1                 graph1 -> index
     * @param udG2                 graph2 -> target
     * @param nodalFactor          nodal compared with topological effect
     * @param forcedMappingForSame whether force mapping
     * @param hAccount             hungarian matrix account
     * @param tolerance            the limit to check whether the matrix has converged
     */
    public HGA_sp(SimMat<V> simMat, UndirectedGraph<V, E> udG1, UndirectedGraph<V, E> udG2, double nodalFactor, boolean forcedMappingForSame, double hAccount, double tolerance) throws IOException {
        super(simMat, udG1, udG2, nodalFactor, forcedMappingForSame, hAccount, tolerance);
    }

    @Override
    protected void scoreMapping(HashMap<V, V> mapping) {
        super.scoreMapping(mapping);
        score += getSP(mapping.values()) ;
    }

    /**
     * get shortest path score.
     * O(n^2 log n)
     * The closer possible drug targets to disease proteins, the higher the score will be.
     * SP = 1/d<CT>
     *
     * d<CT> = Sum(sp-> c:t + t:c)/ (||C||+||T||)
     * @param alignSet possible drug targets for treatment of new diseases
     * @return SP
     */
    private double getSP(Collection<V> alignSet) {
        DijkstraShortestPath<V, E> dijkstraAlg = new DijkstraShortestPath<>(this.target);
        AtomicInteger sum_ct = new AtomicInteger();
        AtomicInteger sum_tc = new AtomicInteger();

        AtomicInteger shortest = new AtomicInteger(Integer.MAX_VALUE);
        // c:t
        alignSet.forEach(n->{
            ShortestPathAlgorithm.SingleSourcePaths<V, E> path = dijkstraAlg.getPaths(n);
            target.vertexSet().forEach(v->{
                int l = path.getPath(v).getLength();
                if(l < shortest.get()){
                    shortest.set(l);
                }
            });
            sum_ct.addAndGet(shortest.get());
        });

        AtomicInteger shortest_ = new AtomicInteger(Integer.MAX_VALUE);
        // t:c
        target.vertexSet().forEach(n->{
            ShortestPathAlgorithm.SingleSourcePaths<V, E> path = dijkstraAlg.getPaths(n);
            alignSet.forEach(v-> {
                int l = path.getPath(v).getLength();
                if(l < shortest_.get()){
                    shortest_.set(l);
                }
            });
            sum_tc.addAndGet(shortest_.get());
        });

        // score
        return (sum_ct.get()+sum_tc.get())*1.0/ (alignSet.size()+target.vertexSet().size());
    }
}
