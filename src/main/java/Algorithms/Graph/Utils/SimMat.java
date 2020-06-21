package Algorithms.Graph.Utils;

import Algorithms.Graph.Utils.AdjList.SimList;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimMat {
    private DoubleMatrix mat;
    //------------------similarity matrix----------
    private HashMap<String, Integer> rowMap;
    private HashMap<String, Integer> colMap;
    //-----------------index name map---------------------
    private HashMap<Integer, String> rowIndexNameMap;
    private HashMap<Integer, String> colIndexNameMap;
    private HashMap<String, HashSet<String>> nonZerosIndexMap;
    //----------------preferences---------------------
    public boolean updateNonZerosForRow;

    public SimMat(HashSet<String> graph1Nodes, HashSet<String> graph2Nodes) {
        mat = new DoubleMatrix(graph1Nodes.size(), graph2Nodes.size());
        rowMap = new HashMap<>(graph1Nodes.size());
        colMap = new HashMap<>(graph2Nodes.size());
        rowIndexNameMap = new HashMap<>(graph1Nodes.size());
        colIndexNameMap = new HashMap<>(graph2Nodes.size());

        // init row,col Map
        int i = 0;
        for (String node : graph1Nodes) {
            rowMap.put(node, i++);
        }
        i = 0;
        for (String node : graph2Nodes) {
            colMap.put(node, i++);
        }
        getIndexNameMap();
    }

    /**
     * The rowMat have to be in the same length and same order
     */
    public SimMat(HashMap<String, Integer> rowMap, HashMap<String, Integer> colMap, DoubleMatrix matrix) {
        mat = matrix;
        rowIndexNameMap = new HashMap<>(rowMap.size());
        colIndexNameMap = new HashMap<>(colMap.size());
        this.rowMap = rowMap;
        this.colMap = colMap;
        getIndexNameMap();
    }

    public SimMat(HashMap<String, Integer> rowMap, HashMap<Integer, String> rowIndexNameMap, HashMap<String, Integer> colMap,
                  HashMap<Integer, String> colIndexNameMap, HashMap<String, HashSet<String>> nonZerosMap, DoubleMatrix matrix) {
        mat = matrix;
        this.rowIndexNameMap = rowIndexNameMap;
        this.colIndexNameMap = colIndexNameMap;
        this.rowMap = rowMap;
        this.colMap = colMap;
        this.nonZerosIndexMap = nonZerosMap;
    }

    public SimMat() {

    }


    public void put(String node1, String node2, double val) {
        updateNonZerosForRow(node1, node2, val);
        int i = rowMap.get(node1);
        int j = colMap.get(node2);
        mat.put(i, j, val);
    }

    private void updateNonZerosForRow(String node1, String node2, double val) {
        if (updateNonZerosForRow) {
            // update nonZeros
            HashSet<String> nonZeros = nonZerosIndexMap.get(node1);
            if (val != 0 && !nonZeros.contains(node2)) {
                nonZeros.add(node2);
                nonZerosIndexMap.put(node1, nonZeros);
            }
        }
    }

    private void getIndexNameMap() {
        // switch key and value only because of the bi-direction relationship maintained by the NodeList
        // sorted by natural order of the key
        TreeMap<String, Integer> colTree = new TreeMap<>(colMap);
        TreeMap<String, Integer> rowTree = new TreeMap<>(rowMap);
        // switch
        colTree.forEach((name, index) -> colIndexNameMap.put(index, name));
        rowTree.forEach((name, index) -> rowIndexNameMap.put(index, name));
    }

    public double getVal(String node1, String node2) {
        int i = rowMap.get(node1);
        int j = colMap.get(node2);
        return mat.get(i, j);
    }

    protected double getVal(int i, int j) {
        return mat.get(i, j);
    }

    /**
     * return the split result and the left result for adjList in which rows have at least h nonzero elements
     *
     * @param h number of nonzero elements
     * @return matrix for split
     */
    public Pair<SimMat, SimMat> getSplit(int h) {
        HashMap<String, Integer> rowMap_split = new HashMap<>();
        HashMap<String, Integer> rowMap_left = new HashMap<>();
        // record i index for nonZerosMap
        for (int i = 0; i < mat.rows; i++) {
            if (nonZerosIndexMap.get(rowIndexNameMap.get(i)).size() >= h) {
                rowMap_split.put(this.rowIndexNameMap.get(i), i);
            } else {
                rowMap_left.put(this.rowIndexNameMap.get(i), i);
            }
        }
        SimMat split_ = setUpSimMat(rowMap_split);
        SimMat left_ = setUpSimMat(rowMap_left);

        return new Pair<>(split_, left_);
    }



    private SimMat setUpSimMat(HashMap<String, Integer> map) {
        DoubleMatrix res = new DoubleMatrix(map.size(), colMap.size());
        // copy other info
        HashMap<String, HashSet<String>> nonZerosOfRowMap = new HashMap<>();
        int j = 0;
        // map values -> indexes in selection
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            res.putRow(j, mat.getRow(e.getValue()));
            nonZerosOfRowMap.put(e.getKey(), this.nonZerosIndexMap.get(e.getKey()));
            j++;
        }

        SimMat res_ = new SimMat(map, colMap, res);
        res_.setNonZerosIndexMap(nonZerosOfRowMap);
        return res_;
    }

    /**
     * find the max value node with tgt nodes not been assigned,
     *
     * @param assign previous mapping result
     * @return the best node for mapping by greedy algorithm, null for all nodes has been assigned
     */
    public String getMax(int row, HashSet<String> assign) {
        double max = -Double.MAX_VALUE;
        String res = null;
        HashSet<String> nonZeros = nonZerosIndexMap.get(rowIndexNameMap.get(row));
        // graph2 has not been allocated completely
        if (!assign.equals(nonZeros)) {
            for (String s : nonZeros) {
                int j = colMap.get(s);
                double val = mat.get(row, j);
                if (!assign.contains(s) && val > max) {
                    max = val;
                    res = colIndexNameMap.get(j);
                }
            }
        } else {
            // start zeros allocation
            Set<String> zeros = colMap.keySet();
            zeros.removeAll(nonZeros);
            for (String s : zeros) {
                res = s;
                assign.add(s);
                break;
            }
        }
        return res;
    }

    /**
     * sum up the matrix non-zero entries, and only with non-zero
     *
     * @return sum val
     */
    public double sum() {
        // parallel here there is no interference or stateful lambda
        //https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html
        AtomicReference<Double> sum = new AtomicReference<>((double) 0);
        nonZerosIndexMap.forEach((k, v) -> v.parallelStream().forEach(n -> sum.updateAndGet(v1 -> v1 + getVal(k, n))));
        return sum.get();
    }

    /**
     * Split the matrix which contains only rows in rowSet and cols in colSet
     * @return split result
     */
    public SimMat getPart(Collection<String> rowSet, Collection<String> colSet) {
        assert(getRowSet().containsAll(rowSet)&&getColSet().containsAll(colSet));
        if(rowSet.equals(this.getRowSet())&&colSet.equals(this.getColSet())){
            return this;
        }
        return getPart(rowSet, true).getPart(colSet, false);
    }

    public SimMat getPart(Collection<String> set, boolean isRow) {
        DoubleMatrix mat;
        HashMap<String,Integer> rowMap =new HashMap<>();
        HashMap<Integer,String> rowIndexNameMap = new HashMap<>();
        HashMap<String,Integer> colMap =new HashMap<>();
        HashMap<Integer,String> colIndexNameMap = new HashMap<>();
        HashMap<String,HashSet<String>> nonZerosIndexMap = new HashMap<>();
        if (isRow) {
            Set<String> rowSet = getRowSet();
            colMap = this.colMap;
            colIndexNameMap = this.colIndexNameMap;
            mat = new DoubleMatrix(set.size(),getColSet().size());
            int i = 0;
            for (String s : rowSet) {
                // need
                if (set.contains(s)) {
                    int index = this.rowMap.get(s);
                    rowMap.put(s,i);
                    rowIndexNameMap.put(i,s);
                    nonZerosIndexMap.put(s,this.nonZerosIndexMap.get(s));
                    mat.putRow(i++, this.mat.getRow(index));
                }
            }
        } else {
            Set<String> colSet = getColSet();
            rowMap = this.rowMap;
            rowIndexNameMap = this.rowIndexNameMap;
            nonZerosIndexMap = this.nonZerosIndexMap;
            mat = new DoubleMatrix(getRowSet().size(),set.size());
            int j = 0;
            for (String s : colSet) {
                if(set.contains(s)){
                    int index = this.colMap.get(s);
                    colMap.put(s,j);
                    colIndexNameMap.put(j,s);
                    mat.putColumn(j++,this.mat.getColumn(index));
                }
                else{
                    nonZerosIndexMap.values().forEach(nonZeros-> nonZeros.remove(s));
                }
            }
        }
        this.setMat(mat);
        this.setNonZerosIndexMap(nonZerosIndexMap);
        this.setRowIndexNameMap(rowIndexNameMap);
        this.setColIndexNameMap(colIndexNameMap);
        this.setRowMap(rowMap);
        this.setColMap(colMap);
        // set up matrix
        return this;
    }

    /**
     * Deep copy
     * @return deep copy result
     */
    public Object dup() {
        DoubleMatrix mat = this.mat.dup();
        //------------------similarity matrix----------
        HashMap<String, Integer> rowMap = new HashMap<>(this.rowMap);
        HashMap<String, Integer> colMap = new HashMap<>(this.colMap);
        //-----------------index name map---------------------
        HashMap<Integer, String> rowIndexNameMap = new HashMap<>(this.rowIndexNameMap);
        HashMap<Integer, String> colIndexNameMap = new HashMap<>(this.colIndexNameMap);
        HashMap<String, HashSet<String>> nonZerosIndexMap = new HashMap<>(this.nonZerosIndexMap);
        SimMat res = new SimMat(rowMap,rowIndexNameMap,colMap,colIndexNameMap,nonZerosIndexMap,mat);
        res.updateNonZerosForRow = this.updateNonZerosForRow;
        return res;
    }

    public DoubleMatrix getMat() {
        return mat;
    }

    public HashMap<Integer, String> getRowIndexNameMap() {
        return rowIndexNameMap;
    }

    public HashMap<Integer, String> getColIndexNameMap() {
        return colIndexNameMap;
    }

    public HashMap<String, Integer> getColMap() {
        return colMap;
    }

    public HashMap<String, Integer> getRowMap() {
        return rowMap;
    }

    public HashMap<String, HashSet<String>> getNonZerosIndexMap() {
        return nonZerosIndexMap;
    }

    public HashSet<String> getRowSet() {
        return new HashSet<>(this.rowMap.keySet());
    }


    public Set<String> getColSet() {
        return new HashSet<>(this.colMap.keySet());
    }

    public void setNonZerosIndexMap(HashMap<String, HashSet<String>> nonZerosIndexMap) {
        this.nonZerosIndexMap = nonZerosIndexMap;
    }

    public void setColIndexNameMap(HashMap<Integer, String> colIndexNameMap) {
        this.colIndexNameMap = colIndexNameMap;
    }

    public void setRowIndexNameMap(HashMap<Integer, String> rowIndexNameMap) {
        this.rowIndexNameMap = rowIndexNameMap;
    }

    public void setColMap(HashMap<String, Integer> colMap) {
        this.colMap = colMap;
    }

    public void setRowMap(HashMap<String, Integer> rowMap) {
        this.rowMap = rowMap;
    }

    public void setMat(DoubleMatrix mat) {
        this.mat = mat;
    }
}
