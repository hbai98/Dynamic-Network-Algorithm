package Internal.Algorithms.Graph.Network;

import Internal.Algorithms.Graph.Utils.List.HNodeList;

import java.util.HashSet;
import java.util.LinkedList;

public abstract class AbstractAdjList extends LinkedList<HNodeList> {
    protected HashSet<String> rowSet;
    protected HashSet<String> colSet;
    private int edgeCount = -1;
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
    public abstract void addOneNode(String tgtHead, String tgtNode, double weight);

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

    public int getEdgeCount(){
        if(edgeCount == -1){
            int c = this.parallelStream().mapToInt(LinkedList::size).sum();
            edgeCount = c;
            return c;
        }
        return edgeCount;
    }

    /**
     * add one HNodeList
     */
    protected abstract boolean addRowList(HNodeList hNodeList);

    public void setRowSet(HashSet<String> rowSet) {
        this.rowSet = rowSet;
    }

    public void setColSet(HashSet<String> colSet) {
        this.colSet = colSet;
    }

    public HashSet<String> getColSet() {
        return colSet;
    }

    public HashSet<String> getRowSet() {
        return rowSet;
    }

}
