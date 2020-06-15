package Algorithms.Graph.Utils.AdjList;

import Algorithms.Graph.Network.AbstractAdjList;
import Algorithms.Graph.Network.EdgeHashSet;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Utils.List.HNodeList;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.io.IOException;
import java.util.*;

/**
 * This class is meant to make an adjacent list which represents the sparse matrix
 * <p>
 * The row and column orders have remained lexicographically ascending, which can then
 * be characterized as a matrix. This matrix is maintained simultaneously
 * for the convenience of shifting the matrix to Hungarian (which can only be
 * manipulated using matrices.)
 * </p>
 */
public class SimList extends AbstractAdjList {

    private DoubleMatrix mat;
    //------------------similarity matrix----------
    private EdgeHashSet allEdges;
    private HashMap<String, Integer> rowMap;
    private HashMap<String, Integer> colMap;
    //---------------Neighbors info--------------> src <-> tgt
    private HashMap<String,HashSet<String>> g1Neighbors;
    private HashMap<String,HashSet<String>> g2Neighbors;
    //---------------colMap mat index -> node's name-------------
    private HashMap<Integer, String> swapOrderedColMap;

    public SimList() {
        super();
    }



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
     *
     * @return Similar matrix
     */
    public DoubleMatrix getMatrix() {
        if (mat == null) {
            // step 1：create the dictionary
            HashMap<String, Integer> colList = dict();
            // step 2 : get the matrix
            mapping(colList);
        }
        // return copy, keep mat unchanged
        return mat.dup();
    }

    /**
     * When the structure of the adjList has changed, then synchronize the matrix
     */
    public void updateMatrix(){
        // step 1：create the dictionary
        HashMap<String, Integer> colList = dict();
        // step 2 : get the matrix
        mapping(colList);
        // above steps have already changed colMap and rowMap
        // rest
        reverseTpIndexColMap();
    }

    protected DoubleMatrix getMatrix(HashSet<String> graph_1, HashSet<String> graph_2) {
        if (mat == null) {
            // step 1: initialize the matrix.
            init(graph_1, graph_2);
            // step 2：create the dictionary
            HashMap<String, Integer> colList = dict();
            // step 3 : get the matrix
            mapping(colList);
        }
        return mat.dup();

    }

    // step 1: initialize the matrix.
    private void init(HashSet<String> graph_1, HashSet<String> graph_2) {
        this.rowSet = graph_1;
        this.colSet = graph_2;
        mat = new DoubleMatrix(graph_1.size(), graph_2.size());
    }
    // alter: step_1


    /**
     * step 1：create the dictionary
     *
     * @return map for the matrix's column
     */
    private HashMap<String, Integer> dict() {
        // node name , index
        ArrayList<String> colList = new ArrayList<>(colSet);
        colList.sort(Comparator.naturalOrder());
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < colList.size(); i++) {
            hashMap.put(colList.get(i), i);
        }
        colMap = hashMap;
        return hashMap;
    }

    /**
     * Step 2:
     * Iterate all nodeLists mapping the colList.
     *
     * @param colMap the map for the matrix's column
     */
    private void mapping(HashMap<String, Integer> colMap) {
        rowMap = new HashMap<>();
        for (int r = 0; r < this.size(); r++) {
            HNodeList list = this.get(r);
            rowMap.put(list.getSignName(), r);
            DoubleMatrix rowVector = new DoubleMatrix(colMap.size());
            list.forEach(node -> rowVector.put(colMap.get(node.getStrName()), node.getValue()));
            mat.putRow(r, rowVector);
        }
    }



    /**
     * add a list with to graph, if it exists(same head name),combine two of them; else add the list to the graph.
     * <p>
     * Notice: automatically sort() according to the name's lexicographical order.
     * </p>
     *
     * @param hNodeList a list to be added
     * @return true for already exist.
     */
    @Override
    public boolean addRowList(HNodeList hNodeList) {
        assert (hNodeList != null);
        int index = Collections.binarySearch(this, hNodeList, Comparator.comparing(o -> o.signName));
        if (index >= 0) {
            this.get(index).addAll(hNodeList);
            // synchronize colSet
            setAddAllNodesName(colSet,hNodeList);
            return true;
        } else {
            add(-index - 1, hNodeList);
            // synchronize colSet
            setAddAllNodesName(colSet,hNodeList);
            return false;
        }
    }


    /**
     * find node head's all nodes.(Can only be used when the AdjList has been sorted)
     *
     * @param tgtNode target node
     */
    public HNodeList sortGetHeadNodesList(String tgtNode) {
        int index = Collections.binarySearch(this, new HNodeList(tgtNode), Comparator.comparing(o -> o.signName));
        if (index < 0) {
            return null;
        } else {
            return this.get(index);
        }
    }

    /**
     * find node'all neighbors which include 2 parts in undirected Graph, or only 1 part in directed one.
     * This method is for undirected graphs.
     * <p>
     *     <ol>
     *         <li>the head nodeList's all connected nodes</li>
     *         <li>other lists's connected nodes</li>
     *     </ol>
     *     <p>A:->B->C</p>
     *     <p>B:->C->D</p>
     *     <p>C:->E</p>
     *     <p>return C's neighbors as a list: E->A->B</p>
     *     <p>which consists of headNodeList starting with C plus list containing nodes from other headLists</p>
     * </p>
     * <br>
     * <p>NOTICE: use it in the condition that the adjList has been sorted.</p>
     *
     * @param tgtNode target node
     */
    public HNodeList sortGetNeighborsList(String tgtNode) {
        int index = Collections.binarySearch(this, new HNodeList(tgtNode), Comparator.comparing(o -> o.signName));
        HNodeList list1;
        if (index < 0) {
            list1 = new HNodeList(tgtNode);
        } else {
            list1 = this.get(index);
        }
        forEach(hList -> {
            if (!hList.getSignName().equals(list1.getSignName())) {
                hList.forEach(node -> {
                    if (node.getStrName().equals(tgtNode)) {
                        list1.sortAdd(new Node(hList.getSignName(), node.getValue()));
                    }
                });
            }
        });
        return list1;
    }

    public HNodeList sortGetNeighborsList(String tgtNode, SimList revList) {
        int index = Collections.binarySearch(this, new HNodeList(tgtNode), Comparator.comparing(HNodeList::getSignName));
        HNodeList list1;
        if (index < 0) {
            list1 = new HNodeList(tgtNode);
        } else {
            list1 = this.get(index);
        }
        for (HNodeList hList : revList) {
            if (hList.getSignName().equals(tgtNode)) {
                hList.forEach(list1::sortAdd);
                break;
            }
        }
        return list1;
    }


    /**
     * remove tgtNode from list(tgtHead).[after sorting]
     *
     * @param tgtHead headName of the list.
     * @param tgtNode name of the node to be removed.
     * @return true for success.
     */
    public boolean sortRemoveNode(String tgtHead, String tgtNode) {
        int index = Collections.binarySearch(this, new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if (index >= 0) {
            this.get(index).remove(tgtNode);
            // synchronize colSet
            colSet.remove(tgtNode);
            return true;
        } else {
            return false;
        }
    }

    /**
     * add tgtHG to list(tgtHead),and keep the target list an ascending order.
     *
     * @param tgtHead headName of the list.
     * @param tgtNode name of the homoGene to be removed.
     * @return true for already node exist.
     */
    public boolean sortAddOneNode(String tgtHead, String tgtNode) {
        int index = Collections.binarySearch(this, new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if (index >= 0) {
            this.get(index).add(tgtNode);
            // synchronize colSet
            colSet.add(tgtNode);
            return true;
        } else {
            add(-index - 1, new HNodeList(tgtHead));
            // synchronize set
            rowSet.add(tgtHead);
            colSet.add(tgtNode);
            this.get(-index - 1).add(tgtNode);
            return false;
        }
    }


    /**
     * add tgtNode to list(tgtHead) with <code>weight</code>,and
     * keep the target list an ascending order.
     *
     * @param tgtHead headName of the list.
     * @param tgtNode name of the Node to be removed.
     * @return index of the row
     */
    public int sortAddOneNode(String tgtHead, String tgtNode, double weight) {
        int index = Collections.binarySearch(this, new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if (index >= 0) {
            this.get(index).sortAdd(tgtNode, weight);
            // synchronize colSet
            colSet.add(tgtNode);
            return index;
        } else {
            // synchronize set
            rowSet.add(tgtHead);
            colSet.add(tgtNode);
            add(-index - 1, new HNodeList(tgtHead));
            this.get(-index - 1).sortAdd(tgtNode, weight);
            return -index-1;
        }
    }




    /**
     * NOTICE:can only be used when the adjList haven't added a new row
     * or mat will get wrong probably
     */
    public void updateMat(int i, int j, double value) {
        if (mat == null) {
            updateMatrix();
        }
        mat.put(i, j, value);
    }

    /**
     * NOTICE:can only be used when the adjList haven't added a new row
     * or mat will get wrong probably
     */
    public void updateMat(String srcNode, String tgtNode, double value) {
        if (colMap == null || rowMap == null) {
            updateMatrix();
        }

        updateMat(rowMap.get(srcNode), colMap.get(tgtNode), value);
    }

    //------------------PUBLIC ACCESS -----------------------------

    @Override
    protected HNodeList getHeadNodesList(String headNodeName) {
        if(mat == null){
            updateMatrix();
        }
        if(rowMap.containsKey(headNodeName)){
            return this.get(rowMap.get(headNodeName));
        }
        return null;
    }

    @Override
    protected boolean removeNode(String tgtHead, String tgtNode) {
        if(mat == null){
            updateMatrix();
        }
        if(rowMap.containsKey(tgtHead)){
            return this.get(rowMap.get(tgtHead)).remove(tgtNode);
        }
        return false;
    }


    public HashMap<String, Integer> getRowMap() {
        return rowMap;
    }
    public HashMap<String, Integer> getColMap() {
        return colMap;
    }

    public Pair<Node, Node> getNodeNameByMatrixIndex(int i, int j) {
        if (mat == null) {
            mat = this.getMatrix();
        }
        if (swapOrderedColMap == null) {
            reverseTpIndexColMap();
        }
        return new Pair<>(new Node(this.get(i).signName), new Node(swapOrderedColMap.get(j), mat.get(i, j)));
    }

    /**
     * mat index of name
     */
    public double getValByMatName(String tgtHead, String tgtNode) {
        if (colMap == null || rowMap == null) {
            updateMatrix();
        }
        int row = rowMap.get(tgtHead);
        int col = colMap.get(tgtNode);
        return mat.get(row, col);
    }

    private void reverseTpIndexColMap() {
        // switch key and value only because of the bi-direction relationship maintained by the NodeList
        swapOrderedColMap = new HashMap<>();
        // sorted by natural order of the key
        TreeMap<String, Integer> colTree = new TreeMap<>(colMap);
        // switch
        colTree.forEach((name, index) -> swapOrderedColMap.put(index, name));
    }


    /**
     * After sorting
     * @param rowName rowName to search
     * @return row index plus the result node
     */
    public Pair<Integer, Node> sortFindMaxOfList(String rowName) {
        int index = Collections.binarySearch(this, new HNodeList(rowName), Comparator.comparing(HNodeList::getSignName));
        if (index >= 0) {
            return new Pair<>(index, this.get(index).findMax());
        } else {
            throw new IllegalArgumentException("Can't the rowName HNodeList in the adjList");
        }
    }

    public Node sortFindMaxOfList(int rowIndex) {
        assert (rowIndex >= 0 && rowIndex < this.size());
        return this.get(rowIndex).findMax();
    }


    /**
     * return the split result and the left result for adjList in which rows have at least h nonzero elements
     *
     * @param h number of nonzero elements
     * @return matrix for split
     */
    public Pair<SimList, SimList> getSplit(int h) {
        SimList split = new SimList();
        SimList left = new SimList();
        forEach(
                list -> {
                    if(list.size() >= h){
                        split.addRowList(list);
                    }
                    else{
                        left.addRowList(list);
                    }
                }
        );
        return new Pair<>(split,left);
    }

    /**
     * Return all edges in the adjList
     *
     * @return Edge set
     */
    public EdgeHashSet getAllEdges() {
        if(allEdges == null){
            allEdges = new EdgeHashSet();
            forEach(list->{
                String hName = list.getSignName();
                list.forEach(
                        node -> {
                            String nodeName = node.getStrName();
                            allEdges.add(hName,nodeName,node.getValue());
                        }
                );
            });
            return allEdges;
        }
        return allEdges;
    }

    public EdgeHashSet getEdgesHasNode(Node source) {
        //TODO neighbors
        EdgeHashSet res = new EdgeHashSet();
        forEach(list->{
            String listName = list.signName;
            list.forEach(node -> {
                String nodeName = node.getStrName();
                if(listName.equals(source.getStrName())||nodeName.equals(source.getStrName())){
                    res.add(listName,nodeName,node.getValue());
                }
            });
        });
        return res;
    }

    public SimList getPart(HashSet<String> rowSet, HashSet<String> colSet) {
        return getPart(rowSet,true).getPart(colSet,false);
    }

    public SimList getPart(HashSet<String> set, boolean isRow) {
        if(rowMap == null || colMap == null){
            updateMatrix();
        }
        SimList res = new SimList();
        if(isRow){
            for (String s : set) {
                res.addRowList(this.get(rowMap.get(s)));
            }
        }
        else{
            this.forEach(h-> h.forEach(n->{
                if(set.contains(n.getStrName())){
                    res.sortAddOneNode(h.signName,n.getStrName(),n.getValue());
                }
            }));
        }
        return res;
    }


}
