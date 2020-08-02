package DS.Network;

import java.util.Set;

public interface Graph<V,E> {
    Set<V> getNeb(V vertex);

    int getNebNum(V vertex);
}
