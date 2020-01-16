package Algorithms.Graph.Network;
// Author: Haotian Bai
// Shanghai University, department of computer science
import org.jgrapht.graph.DefaultEdge;

import java.util.Objects;

public class Edge extends DefaultEdge implements Comparable<Edge> {

    public enum Type {UNDIRECTED, DIRECTED}

    protected Node source;
    protected Node target;
    protected double weight;
    protected Type type;

    /**
     * For easing the comparision between edges. <p>Limit:
     * the edge is both undirected and its components are with the ascending name order.
     * This constructor will force "undirected" edges follow ascending name order if it's not the case.</>
     */
    public Edge(String src, String tgt, double weight, Type type) throws IllegalArgumentException {
        this.source = new Node(src);
        this.target = new Node(tgt);
        this.weight = weight;
        this.type = type;
        if (!type.equals(Type.UNDIRECTED)) {
            throw new IllegalArgumentException("The edge should be undirected.");
        }
        if(src.compareTo(tgt) > 0){
            Node tpNode = source;
            source = target;
            target = tpNode;
        }
    }
    /**
     * For easing the comparision between edges. <p>Limit:
     * the edge is both undirected and its components are with the ascending name order.
     * This constructor will force "undirected" edges follow ascending name order if it's not the case.</>
     */
    public Edge(String src, String tgt, double weight){
        this.source = new Node(src);
        this.target = new Node(tgt);
        this.weight = weight;
        this.type = Type.UNDIRECTED;
        if(src.compareTo(tgt) > 0){
            Node tpNode = source;
            source = target;
            target = tpNode;
        }
    }
    /**
     * For easing the comparision between edges. <p>Limit:
     * the edge is both undirected and its components are with the ascending name order.
     * This constructor will force "undirected" edges follow ascending name order if it's not the case.</>
     */
    public Edge(String src, String tgt){
        this.source = new Node(src);
        this.target = new Node(tgt);
        this.weight = 0.0d;
        this.type = Type.UNDIRECTED;
        if(src.compareTo(tgt) > 0){
            Node tpNode = source;
            source = target;
            target = tpNode;
        }
    }

    //--------------------PUBLIC ACCESS---------------------------
    @Override
    public Node getSource() {
        return source;
    }

    @Override
    public Node getTarget() {
        return target;
    }

    public double getWeight() {
        return weight;
    }

    public Type getType() {
        return type;
    }
//    -----------------SET--------------------


    public void setSource(Node source) {
        this.source = source;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Method for the comparision between edges with the rule as the followings:
     * <p>1. source node takes first considerations, (lexicographical way in its name)</p>
     * <p>2. then the second</p>
     *
     * @param o another edge to be compared
     * @return value 0 for equal, less than 0 for the edge < edge o, greater than 0 for the edge > edge 0
     */
    @Override
    public int compareTo(Edge o) {
        Node src = o.source;
        Node tgt = o.target;
        int tmpCpResult = this.source.compareTo(src);
        if (tmpCpResult==0){
            return this.target.compareTo(tgt);
        }
        return tmpCpResult;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source +
                ", target=" + target +
                ", weight=" + weight +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Double.compare(edge.weight, weight) == 0 &&
                source.equals(edge.source) &&
                target.equals(edge.target) &&
                type == edge.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, weight, type);
    }
}
