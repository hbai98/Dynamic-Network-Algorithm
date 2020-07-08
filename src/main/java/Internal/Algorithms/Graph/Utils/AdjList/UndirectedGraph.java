package Internal.Algorithms.Graph.Utils.AdjList;

import Internal.Algorithms.Graph.Network.AbstractAdjList;
import Internal.Algorithms.Graph.Network.Node;
import Internal.Algorithms.Graph.Utils.List.HNodeList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UndirectedGraph extends AbstractAdjList implements Graph {
    HashMap<String, Integer> rowMap;
    public double edgeScore = 0.;
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

    /**
     * rowSet and colSet only retain source -> target , and the two sets would be
     * the same as those in Directed Graph
     */
    @Override
    public void addOneNode(String node1, String node2, double weight) {
        rowSet.add(node1);
        colSet.add(node2);
        if(rowMap.containsKey(node1)){
            this.get(rowMap.get(node1)).add(node2,weight);
        }
        else{
            HNodeList tmp = new HNodeList(node1);
            tmp.add(node2,weight);
            this.add(tmp);
            rowMap.put(node1,size()-1);
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

    @Override
    public Graph dup() {
        UndirectedGraph res = new UndirectedGraph();
        this.forEach(res::addRowList);
        res.rowMap = new HashMap<>(this.rowMap);
        res.rowSet = new HashSet<>(this.rowSet);
        res.colSet = new HashSet<>(this.colSet);
        return res;
    }

    public DirectedGraph toDirect(){
        DirectedGraph out = new DirectedGraph();
        this.forEach(h->h.forEach(n->{
            if(rowSet.contains(h.signName)&&colSet.contains(n.getStrName())){
                out.addOneNode(h.signName,n.getStrName(),edgeScore);
            }
        }));
        return out;
    }
}
