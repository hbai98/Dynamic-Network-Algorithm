package Algorithms.Graph.Network;
// Author: Haotian Bai
// Shanghai University, department of computer science

import Algorithms.Graph.IO.GraphFileReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class can be defined as HashSet， while the edge's weight will not get updated
 * if there's higher value --> so it would not be treated as a self-update graph.
 * Thus, it will allow 'same' edges with different weights.
 * <br>
 * <p>
 *     NOTICE: in order to update the edge with a higher weight, there is a method bellow.
 * </p>
 */
public class EdgeHasSet extends HashSet<Edge> {
    // file to store the edges
    protected String filename;

    /**
     * Add an edge if not existed
     *
     * @param edge edge to be added
     * @return true for success
     */
    @Override
    public boolean add(Edge edge) {
        // Note: if equals() has been overloaded for the edge comparision,
        // add(),remove(),contains() will all be changed.
        return super.add(edge);
    }

    public boolean add(String src, String tgt, double weight, Edge.Type type) {
        return add(new Edge(src, tgt, weight, type));
    }

    /**
     * Undirected edge by default
     */
    public boolean add(String src, String tgt, double weight) {
        return add(new Edge(src, tgt, weight));
    }

    /**
     * Find edges contains {@code} node
     *
     * @param node node to be found
     * @return hasSet with all edges
     */
    public EdgeHasSet getVetEdg(Node node) {
        EdgeHasSet res = new EdgeHasSet();
        forEach(edge -> {
            if (edge.source.equals(node) || edge.target.equals(node)) {
                res.add(edge);
            }
        });
        return res;
    }

    /**
     * check hashSet if there's a edge has {@code}node as its source
     *
     * @return true if being found
     */
    public boolean findNodeEdgeSrc(Node node) {
        for (Edge edge : this) {
            if (edge.source.equals(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check hashSet if there's a edge has {@code}node as its target
     *
     * @return true if being found
     */
    public boolean findNodeEdgeTgt(Node node) {
        for (Edge edge : this) {
            if (edge.target.equals(node)) {
                return true;
            }
        }
        return false;
    }

    protected EdgeHasSet readFromFile(String filename) throws IOException {
        GraphFileReader reader = new GraphFileReader();
        return reader.readToEL(filename);
    }

    public void printAll() {
        HashMap<String, String> toPrint = new HashMap<>();
        forEach(edge -> {
            Node src = edge.source;
            Node tgt = edge.target;

            String strSrc = src.strName;
            String strTgt = tgt.strName;
            double weight = edge.weight;

            if (toPrint.containsKey(strSrc)) {
                String line = toPrint.get(strSrc);
                line += "  ->" + strTgt + "(" + weight + ")";
                toPrint.put(strSrc, line);
            } else {
                toPrint.put(strSrc, strSrc + "  ->" + strTgt + "(" + weight + ")");
            }
        });
        // print
        toPrint.forEach((k, v) -> {
            System.out.println(v);
        });
    }
    /**
     * Update the edgeSet if there's a edge with the same srcStr and tgtStr
     * @param edge new edge used to replace the original
     */
    public void updateEdge(Edge edge){
        for (Edge tpEdge : this) {
            if (tpEdge.source == edge.source && tpEdge.target == edge.target){
                tpEdge.setWeight(edge.weight);
            }
        }
    }
    /**
     * Move all edges with the {@code} node
     *
     * @param node node to be added
     */
    public void removeWithNode(Node node) {
        forEach(edge -> {
            if (node.strName.equals(edge.source.strName)) {
                remove(edge);
            }
        });
    }


}
