package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.Node;
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
    //------------------similarity matrix----------
    private HashSet<String> rowSet;
    private HashSet<String> colSet;
    private HashMap<String,Integer> rowMap;
    private HashMap<String,Integer> colMap;

    // used to construct the matrix

    /**
     * <ol>
     *     <li>initialize the matrix.</li>
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
    public DoubleMatrix toMatrix() {
        if (mat == null) {
            // step 1: initialize the matrix.
            HashSet<String> colSet = init();
            // step 2：create the dictionary
            HashMap<String, Integer> colList = dict(colSet);
            // step 3 : get the matrix
            mapping(colList);
        }
        return mat;

    }
    protected DoubleMatrix toMatrix(HashSet<String> graph_1,HashSet<String> graph_2) {
        if (mat == null) {
            // step 1: initialize the matrix.
            init(graph_1, graph_2);
            // step 2：create the dictionary
            HashMap<String, Integer> colList = dict(graph_2);
            // step 3 : get the matrix
            mapping(colList);
        }
        return mat;

    }
    // step 1: initialize the matrix.
    private void init(HashSet<String> graph_1,HashSet<String> graph_2){
        this.rowSet = graph_1;
        this.colSet = graph_2;
        mat = new DoubleMatrix(graph_1.size(),graph_2.size());
    }
    // alter: step_1

    /**
     * iterate over the adjList to init.
     * return colSet.
     */
    private HashSet<String> init(){
        int row = 0;
        int col = 0;
        HashSet<String> colSet = new HashSet<>();
        HashSet<String> rowSet = new HashSet<>();

        for (HNodeList list: this) {
            row++;
            rowSet.add(list.getSignName());
            for (Node node: list) {
                colSet.add(node.getStrName());
            }
        }
        this.rowSet = rowSet;
        this.colSet = colSet;
        mat = new DoubleMatrix(row,colSet.size());
        return colSet;
    }

    /**
     * step 2：create the dictionary
     * @return map for the matrix's column
     */
    private HashMap<String,Integer> dict(HashSet<String> colSet){
        // node name , index
        HashMap<String,Integer> map = new HashMap<>();
        ArrayList<String> colList = new ArrayList<String>(colSet);
        colList.sort(Comparator.naturalOrder());
        HashMap<String,Integer> hashMap = new HashMap<>();
        for (int i = 0; i < colList.size(); i++) {
            hashMap.put(colList.get(i),i);
        }
        colMap = hashMap;
        return hashMap;
    }

    /**
     * Iterate all nodeLists mapping the colList.
     * @param colMap the map for the matrix's column
     */
    private void mapping(HashMap<String,Integer> colMap){
        for (int r = 0; r < this.size(); r++) {
            HNodeList list = this.get(r);
            rowMap.put(list.getSignName(),r);
            DoubleMatrix rowVector = new DoubleMatrix(colMap.size());
            list.forEach(node -> {
                rowVector.put(colMap.get(node.getStrName()),node.getValue());
            });
            mat.putRow(r,rowVector);
        }
    }



    /**
     * add a list with to graph, if it exists(same head name),combine two of them; else add the list to the graph.
     * <p>
     *     Notice: automatically sort() according to the name's lexicographical order.
     * </p>
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

    public Pair<Integer, Node> findMaxOfList(String rowName){
        int index = Collections.binarySearch(this,new HNodeList(rowName), Comparator.comparing(o -> o.signName));
        if(index >=0){
            return new Pair<Integer,Node>(index,this.get(index).findMax());
        }
        else{
            throw new IllegalArgumentException("Can't the rowName HNodeList in the adjList");
        }
    }
    public Pair<Integer,Node> findMaxOfList(int rowIndex){
        assert(rowIndex>=0);
        return new Pair<>(rowIndex, this.get(rowIndex).findMax());
    }
    //------------------PUBLIC-----------------------------


    public HashSet<String> getColSet() {
        if(mat == null){
            mat = this.toMatrix();
            return colSet;
        }
        return colSet;
    }

    public HashSet<String> getRowSet() {
        if(mat == null){
            mat = this.toMatrix();
            return rowSet;
        }
        return rowSet;
    }

    public HashMap<String,Integer> getRowMap(){
        if(rowMap == null){
            for (int r = 0; r < this.size(); r++) {
                HNodeList list = this.get(r);
                rowMap.put(list.getSignName(), r);
            }
            return rowMap;
        }
        else return rowMap;
    }
    public HashMap<String, Integer> getColMap() {
        if(mat == null){
            mat = this.toMatrix();
            return colMap;
        }
        return colMap;
    }
}
