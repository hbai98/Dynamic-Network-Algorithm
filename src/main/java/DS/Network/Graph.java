package DS.Network;

import java.util.Set;

public interface Graph<V,E> extends org.jgrapht.Graph<V,E> {
    /**
     * Get all neighbors for a vertex.
     * @param vertex the target vertex
     * @return a set of neighbor points
     */
    Set<V> getNeb(V vertex);

    /**
     * Get a boolean matrix to describe the graph connections,
     * and A(i,j) = true means Vi and Vj is connected.
     * @return a one-dimension boolean adjacent matrix
     */
    boolean[] getAdjMat();

}
