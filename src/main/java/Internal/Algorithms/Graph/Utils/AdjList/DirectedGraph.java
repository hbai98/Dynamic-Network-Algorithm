package Internal.Algorithms.Graph.Utils.AdjList;

import Internal.Algorithms.Graph.Network.AbstractAdjList;
import Internal.Algorithms.Graph.Utils.List.HNodeList;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Simple graph based on AdjList and a rowMap to make indexing faster
 */
public class DirectedGraph extends AbstractAdjList {
    //-----------neighbor nodes info------------------
    private HashMap<String, HashSet<String>> neighborsMap;
    HashMap<String, Integer> rowMap;

    public DirectedGraph(){
        super();
        rowMap = new HashMap<>();
    }
    public DirectedGraph(HashMap<String, HashSet<String>> neighborsMap){
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
    @Override
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
        return new HashMap<>(neighborsMap);
    }

    public void setNeighborMap(HashMap<String, HashSet<String>> graphNeighbors) {
        this.neighborsMap = graphNeighbors;
    }


}
