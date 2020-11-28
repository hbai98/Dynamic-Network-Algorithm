package Algorithms.Graph.Alignment.HGA;

import DS.Matrix.SimMat;
import DS.Network.UndirectedGraph;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

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
     * The closer possible drug targets to disease proteins, the higher the score will be.
     * SP = 1/d<CT>
     *
     * d<CT> = Sum(sp-> c:t + t:c)/ (||C||+||T||)
     * @param alignSet possible drug targets for treatment of new diseases
     * @return SP
     */
    private double getSP(Collection<V> alignSet) {
        
        return 0;
    }
}
