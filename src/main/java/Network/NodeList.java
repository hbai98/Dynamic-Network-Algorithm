package Network;
// Author: Haotian Bai
// Shanghai University, department of computer science
import java.util.*;

public class NodeList extends LinkedList<Node> {
    /***
     * Add a protein into list. If it already existed(same name), choose the one with
     * greater value
     * @param node node to be added
     * @return true for success
     */
    @Override
    public boolean add(Node node) {
        for (Node tpNode : this) {
            if (tpNode.strProteinName.equals(node.strProteinName)) {
                if (tpNode.proteinValue < node.proteinValue) {
                    tpNode.setProteinValue(node.proteinValue);
                    return true;
                } else return false;
            }
        }
        // not found
        return super.add(node);
    }


    public Node findByName(String strProteinName) {
        for (Node tpNode : this) {
            if (tpNode.strProteinName.equals(strProteinName)) {
                return tpNode;
            }
        }
        return null;

    }

    public void removeAll(Node node) {
        Collection<Node> nodes = new LinkedList<Node>();
        nodes.add(node);
        this.removeAll(nodes);
    }

    /**
     * sort according to name's dictionary sequence
     */
    public void sort() {
        sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.strProteinName.compareTo(o2.strProteinName);
            }
        });
    }

    /**
     * add a node while not interfere with the sorted sequence
     *
     * @param node node to be added
     */
    public void sortedAdd(Node node) {
        int index = Collections.binarySearch(this, node, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.strProteinName.compareTo(o2.strProteinName);
            }
        });
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

        double nodeToaddVal = node.proteinValue;
        if (this.isEmpty()) super.add(node);
        else {
            Iterator<Node> iterator = this.iterator();
            int index = 0;
            while(iterator.hasNext()){
                Node tmpNode = iterator.next();
                double tmpVal = tmpNode.proteinValue;
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
