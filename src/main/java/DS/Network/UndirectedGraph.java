package DS.Network;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class UndirectedGraph<V, E> extends DefaultUndirectedWeightedGraph<V, E> implements Graph<V, E> {

    public UndirectedGraph(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    @Override
    public Set<V> getNeb(V vertex) {
        Set<E> edges = this.edgesOf(vertex);
        return edges.stream().map(e -> neb(vertex, e)).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean[] getAdjMat() {
        V[] nodes = (V[]) vertexSet().toArray();
        int s = nodes.length;
        boolean[] adjMat = new boolean[s * s];

        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (containsEdge(nodes[i], nodes[j])) {
                    adjMat[j * s + i] = true;
                }
            }
        }
        return adjMat;
    }

    private V neb(V v, E e) {
        V source = this.getEdgeSource(e);
        V target = this.getEdgeTarget(e);
        return source.equals(v) ? target : source;
    }

}
