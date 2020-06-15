package Algorithms.Graph.Network;

// Author: Haotian Bai
// Shanghai University, department of computer science


import java.util.Objects;

/**
 * Node for Protein network
 */
public class Node implements Comparable<Node> {
    protected String strName;
    protected double value;


    public Node(String strName, double value, String strLayerCode) {
        this.strName = strName;
        this.value = value;
    }

    public Node(String strName) {
        this.strName = strName;
        this.value = 0.0d;
    }

    public Node(double value) {
        this.value = value;
        this.strName = "";
    }

    public Node(String strName, double value) {
        this.strName = strName;
        this.value = value;
    }




    /***
     * methods for public access
     */



    public double getValue() {
        return value;
    }

    public String getStrName() {
        return strName;
    }


    // ----------------------SET-------------------------------
    public void setValue(double value) {
        this.value = value;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }




    /**
     * comparision between nodes
     *
     * @param o node to be compared
     * @return the value 0 if the argument node is equal to this node;
     * a value less than 0 if this node is lexicographically less than the node argument;
     * and a value greater than 0 if this node is lexicographically greater than the node argument.
     */
    @Override
    public int compareTo(Node o) {
        return strName.compareTo(o.getStrName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(strName, node.strName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strName);
    }
}
