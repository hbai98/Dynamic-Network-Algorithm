package DS.Network;

import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimMat<T> {
    private DoubleMatrix mat;
    //------------------similarity matrix----------
    private HashMap<T, Integer> rowMap;
    private HashMap<T, Integer> colMap;
    //-----------------index name map---------------------
    private HashMap<Integer, T> rowIndexNameMap;
    private HashMap<Integer, T> colIndexNameMap;
    //----------------preferences---------------------
    public Class<T> typeParameterClass;


    public SimMat(Set<T> graph1Nodes, Set<T> graph2Nodes, Class<T> typeParameterClass) {
        this.mat = new DoubleMatrix(graph1Nodes.size(), graph2Nodes.size());
        this.rowMap = new HashMap<>(graph1Nodes.size());
        this.colMap = new HashMap<>(graph2Nodes.size());
        this.rowIndexNameMap = new HashMap<>(graph1Nodes.size());
        this.colIndexNameMap = new HashMap<>(graph2Nodes.size());
        this.typeParameterClass = typeParameterClass;
        // init row,col Map
        initRowColMap(graph1Nodes, graph2Nodes);
        getIndexNameMap();
    }


    private void initRowColMap(Set<T> graph1Nodes, Set<T> graph2Nodes) {
        int i = 0;
        for (T node : graph1Nodes) {
            rowMap.put(node, i++);
        }
        i = 0;
        for (T node : graph2Nodes) {
            colMap.put(node, i++);
        }
    }

    /**
     * The rowMat have to be in the same length and same order
     */
    public SimMat(HashMap<T, Integer> rowMap, HashMap<T, Integer> colMap,
                  DoubleMatrix matrix,
                  Class<T> typeParameterClass
    ) {
        mat = matrix;
        rowIndexNameMap = new HashMap<>(rowMap.size());
        colIndexNameMap = new HashMap<>(colMap.size());
        this.rowMap = rowMap;
        this.colMap = colMap;
        this.typeParameterClass = typeParameterClass;
        getIndexNameMap();
    }


    public SimMat(HashMap<T, Integer> rowMap, HashMap<Integer, T> rowIndexNameMap, HashMap<T, Integer> colMap,
                  HashMap<Integer, T> colIndexNameMap, DoubleMatrix matrix,
                  Class<T> typeParameterClass) {
        mat = matrix;
        this.rowIndexNameMap = rowIndexNameMap;
        this.colIndexNameMap = colIndexNameMap;
        this.typeParameterClass = typeParameterClass;
        this.rowMap = rowMap;
        this.colMap = colMap;
    }


    public void put(T node1, T node2, double val) {
        // only input the nodes in need
        if (rowMap.containsKey(node1) && colMap.containsKey(node2)) {
            int i = rowMap.get(node1);
            int j = colMap.get(node2);
            mat.put(i, j, val);
        }

    }


    private void getIndexNameMap() {
        // switch key and value only because of the bi-direction relationship maintained by the NodeList
        // sorted by natural order of the key
        TreeMap<T, Integer> colTree = new TreeMap<>(colMap);
        TreeMap<T, Integer> rowTree = new TreeMap<>(rowMap);
        // switch
        colTree.forEach((name, index) -> colIndexNameMap.put(index, name));
        rowTree.forEach((name, index) -> rowIndexNameMap.put(index, name));
    }

    public double getVal(T node1, T node2) {
        int i = rowMap.get(node1);
        int j = colMap.get(node2);
        return mat.get(i, j);
    }

    public double getVal(int i, int j) {
        return mat.get(i, j);
    }

    /**
     * return the split result and the left result for adjList in which rows have at least h nonzero elements
     *
     * @param h number of nonzero elements
     * @return matrix for split
     */
    public Pair<SimMat<T>, SimMat<T>> getSplit(int h) {
        HashMap<T, Integer> rowMap_split = new HashMap<>();
        HashMap<T, Integer> rowMap_left = new HashMap<>();
        // record i index for nonZerosMap
        for (int i = 0; i < mat.rows; i++) {
            rowMap_left.put(this.rowIndexNameMap.get(i), i);
        }
        SimMat<T> split_ = setUpSimMat(rowMap_split);
        SimMat<T> left_ = setUpSimMat(rowMap_left);

        return new Pair<>(split_, left_);
    }


    private SimMat<T> setUpSimMat(HashMap<T, Integer> map) {
        DoubleMatrix res = new DoubleMatrix(map.size(), colMap.size());
        // copy other info
        HashMap<T, HashSet<T>> nonZerosOfRowMap = new HashMap<>();
        int j = 0;
        // map values -> indexes in selection
        for (Map.Entry<T, Integer> e : map.entrySet()) {
            res.putRow(j, mat.getRow(e.getValue()));
            j++;
        }

        return new SimMat<>(map, colMap, res, typeParameterClass);
    }


    /**
     * Split the matrix which contains only rows in rowSet and cols in colSet
     *
     * @return split result
     */
    public SimMat<T> getPart(Collection<T> rowSet, Collection<T> colSet) {
        assert (getRowSet().containsAll(rowSet) && getColSet().containsAll(colSet));
        if (rowSet.equals(this.getRowSet()) && colSet.equals(this.getColSet())) {
            return this;
        }
        int i = 0;
        int j = 0;
        int[] rowIndexes = new int[rowSet.size()];
        int[] colIndexes = new int[colSet.size()];
        HashMap<T, Integer> rowMap = new HashMap<>();
        HashMap<T, Integer> colMap = new HashMap<>();
        for (T s : rowSet) {
            rowIndexes[i] = this.rowMap.get(s);
            rowMap.put(s, i++);
        }
        for (T s : colSet) {
            colIndexes[j] = this.colMap.get(s);
            colMap.put(s, j++);
        }
        DoubleMatrix res = mat.get(rowIndexes, colIndexes);
        return new SimMat<>(rowMap, colMap, res, typeParameterClass);
    }

    /**
     * Deep copy
     *
     * @return deep copy result
     */
    public SimMat<T> dup() {
        DoubleMatrix mat = this.mat.dup();
        //------------------similarity matrix----------
        HashMap<T, Integer> rowMap = new HashMap<>(this.rowMap);
        HashMap<T, Integer> colMap = new HashMap<>(this.colMap);
        //-----------------index name map---------------------
        HashMap<Integer, T> rowIndexNameMap = new HashMap<>(this.rowIndexNameMap);
        HashMap<Integer, T> colIndexNameMap = new HashMap<>(this.colIndexNameMap);
        return new SimMat<>(rowMap, rowIndexNameMap, colMap, colIndexNameMap, mat, typeParameterClass);
    }

    public DoubleMatrix getMat() {
        return mat;
    }

    HashMap<T, HashSet<T>> computeNonZeros() {

        HashMap<T, HashSet<T>> nonZeros = new HashMap<>();
        getRowSet().parallelStream().forEach(
                r -> getColSet().parallelStream().forEach(c -> {
                    if (nonZeros.containsKey(r)) {
                        HashSet<T> tmp = nonZeros.get(r);
                        tmp.add(c);
                        nonZeros.put(r, tmp);
                    } else {
                        nonZeros.put(r, new HashSet<>());
                    }
                })
        );
        return nonZeros;
    }

    public HashMap<Integer, T> getRowIndexNameMap() {
        return new HashMap<>(rowIndexNameMap);
    }

    public HashMap<Integer, T> getColIndexNameMap() {
        return new HashMap<>(colIndexNameMap);
    }

    public HashMap<T, Integer> getColMap() {
        return new HashMap<>(colMap);
    }

    public HashMap<T, Integer> getRowMap() {
        return new HashMap<>(rowMap);
    }


    public HashSet<T> getRowSet() {

        return new HashSet<>(this.rowMap.keySet());
    }


    public Set<T> getColSet() {
        return new HashSet<>(this.colMap.keySet());
    }


    public void setColIndexNameMap(HashMap<Integer, T> colIndexNameMap) {
        this.colIndexNameMap = colIndexNameMap;
    }

    public void setRowIndexNameMap(HashMap<Integer, T> rowIndexNameMap) {
        this.rowIndexNameMap = rowIndexNameMap;
    }

    public void setColMap(HashMap<T, Integer> colMap) {
        this.colMap = colMap;
    }

    public void setRowMap(HashMap<T, Integer> rowMap) {
        this.rowMap = rowMap;
    }

    public void setMat(DoubleMatrix mat) {
        this.mat = mat;
    }

    /**
     * @param account Hungarian account
     * @return simMat with rows' average similarity which meet the account[Hungarian mat], Greedy mat
     */
    public Pair<SimMat<T>, SimMat<T>> splitByPercentage(double account) {
        Vector<Pair<T, Double>> rowAves = new Vector<>();
        rowMap.keySet().parallelStream().forEach(r -> {
            int row = rowMap.get(r);
            double ave = mat.getRow(row).sum() / colMap.size();
            rowAves.add(new Pair<>(r, ave));
        });
        // sort
        List<Pair<T, Double>> res = rowAves.stream().sorted(Comparator.comparingDouble(Pair::getSecond)).collect(Collectors.toList());
        int num = (int) (account * rowMap.size());
        HashSet<T> rows = new HashSet<>();
        for (int i = num; i < res.size(); i++) {
            rows.add(res.get(i).getFirst());
        }
        HashSet<T> left = getRowSet();
        left.removeAll(rows);
        SimMat<T> H = getPart(rows, getColSet());
        SimMat<T> G = getPart(left, getColSet());
        return new Pair<>(H, G);
    }

    /**
     * find the max value node with tgt nodes not been assigned,
     *
     * @param assign previous mapping result
     * @return the best node for mapping by greedy algorithm, null for all nodes has been assigned
     */
    public T getMax(int row, HashSet<T> assign) {
        double max = -Double.MAX_VALUE;
        T res = null;
        for (T s : getColSet()) {
            int j = colMap.get(s);
            double val = mat.get(row, j);
            if (!assign.contains(s) && val > max) {
                max = val;
                res = colIndexNameMap.get(j);
            }
        }
        assign.add(res);
        return res;
    }

}
