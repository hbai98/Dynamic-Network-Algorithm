package Algorithms.Graph.Network;

import Algorithms.Graph.Utils.List.HNodeList;
import Algorithms.Graph.Utils.AdjList.SimList;

import java.util.HashSet;
import java.util.LinkedList;

public abstract class AbstractAdjList extends LinkedList<HNodeList> {
    protected HashSet<String> rowSet;
    protected HashSet<String> colSet;
    //---------------

    public AbstractAdjList(){
        rowSet = new HashSet<>();
        colSet = new HashSet<>();
    }
    /**
     * find node head's all nodes.
     *
     * @param headNodeName target node
     */
    protected abstract HNodeList getHeadNodesList(String headNodeName);

    /**
     * move one node from adjlist
     */
    protected abstract boolean removeNode(String tgtHead, String tgtNode);

    public HashSet<String> getAllNodes() {
        // merge
        HashSet<String> tpRow = new HashSet<>(rowSet);
        tpRow.addAll(colSet);
        return tpRow;
    }


    /**
     * Add all nodes strNames to set
     */
    protected void setAddAllNodesName(HashSet<String> set, HNodeList hNodeList) {
        hNodeList.forEach(
                n-> set.add(n.getStrName())
        );
    }



    /**
     * add one HNodeList
     */
    protected abstract boolean addRowList(HNodeList hNodeList);

    public HashSet<String> getColSet() {
        return colSet;
    }

    public HashSet<String> getRowSet() {
        return rowSet;
    }

}
