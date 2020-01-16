package Algorithms.Graph.Network;
// Author: Haotian Bai
// Shanghai University, department of computer science
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import static Algorithms.Graph.IO.AdjListFileReader.read;

/**
 * This class can be defined as HashSet
 * for the occurance of the same edges will not be allowed in lists
 */
public class EdgeList extends HashSet<Edge> {
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
        // Note: equals() has been overloaded for the edge comparision,
        // so add(),remove(),contains() will all be changed.
        return super.add(edge);
    }

    public boolean add(String src, String tgt, double weight, Edge.Type type) {
        return add(new Edge(src, tgt, weight, type));
    }

    /**
     * Find edges contains {@code} node
     *
     * @param node node to be found
     * @return hasSet with all edges
     */
    public EdgeList getVetEdg(Node node) {
        EdgeList res = new EdgeList();
        forEach(edge -> {
            if(edge.source.equals(node)||edge.target.equals(node)){
                res.add(edge);
            }
        });
        return res;
    }

    protected EdgeList readFromFile(String filename) throws IOException {
        return read(filename);
    }

    public void printAll(){
        HashMap<String,String> toPrint = new HashMap<>();
        forEach(edge -> {
            Node src = edge.source;
            Node tgt = edge.target;

           String strSrc = src.strName;
           String strTgt = tgt.strName;
           double weight = edge.weight;

           if(toPrint.containsKey(strSrc)){
               String line = toPrint.get(strSrc);
               line += "  ->"+strTgt+"("+ weight+")";
               toPrint.put(strSrc,line);
           }
           else{
               toPrint.put(strSrc,strSrc + "  ->"+strTgt+"("+ weight+")");
           }
        });
        // print
        toPrint.forEach((k,v)->{
            System.out.println(v);
        });
    }

    /**
     * Move all edges with the {@code} node
     * @param node node to be added
     */
    public void removeWithNode(Node node){
        forEach(edge -> {
            if(node.strName.equals(edge.source.strName)){
                remove(edge);
            }
        });
    }


}
