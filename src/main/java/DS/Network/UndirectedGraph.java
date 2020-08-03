package DS.Network;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UndirectedGraph<V,E> extends DefaultUndirectedWeightedGraph<V,E> implements Graph<V,E> {

    public UndirectedGraph(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    @Override
    public Set<V> getNeb(V vertex) {
        Set<E> edges = this.edgesOf(vertex);
        return edges.stream().map(e -> neb(vertex, e)).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public int getNebNum(V vertex) {
        return this.edgesOf(vertex).size();
    }

    private V neb(V v,E e){
        V source = this.getEdgeSource(e);
        V target = this.getEdgeTarget(e);
        return source.equals(v)?target:source;
    }

}
