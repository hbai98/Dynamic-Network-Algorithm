package Algorithms.Graph.Utils.AdjList;

import Algorithms.Graph.Network.AbstractAdjList;
import Algorithms.Graph.Utils.List.HNodeList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Simple graph based on AdjList and a rowMap to make indexing faster
 */
public class Graph extends AbstractAdjList {
    //-----------neighbor nodes info------------------
    private HashMap<String, HashSet<String>> neighborsMap;

    HashMap<String, Integer> rowMap;
    private int edgeCount = -1;

    public Graph(){
        super();
        rowMap = new HashMap<>();
    }
    public Graph(HashMap<String, HashSet<String>> neighborsMap){
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
        rowMap.put(hNodeList.signName,this.size());
        return add(hNodeList);
    }

    public void addOneNode(String tgtHead,String tgtNode,double weight){
        if(rowMap.containsKey(tgtHead)){
            this.get(rowMap.get(tgtHead)).add(tgtNode,weight);
        }
        else{
            HNodeList tmp = new HNodeList(tgtHead);
            tmp.add(tgtNode,weight);
            this.add(tmp);
            rowMap.put(tgtHead,size()-1);
            rowSet.add(tgtHead);
        }
        colSet.add(tgtNode);
    }
    public HashMap<String, HashSet<String>> getNeighborsMap() {
        return neighborsMap;
    }
    public int getEdgeCount(){
        if(edgeCount == -1){
            int c = 0;
            for (int i = 0; i < size(); i++) {
                c+=get(i).size();
            }
            edgeCount = c;
            return c;
        }
        return edgeCount;
    }

    public void setNeighborMap(HashMap<String, HashSet<String>> graphNeighbors) {
        this.neighborsMap = graphNeighbors;
    }
}
