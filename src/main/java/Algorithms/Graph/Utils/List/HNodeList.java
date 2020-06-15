package Algorithms.Graph.Utils.List;

import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.NodeList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * This class can be used as a list class while automatically
 * clean nodes with duplicated names and leave the node with a higher value.
 */
public class HNodeList extends NodeList {
    // target protein
    //----------keys-----------
    public String signName;
    //---------record the max value in the list
    private double max;
    private String maxNodeName;

    public HNodeList(String signName){
        this.signName = signName;
        this.max = -1;
    }
    public HNodeList(){
        this.max = -1;
    }

    public int computeNonZeroNumb(){
        int sum = 0;
        for (Node node : this) {
            if(node.getValue() != 0){
                sum ++;
            }
        }
        return sum;
    }
    /***
     * Add a {@code}node into list. If it already existed(same name), choose the one with
     * greater value, and the nodeList keeps it's nodes with the characteristic of
     * single existence.
     * @param node node to be added
     * @return true for the node has already been added.
     */
    @Override
    public boolean add(Node node) {
        for (Node tpNode : this) {
            // find one same node, return;
            if (tpNode.equals(node)) {
                if (tpNode.getValue() < node.getValue()) {
                    tpNode.setValue(node.getValue());
                    return true;
                } else {
                    return false;
                }
            }
        }
        // not found
        return super.add(node);
    }

    public boolean add(String strNode) {
        return this.add(new Node(strNode));
    }

    public boolean add(String strNode, double weight) {
        return this.add(new Node(strNode, weight));
    }

    /**
     * sort according to name's dictionary sequence
     */
    public void sort() {
        sort(Comparator.comparing(Node::getStrName));
    }

    /**
     * add a node while not interfere with the sorted sequence
     *
     * @param node node to be added
     */
    public void sortAdd(Node node) {
        int index = Collections.binarySearch(this, node, Comparator.comparing(Node::getStrName));
        if (index >= 0) {
            this.get(index).setValue(node.getValue());
        } else {
            this.add(-index - 1, node);
        }
    }

    /**
     * add a node while not interfere with the sorted sequence
     * <p>
     * Only add while the node's weight is greater.
     * </p>
     *
     * @param strNode node's name to be added
     */
    public void sortAdd(String strNode) {
        this.sortAdd(new Node(strNode));
    }

    /**
     * add a node while not interfere with the sorted sequence
     *
     * @param strNode node's name
     * @param weight  node's weight
     */
    public void sortAdd(String strNode, double weight) {
        this.sortAdd(new Node(strNode, weight));
    }

    /**
     * add a node while not interfere with the sorted sequence
     * <p>
     * Only add while the node's weight is greater.
     * </p>
     */
    public void sortAddAll(Node... nodes) {
        for (Node node :
                nodes) {
            sortAdd(node);
        }
    }
    public void sortAddAll(String... nodeStrs) {
        for (String nodeName :
                nodeStrs) {
            sortAdd(nodeName);
        }
    }

    /**
     * Find node according to the name, but before use this method,
     * user should ensure the nodeList has already be in order.
     *
     * @param strNode node to be found
     * @return node found or new Node with strNode, but its value equals 0
     */
    public Node binarySearchFind(String strNode) {
        int index = Collections.binarySearch(this, new Node(strNode), Comparator.comparing(Node::getStrName));
        if (index >= 0) {
            return this.get(index);
        } else return new Node(strNode);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HNodeList nodes = (HNodeList) o;
        return signName.equals(nodes.signName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), signName);
    }

   //---------------PUBLIC------------------

    public String getSignName() {
        return signName;
    }


    public double getMax() {
        return max;
    }

    public String getMaxNodeName() {
        return maxNodeName;
    }

    //-----------------SET-----------------

    public void setSignName(String signName) {
        this.signName = signName;
    }


    public void setMax(double max) {
        this.max = max;
    }

    public void setMaxNodeName(String maxNodeName) {
        this.maxNodeName = maxNodeName;
    }
}
