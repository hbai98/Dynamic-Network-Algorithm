package Algorithms.Graph.Utils;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Tools {

    static <K, V extends Comparable<? super V>>
    SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                Comparator.comparing(Map.Entry::getValue)
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
