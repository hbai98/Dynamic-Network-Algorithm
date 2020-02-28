package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.Edge;
import org.jgrapht.alg.util.Pair;

import java.util.HashSet;

public class PairedEdges extends HashSet<Pair<Edge,Edge>> {
    public PairedEdges(){

    }
    @Override
    public boolean add(Pair<Edge, Edge> edgePair) {
        return super.add(edgePair);
    }
}
