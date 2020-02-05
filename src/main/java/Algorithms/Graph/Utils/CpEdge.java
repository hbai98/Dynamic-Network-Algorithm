package Algorithms.Graph.Utils;
// Author: Haotian Bai
// Shanghai University, department of computer science
import Algorithms.Graph.Network.Node;
import org.jgrapht.graph.DefaultEdge;

import java.util.Objects;

public class CpEdge extends DefaultEdge implements Comparable<CpEdge>{

    public enum Type {UNDIRECTED, DIRECTED}

    protected Node source;
    protected Node target;
    protected double weight;
    protected Type type;
    // used to record the edge's position in the similarity matrix
    protected int matRow = -1;
    protected int matCol = -1;
    /**
     * For easing the comparision between edges. <p>Limit:
     * the edge is both undirected and its components are with the ascending name order.
     * This constructor will force "undirected" edges follow ascending name order if it's not the case.</>
     */
    public CpEdge(String src, String tgt, double weight, Type type) throws IllegalArgumentException {
        this.source = new Node(src);
        this.target = new Node(tgt);
        this.weight = weight;
        this.type = type;

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
    public CpEdge(String src, String tgt, double weight){
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
    public CpEdge(String src, String tgt){
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

    public CpEdge(Node src, Node tgt, double weight){
        this.source = src;
        this.target = tgt;
        this.weight = weight;
        this.type = Type.UNDIRECTED;
        if(src.compareTo(tgt) > 0){
            Node tpNode = source;
            source = target;
            target = tpNode;
        }
    }

    public CpEdge(Node src, Node tgt){
        this.source = src;
        this.target = tgt;
        this.weight = 0.0d;
        this.type = Type.UNDIRECTED;
        if(src.compareTo(tgt) > 0){
            Node tpNode = source;
            source = target;
            target = tpNode;
        }
    }
    public CpEdge(Node src, Node tgt, int matRow , int matCol){
        this.source = src;
        this.target = tgt;
        this.weight = 0.0d;
        this.matRow = matRow;
        this.matCol = matCol;
        this.type = Type.UNDIRECTED;
        if(src.compareTo(tgt) > 0){
            Node tpNode = source;
            source = target;
            target = tpNode;
        }
    }
    public CpEdge(Node src, Node tgt, int matRow , int matCol, double weight){
        this.source = src;
        this.target = tgt;
        this.weight = weight;
        this.matRow = matRow;
        this.matCol = matCol;
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

    public int getMatCol() {
        return matCol;
    }

    public int getMatRow() {
        return matRow;
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

    public void setMatCol(int matCol) {
        this.matCol = matCol;
    }

    public void setMatRow(int matRow) {
        this.matRow = matRow;
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
    public int compareTo(CpEdge o) {
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
        CpEdge edge = (CpEdge) o;
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
