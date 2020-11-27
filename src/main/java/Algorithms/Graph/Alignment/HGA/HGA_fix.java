package Algorithms.Graph.Alignment.HGA;

import DS.Matrix.SimMat;
import DS.Network.UndirectedGraph;

import java.io.IOException;

/**
 * The difference between the fix version with previous one is the mapping strategy,
 * as HGA_fix only update selected nodes in index network(matrix rows).The criteria is how many non_zero entries
 * in the rows of similarity matrix. Say, when non_zero items > m, m is an integer value, then the row will be sent to
 * the Hungarian matrix and the Hungarian matrix would not change, which means Hungarian allocation will always be implemented
 * in those selected rows. In the contrary, HGA would select the rows with high average similarity score so the hungarian matrix would always
 * filled with top ranking rows.
 * @param <V>
 * @param <E>
 */
public class HGA_fix<V,E>{

    public HGA_fix(SimMat<V> simMat, UndirectedGraph<V, E> udG1, UndirectedGraph<V, E> udG2, double nodalFactor, boolean forcedMappingForSame, double hAccount, double tolerance) throws IOException {

    }
}
