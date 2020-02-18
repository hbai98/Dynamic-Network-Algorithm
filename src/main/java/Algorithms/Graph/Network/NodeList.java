package Algorithms.Graph.Network;
// Author: Haotian Bai
// Shanghai University, department of computer science

import Algorithms.Graph.Utils.HNodeList;

import java.util.*;

/**
 * This class can be used as a list class while automatically
 * clean nodes with duplicated names and leave the node with a higher value.
 */
public class NodeList extends LinkedList<Node> {
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
                if (tpNode.value < node.value) {
                    tpNode.setValue(node.value);
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

    public Node findByName(String strNode) {
        for (Node tpNode : this) {
            if (tpNode.strName.equals(strNode)) {
                return tpNode;
            }
        }
        return null;
    }

    /**
     * Find node according to the name, but before use this method,
     * user should ensure the nodeList has already be in order.
     *
     * @param strNode node to be found
     * @return node found or new Node with strNode, but its value equals 0
     */
    public Node sortFindByName(String strNode) {
        int index = Collections.binarySearch(this, new Node(strNode), Comparator.comparing(o -> o.strName));
        if (index >= 0) {
            return this.get(index);
        } else return new Node(strNode);
    }

    public void remove(String strNode) {
        this.removeAll(new Node(strNode));
    }

    public void removeAll(Node node) {
        super.removeAll(Collections.singletonList(node));
    }

    /**
     * sort according to name's dictionary sequence
     */
    public void sort() {
        sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.strName.compareTo(o2.strName);
            }
        });
    }

    /**
     * add a node while not interfere with the sorted sequence
     * <p>
     * Only add while the node's weight is greater.
     * </p>
     *
     * @param node node to be added
     */
    public void sortAdd(Node node) {
        int index = Collections.binarySearch(this, node, Comparator.comparing(o -> o.strName));
        if (index >= 0) {
            if (node.getValue() > this.get(index).getValue()) {
                this.get(index).setValue(node.getValue());
            }
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
     * <p>
     * Only add while the node's weight is greater.
     * </p>
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

    public Node findMax() {
        double max = Double.MIN_NORMAL;
        Node tpNode = null;
        for (Node node : this) {
            double val = node.getValue();
            if (val > max) {
                max = val;
                tpNode = node;
            }
        }
        return tpNode;
    }


}
