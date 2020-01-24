package Algorithms.Graph.Network;
// Author: Haotian Bai
// Shanghai University, department of computer science
import java.util.*;

/**
 * This class can be used as a list class while automatically
 * clean nodes with duplicated names and leave the node with a higher value.
 *
 */
public class NodeList extends LinkedList<Node> {
    /***
     * Add a {@code}node into list. If it already existed(same name), choose the one with
     * greater value
     * @param node node to be added
     * @return true for success
     */
    @Override
    public boolean add(Node node) {
        for (Node tpNode : this) {
            if (tpNode.strName.equals(node.strName)) {
                if (tpNode.value < node.value) {
                    tpNode.setValue(node.value);
                    return true;
                } else return false;
            }
        }
        // not found
        return super.add(node);
    }

    public boolean add(String strNode){
        return this.add(new Node(strNode));
    }
    public boolean add(String strNode, double weight) {
        return this.add(new Node(strNode,weight));
    }

    public Node findByName(String strNode) {
        for (Node tpNode : this) {
            if (tpNode.strName.equals(strNode)) {
                return tpNode;
            }
        }
        return null;

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
     *
     * @param node node to be added
     */
    public void sortedAdd(Node node) {
        int index = Collections.binarySearch(this, node, Comparator.comparing(o -> o.strName));
        if (index >= 0) {
            this.add(index, node);
        } else {
            this.add(-index - 1, node);
        }
    }

    /**
     * add a node according to the ascending sequence of the nodes' value
     *
     * @param node node to be added
     */
    public void vAscAdd(Node node) {

        double nodeToaddVal = node.value;
        if (this.isEmpty()) super.add(node);
        else {
            Iterator<Node> iterator = this.iterator();
            int index = 0;
            while(iterator.hasNext()){
                Node tmpNode = iterator.next();
                double tmpVal = tmpNode.value;
                if(tmpVal > nodeToaddVal){
                    super.add(index,node);
                    break;
                }
                else{
                    index++;
                }
            }
            super.add(index,node);
        }
    }


}
