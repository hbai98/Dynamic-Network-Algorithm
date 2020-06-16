package Algorithms.Graph.Utils.AdjList;

import Algorithms.Graph.Network.AbstractAdjList;
import Algorithms.Graph.Utils.List.HNodeList;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Simple graph based on AdjList and a rowMap to make indexing faster
 */
public class Graph extends AbstractAdjList {
    //-----------neighbor nodes info------------------
    private HashMap<String, HNodeList> neighborsMap;

    HashMap<String, Integer> rowMap;
    public Graph(){
        super();
        rowMap = new HashMap<>();
    }
    public Graph(HashMap<String, HNodeList> neighborsMap){
        super();
        rowMap = new HashMap<>();
        this.neighborsMap = neighborsMap;
    }

    /**
     * find node head's all nodes.
     *
     * @param headNodeName target node
     */
    protected HNodeList getHeadNodesList(String headNodeName) {
        if(rowMap.containsKey(headNodeName)){
            return this.get(rowMap.get(headNodeName));
        }
        return null;
    }

    /**
     * move one node from adjlist
     */
    protected boolean removeNode(String tgtHead, String tgtNode){
        if(rowMap.containsKey(tgtHead)){
            HNodeList hNodeList = this.get(rowMap.get(tgtHead));
            return hNodeList.remove(tgtNode);
        }
        return false;
    }

    @Override
    public boolean addRowList(HNodeList hNodeList) {
        rowSet.add(hNodeList.signName);
        rowMap.put(hNodeList.signName,this.size()-1);
        return add(hNodeList);
    }

    public HashMap<String, HNodeList> getNeighborsMap() {
        return neighborsMap;
    }
}
