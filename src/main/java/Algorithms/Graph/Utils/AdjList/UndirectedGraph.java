package Algorithms.Graph.Utils.AdjList;

import Algorithms.Graph.Network.AbstractAdjList;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.List.HNodeList;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class UndirectedGraph extends AbstractAdjList {
    HashMap<String, Integer> rowMap;

    public UndirectedGraph(){
        super();
        rowMap = new HashMap<>();
    }
    /**
     * find node head's all nodes.
     *
     * @param headNodeName target node
     */
    @Override
    protected HNodeList getHeadNodesList(String headNodeName) {
        if(rowMap.containsKey(headNodeName)){
            return this.get(rowMap.get(headNodeName));
        }
        return null;
    }

    @Override
    public void addOneNode(String node1, String node2, double weight) {
        if(rowMap.containsKey(node1)){
            this.get(rowMap.get(node1)).add(node2,weight);
        }
        else{
            HNodeList tmp = new HNodeList(node1);
            tmp.add(node2,weight);
            this.add(tmp);
            rowMap.put(node1,size()-1);
            rowSet.add(node1);
        }
        if(rowMap.containsKey(node2)){
            this.get(rowMap.get(node2)).add(node1,weight);
        }
        else{
            HNodeList tmp = new HNodeList(node2);
            tmp.add(node1,weight);
            this.add(tmp);
            rowMap.put(node2,size()-1);
        }
        colSet.add(node2);
    }

    @Override
    protected boolean removeNode(String node1, String node2) {
        if(rowMap.containsKey(node1)&&rowMap.containsKey(node2)){
            HNodeList hNodeList = this.get(rowMap.get(node1));
            HNodeList hNodeList_ = this.get(rowMap.get(node2));
            return hNodeList.remove(node2)&&hNodeList_.remove(node1);
        }
        return false;
    }

    @Override
    protected boolean addRowList(HNodeList hNodeList) {
        rowSet.add(hNodeList.signName);
        rowMap.put(hNodeList.signName,this.size());
        return add(hNodeList);
    }

    @Override
    public int getEdgeCount() {
        return super.getEdgeCount()/2;
    }

    public Set<String> getNebs(String node) {
        return this.get(rowMap.get(node)).stream().map(Node::getStrName).collect(Collectors.toSet());
    }
}
