package Network;

import java.util.Iterator;
import java.util.LinkedList;

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
            if(tpNode.strProteinName.equals(node.strProteinName)){
                if(tpNode.proteinValue < node.proteinValue){
                    tpNode.setProteinValue(node.proteinValue);
                    return true;
                }
                else return false;
            }
        }
        // not found
        return super.add(node);
    }


    public void add() {

    }

    public Node findByName(String strProteinName){
        for(Node tpNode:this){
            if (tpNode.strProteinName.equals(strProteinName)){
                return tpNode;
            }
        }
        return null;
    }


}
