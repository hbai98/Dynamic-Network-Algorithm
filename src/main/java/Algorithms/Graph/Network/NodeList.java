package Algorithms.Graph.Network;
// Author: Haotian Bai
// Shanghai University, department of computer science

import java.util.*;


public class NodeList extends LinkedList<Node> {


    public Node findByName(String strNode) {
        for (Node tpNode : this) {
            if (tpNode.strName.equals(strNode)) {
                return tpNode;
            }
        }
        return null;
    }



    public boolean remove(String strNode) {
        return this.remove(new Node(strNode));
    }

    public boolean remove(Node node) {
        return super.remove(node);
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
    /**
     * find the max value node with tgt nodes not been assigned,

     * @param assigned previous mapping result
     * @return the best node for mapping by greedy algorithm, null for all nodes has been assigned
     */
    public Node findMax(boolean[] assigned) {
        double max = -Double.MAX_VALUE;
        Node tpNode = null;
        for (int i=0;i<this.size();i++) {
            Node node = this.get(i);
            double val = node.getValue();
            if (!assigned[i] && val > max) {
                assigned[i] = true;
                max = val;
                tpNode = node;
            }
        }
        return tpNode;
    }

}
