package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.NodeList;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.io.IOException;
import java.util.*;

/**
 * This class is meant to make a graph based on adjacent list which
 *  has already been defined in Network, and the next step is to
 * provide another LinkList to wrap NodeLists.
 * <p>
 *     The order has remained lexicographically ascending, which can then
 *    be characterized as a matrix. This matrix is maintained simultaneously
 *    for the convenience of shifting the matrix to Hungarian (which can only be
 *    manipulated using matrices.)
 * </p>
 */
public class AdjList extends LinkedList<HNodeList> {
    private DoubleMatrix mat;

    // used to construct the matrix
    public AdjList(){
    }

    /**
     * <ol>
     *     <li>Find the longest nodeList,and then initialize the matrix.</li>
     *     <li>Iterate other nodeLists mapping the other graph's NodeSet(NodeList).</li>
     * </ol>
     * <p>
     *     Time complexity: O(n+(n-1)*1/2*(1+n)*n) - > O(n^3) -> brute force
     * </p>
     * <p>
     *     construct a HashMap <K,V> & colMap , rowMap where:
     *         <ol>
     *             <li>K: nodeName </li>
     *             <li>V: column position of the matrix</li>
     *         </ol>
     *     <p>
     *         time complexity: O(n + (n-1)*n + n) -> O(n^2)
     *     </p>
     * </p>
     * @return Similar matrix
     */
    protected DoubleMatrix toMatrix() {
        // step 1: find the longest nodeList.
        Pair<Integer,Integer> result = findLongest();
        int maxCol = result.getFirst();
        int loc = result.getSecond();
        // step 2：create the dictionary
        HashMap<String,Integer> map = dict(loc);
        // step 3 : get the matrix
        return mapping(map);
    }
    // step 1: find the longest nodeList.
    private Pair<Integer, Integer> findLongest(){
        int maxCol = 0;
        int loc = -1;
        for (int index = 0; index < this.size(); index++) {
            // copy the rowMap
            HNodeList tpList = this.get(index);
            int size = tpList.size();
            if(size > maxCol) {
                maxCol = size;
                loc = index;
            }
        }
        return new Pair<>(maxCol,loc);
    }

    /**
     * step 2：create the dictionary
     * @return map for the matrix's column
     */
    private HashMap<String,Integer> dict(int rowIndex){
        // node name , index
        HashMap<String,Integer> map = new HashMap<>();
        NodeList list = this.get(rowIndex);
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getStrName(),i);
        }
        return map;
    }

    /**
     * Iterate other nodeLists mapping the other graph's NodeSet(NodeList).
     * @param map the longest map
     */
    private DoubleMatrix mapping( HashMap<String,Integer> map){
        int row = this.size();
        int col = map.size();
        DoubleMatrix matrix = new DoubleMatrix(row,col);
        for (int r = 0; r < this.size(); r++) {
            NodeList list = this.get(r);
            DoubleMatrix rowVector = new DoubleMatrix(col);
            list.forEach(node -> {
                rowVector.put(map.get(node.getStrName()),node.getValue());
            });
            matrix.putRow(r,rowVector);
        }
        return matrix;
    }



    /**
     * add a list with to graph, if it exists(same head name),combine two of them; else add the list to the graph.
     * @param hNodeList a list to be added
     * @return true for already exist.
     */
    @Override
    public boolean add(HNodeList hNodeList) {
        assert(hNodeList!=null);
        int index = Collections.binarySearch(this, hNodeList, Comparator.comparing(o -> o.signName));
        if(index >= 0){
            this.get(index).addAll(hNodeList);
            return true;
        }
        else{
            add(-index-1, hNodeList);
            return false;
        }
    }


    /**
     * find node head's all nodes.
     * @param tgtNode target node
     */
    public HNodeList getNodeList(String tgtNode){
        for(HNodeList hNodeList : this){
            if(hNodeList.signName.equals(tgtNode)){
                return hNodeList;
            }
        }
        return null;
    }

    public double getMatrixVal(String tgtHead, String tgtNode) throws IOException {
        int index = Collections.binarySearch(this,new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if(index >= 0){
            return this.get(index).sortFindByName(tgtNode).getValue();
        }
        else{
            throw new IOException("Can't find the target head");
        }
    }

    /**
     * remove tgtNode from list(tgtHead).
     * @param tgtHead headName of the list.
     * @param tgtNode name of the node to be removed.
     * @return true for success.
     */
    public boolean removeAllNode(String tgtHead, String tgtNode){
        int index = Collections.binarySearch(this,new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if(index >= 0){
            this.get(index).remove(tgtNode);
            return true;
        }
        else{
          return false;
        }
    }
    /**
     * add tgtHG to list(tgtHead).
     * @param tgtHead headName of the list.
     * @param tgtNode name of the homoGene to be removed.
     * @return true for already node exist.
     */
    // TODO a little duplicated !
    public boolean addOneNode(String tgtHead, String tgtNode){
        int index = Collections.binarySearch(this,new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if(index >= 0){
            this.get(index).add(tgtNode);
            return true;
        }
        else{
            add(-index-1,new HNodeList(tgtHead));
            this.get(-index-1).add(tgtNode);
            return false;
        }
    }
    /**
     * add tgtHG to list(tgtHead),and keep the target list an ascending order.
     * @param tgtHead headName of the list.
     * @param tgtNode name of the homoGene to be removed.
     * @return true for already node exist.
     */
    public boolean sortAddOneNode(String tgtHead, String tgtNode){
        int index = Collections.binarySearch(this,new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if(index >= 0){
            this.get(index).sortAdd(tgtNode);
            return true;
        }
        else{
            add(-index-1,new HNodeList(tgtHead));
            this.get(-index-1).sortAdd(tgtNode);
            return false;
        }
    }
    /**
     * add tgtNode to list(tgtHead) with <code>weight</code>.
     * @param tgtHead headName of the list.
     * @param tgtNode name of the Node to be removed.
     * @return true for node already exist.
     */
    public boolean addOneNode(String tgtHead, String tgtNode, double weight){
        int index = Collections.binarySearch(this,new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if(index >= 0){
            this.get(index).add(tgtNode,weight);
            return true;
        }
        else{
            add(-index-1,new HNodeList(tgtHead));
            this.get(-index-1).add(tgtNode,weight);
            return false;
        }
    }

    /**
     * add tgtNode to list(tgtHead) with <code>weight</code>,and
     * keep the target list an ascending order.
     * @param tgtHead headName of the list.
     * @param tgtNode name of the Node to be removed.
     * @return true for node already exist.
     */
    public boolean sortAddOneNode(String tgtHead, String tgtNode, double weight){
        int index = Collections.binarySearch(this,new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if(index >= 0){
            this.get(index).sortAdd(tgtNode,weight);
            return true;
        }
        else{
            add(-index-1,new HNodeList(tgtHead));
            this.get(-index-1).sortAdd(tgtNode,weight);
            return false;
        }
    }
    //------------------PUBLIC-----------------------------




}
