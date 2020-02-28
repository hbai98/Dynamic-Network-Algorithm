package Algorithms.Graph.Network;

import Algorithms.Graph.Utils.HNodeList;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.io.IOException;
import java.util.*;

/**
 * This class is meant to make a graph based on adjacent list which
 * has already been defined in Network, and the next step is to
 * provide another LinkList to wrap NodeLists.
 * <p>
 * The order has remained lexicographically ascending, which can then
 * be characterized as a matrix. This matrix is maintained simultaneously
 * for the convenience of shifting the matrix to Hungarian (which can only be
 * manipulated using matrices.)
 * </p>
 */
public class AdjList extends LinkedList<HNodeList> {

    private DoubleMatrix mat;
    //------------------similarity matrix----------
    private HashSet<String> rowSet;
    private HashSet<String> colSet;
    private HashSet<String> allNodes;
    private EdgeHasSet allEdges;

    private HashMap<String, Integer> rowMap;
    private HashMap<String, Integer> colMap;
    //---------------Reverse the adjList--------------> src <-> tgt
    private AdjList revList;
    //---------------colMap mat index -> node's name-------------
    private HashMap<Integer, String> swapOrderedColMap;

    public AdjList() {

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
    public DoubleMatrix toMatrix() {
        if (mat == null) {
            // step 1: initialize the matrix.
            HashSet<String> colSet = init();
            // step 2：create the dictionary
            HashMap<String, Integer> colList = dict(colSet);
            // step 3 : get the matrix
            mapping(colList);
        }
        // return copy, keep mat unchanged
        return mat.dup();

    }

    protected DoubleMatrix toMatrix(HashSet<String> graph_1, HashSet<String> graph_2) {
        if (mat == null) {
            // step 1: initialize the matrix.
            init(graph_1, graph_2);
            // step 2：create the dictionary
            HashMap<String, Integer> colList = dict(graph_2);
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
     * iterate over the adjList to init.
     * return colSet.
     */
    private HashSet<String> init() {
        int row = 0;
        HashSet<String> colSet = new HashSet<>();
        HashSet<String> rowSet = new HashSet<>();

        for (HNodeList list : this) {
            row++;
            rowSet.add(list.getSignName());
            for (Node node : list) {
                colSet.add(node.getStrName());
            }
        }
        this.rowSet = rowSet;
        this.colSet = colSet;
        mat = new DoubleMatrix(row, colSet.size());
        return colSet;
    }

    /**
     * step 2：create the dictionary
     *
     * @return map for the matrix's column
     */
    private HashMap<String, Integer> dict(HashSet<String> colSet) {
        // node name , index
        ArrayList<String> colList = new ArrayList<String>(colSet);
        colList.sort(Comparator.naturalOrder());
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < colList.size(); i++) {
            hashMap.put(colList.get(i), i);
        }
        colMap = hashMap;

        return hashMap;
    }

    /**
     * Step 3:
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
            list.forEach(node -> {
                rowVector.put(colMap.get(node.getStrName()), node.getValue());
            });
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
    public boolean add(HNodeList hNodeList) {
        assert (hNodeList != null);
        int index = Collections.binarySearch(this, hNodeList, Comparator.comparing(o -> o.signName));
        if (index >= 0) {
            this.get(index).addAll(hNodeList);
            return true;
        } else {
            add(-index - 1, hNodeList);
            return false;
        }
    }


    /**
     * find node head's all nodes.
     *
     * @param tgtNode target node
     */
    public HNodeList getHeadNodesList(String tgtNode) {
        for (HNodeList hNodeList : this) {
            if (hNodeList.getSignName().equals(tgtNode)) {
                return hNodeList;
            }
        }
        return null;
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
    public HNodeList sortGetNeighborsList(String tgtNode) throws IOException {
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

    public HNodeList sortGetNeighborsList(String tgtNode, AdjList revList) {
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
     * remove tgtNode from list(tgtHead).
     *
     * @param tgtHead headName of the list.
     * @param tgtNode name of the node to be removed.
     * @return true for success.
     */
    public boolean removeAllNode(String tgtHead, String tgtNode) {
        int index = Collections.binarySearch(this, new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if (index >= 0) {
            this.get(index).remove(tgtNode);
            return true;
        } else {
            return false;
        }
    }

    /**
     * add tgtHG to list(tgtHead).
     *
     * @param tgtHead headName of the list.
     * @param tgtNode name of the homoGene to be removed.
     * @return true for already node exist.
     */
    // TODO a little duplicated !
    public boolean addOneNode(String tgtHead, String tgtNode) {
        int index = Collections.binarySearch(this, new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if (index >= 0) {
            this.get(index).add(tgtNode);
            return true;
        } else {
            add(-index - 1, new HNodeList(tgtHead));
            this.get(-index - 1).add(tgtNode);
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
        return sortAddOneNode(tgtHead, tgtNode, 0);
    }

    /**
     * add tgtNode to list(tgtHead) with <code>weight</code>.
     *
     * @param tgtHead headName of the list.
     * @param tgtNode name of the Node to be removed.
     * @return true for node already exist.
     */
    public boolean addOneNode(String tgtHead, String tgtNode, double weight) {
        int index = Collections.binarySearch(this, new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if (index >= 0) {
            this.get(index).add(tgtNode, weight);
            return true;
        } else {
            add(-index - 1, new HNodeList(tgtHead));
            this.get(-index - 1).add(tgtNode, weight);
            return false;
        }
    }

    /**
     * add tgtNode to list(tgtHead) with <code>weight</code>,and
     * keep the target list an ascending order.
     *
     * @param tgtHead headName of the list.
     * @param tgtNode name of the Node to be removed.
     * @return true for head already exist.
     */
    public boolean sortAddOneNode(String tgtHead, String tgtNode, double weight) {
        int index = Collections.binarySearch(this, new HNodeList(tgtHead), Comparator.comparing(o -> o.signName));
        if (index >= 0) {
            this.get(index).sortAdd(tgtNode, weight);
            return true;
        } else {
            add(-index - 1, new HNodeList(tgtHead));
            this.get(-index - 1).sortAdd(tgtNode, weight);
            return false;
        }
    }


    public AdjList getRevList() {
        AdjList rev = new AdjList();
        forEach(list -> {
            String headName = list.getSignName();
            list.forEach(node -> {
                rev.sortAddOneNode(node.getStrName(), headName, node.getValue());
            });
        });
        return rev;
    }

    /**
     * NOTICE:can only be used when the adjList haven't added a new row
     * or mat will get wrong probably
     */
    public void updateMat(int i, int j, double value) {
        if (mat == null) {
            mat = this.toMatrix();
        }
        mat.put(i, j, value);
    }

    /**
     * NOTICE:can only be used when the adjList haven't added a new row
     * or mat will get wrong probably
     */
    public void updateMat(String srcNode, String tgtNode, double value) {
        if (colMap == null) {
            colMap = getColMap();
        }
        if (rowMap == null) {
            rowMap = getRowMap();
        }
        updateMat(rowMap.get(srcNode), colMap.get(tgtNode), value);
    }

    //------------------PUBLIC ACCESS -----------------------------


    public HashSet<String> getColSet() {
        if (mat == null) {
            mat = this.toMatrix();
            return colSet;
        }
        return colSet;
    }

    public HashSet<String> getRowSet() {
        if (mat == null) {
            mat = this.toMatrix();
            return rowSet;
        }
        return rowSet;
    }

    public HashMap<String, Integer> getRowMap() {

        if (rowMap == null) {
            mat = this.toMatrix();
        }
        return rowMap;
    }

    public HashMap<String, Integer> getColMap() {
        if (colMap == null) {
            mat = this.toMatrix();
        }
        return colMap;
    }

    public DoubleMatrix getMat() {
        if (mat != null) {
            return mat;
        }
        return this.toMatrix();
    }

    public Pair<Node, Node> getNodeNameByMatrixIndex(int i, int j) {
        if (mat == null) {
            mat = this.toMatrix();
        }
        if (swapOrderedColMap == null) {
            getIndexColMap();
        }
        return new Pair<>(new Node(this.get(i).signName), new Node(swapOrderedColMap.get(j), mat.get(i, j)));
    }

    /**
     * mat index of name
     */
    public double getValByMatName(String tgtHead, String tgtNode) {
        if (rowMap == null) {
            rowMap = getRowMap();
        }
        if (colMap == null) {
            colMap = getColMap();
        }
        int row = rowMap.get(tgtHead);
        int col = colMap.get(tgtNode);
        return mat.get(row, col);
    }

    private void getIndexColMap() {
        // switch key and value only because of the bi-direction relationship maintained by the NodeList
        swapOrderedColMap = new HashMap<>();
        // sorted by natural order of the key
        TreeMap<String, Integer> colTree = new TreeMap<>(colMap);
        // switch
        colTree.forEach((name, index) -> {
            swapOrderedColMap.put(index, name);
        });
    }

    public double getValByName(String tgtHead, String tgtNode) throws IOException {
        int index = Collections.binarySearch(this, new HNodeList(tgtHead), Comparator.comparing(HNodeList::getSignName));
        if (index >= 0) {
            return this.get(index).sortFindByName(tgtNode).getValue();
        } else {
            throw new IOException("Can't find the target head");
        }
    }


    /**
     * @param rowName rowName to search
     * @return row index plus the result node
     */
    public Pair<Integer, Node> findMaxOfList(String rowName) {
        int index = Collections.binarySearch(this, new HNodeList(rowName), Comparator.comparing(HNodeList::getSignName));
        if (index >= 0) {
            return new Pair<Integer, Node>(index, this.get(index).findMax());
        } else {
            throw new IllegalArgumentException("Can't the rowName HNodeList in the adjList");
        }
    }

    public Node findMaxOfList(int rowIndex) {
        assert (rowIndex >= 0 && rowIndex < this.size());
        return this.get(rowIndex).findMax();
    }

    public HashSet<String> getAllNodes() {
        if (allNodes == null) {
            if (rowSet == null) {
                rowSet = getRowSet();
            }
            if (colSet == null) {
                colSet = getColSet();
            }
            // merge
            HashSet<String> tpRow = (HashSet) rowSet.clone();
            tpRow.addAll(colSet);
            allNodes = tpRow;
        }
        return allNodes;
    }

    /**
     * return the split result for adjList in which rows have at least h nonzero elements
     *
     * @param h number of nonzero elements
     * @return matrix for split
     */
    public AdjList getSplit(int h) {
        AdjList res = new AdjList();
        forEach(
                list -> {
                    if (list.getNonZeroNumb() >= h) {
                        res.add(list);
                    }
                }
        );
        return res;
    }

    /**
     * Return all edges in the adjList
     *
     * @return Edge set
     */
    public EdgeHasSet getAllEdges() {
        if(allEdges == null){
            allEdges = new EdgeHasSet();
            EdgeHasSet res = new EdgeHasSet();
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

    public EdgeHasSet getEdgesHasNode(Node source) {
        EdgeHasSet res = new EdgeHasSet();
        forEach(list->{
            String listName = list.signName;
            list.forEach(node -> {
                String nodeName = node.strName;
                if(listName.equals(source.strName)||nodeName.equals(source.strName)){
                    res.add(listName,nodeName,node.getValue());
                }
            });
        });
        return res;
    }
}
