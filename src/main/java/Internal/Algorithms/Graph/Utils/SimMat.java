package Internal.Algorithms.Graph.Utils;

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
        initRowColMap(graph1Nodes, graph2Nodes);
        getIndexNameMap();
    }

    private void initRowColMap(HashSet<String> graph1Nodes, HashSet<String> graph2Nodes) {
        int i = 0;
        for (String node : graph1Nodes) {
            rowMap.put(node, i++);
        }
        i = 0;
        for (String node : graph2Nodes) {
            colMap.put(node, i++);
        }
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
        if (updateNonZerosForRow) {
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
                assign.add(res);
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
        } else {
            for (String s : getColSet()) {
                int j = colMap.get(s);
                double val = mat.get(row, j);
                if (!assign.contains(s) && val > max) {
                    max = val;
                    res = colIndexNameMap.get(j);
                }
            }
            assign.add(res);
        }
        return res;
    }

    /**
     * sum up the matrix non-zero entries, and only with non-zero map
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
     *
     * @return split result
     */
    public SimMat getPart(Collection<String> rowSet, Collection<String> colSet) {
        assert (getRowSet().containsAll(rowSet) && getColSet().containsAll(colSet));
        if (rowSet.equals(this.getRowSet()) && colSet.equals(this.getColSet())) {
            return this;
        }
        int i = 0;
        int j = 0;
        int[] rowIndexes = new int[rowSet.size()];
        int[] colIndexes = new int[colSet.size()];
        HashMap<String, Integer> rowMap = new HashMap<>();
        HashMap<String, Integer> colMap = new HashMap<>();
        for (String s : rowSet) {
            rowIndexes[i] = this.rowMap.get(s);
            rowMap.put(s, i++);
        }
        for (String s : colSet) {
            colIndexes[j] = this.colMap.get(s);
            colMap.put(s, j++);
        }
        DoubleMatrix res = mat.get(rowIndexes, colIndexes);
        return new SimMat(rowMap, colMap, res);
    }

    /**
     * Deep copy
     *
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
        HashMap<String, HashSet<String>> nonZerosIndexMap_ = null;
        if (this.nonZerosIndexMap != null) {
            nonZerosIndexMap_ = new HashMap<>(this.nonZerosIndexMap);
        }
        SimMat res = new SimMat(rowMap, rowIndexNameMap, colMap, colIndexNameMap, nonZerosIndexMap_, mat);
        res.updateNonZerosForRow = this.updateNonZerosForRow;
        return res;
    }

    public DoubleMatrix getMat() {
        return mat;
    }

    HashMap<String, HashSet<String>> computeNonZeros() {
        if (nonZerosIndexMap != null) {
            return nonZerosIndexMap;
        }
        HashMap<String, HashSet<String>> nonZeros = new HashMap<>();
        getRowSet().parallelStream().forEach(
                r -> getColSet().parallelStream().forEach(c -> {
                    if (nonZeros.containsKey(r)) {
                        HashSet<String> tmp = nonZeros.get(r);
                        tmp.add(c);
                        nonZeros.put(r, tmp);
                    } else {
                        nonZeros.put(r, new HashSet<>());
                    }
                })
        );
        return nonZeros;
    }

    public HashMap<Integer, String> getRowIndexNameMap() {
        return new HashMap<>(rowIndexNameMap);
    }

    public HashMap<Integer, String> getColIndexNameMap() {
        return new HashMap<>(colIndexNameMap);
    }

    public HashMap<String, Integer> getColMap() {
        return new HashMap<>(colMap);
    }

    public HashMap<String, Integer> getRowMap() {
        return new HashMap<>(rowMap);
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

    /**
     * @param account Hungarian account
     * @return simMat with rows' average similarity which meet the account[Hungarian mat], Greedy mat
     */
    public Pair<SimMat, SimMat> splitByPercentage(double account) {
        Vector<Pair<String, Double>> rowAves = new Vector<>();
        rowMap.keySet().parallelStream().forEach(r -> {
            int row = rowMap.get(r);
            double ave = mat.getRow(row).sum() / colMap.size();
            rowAves.add(new Pair<>(r, ave));
        });
        // sort
        List<Pair<String, Double>> res = rowAves.stream().sorted(Comparator.comparingDouble(Pair::getSecond)).collect(Collectors.toList());
        int num = (int) (account * rowMap.size());
        HashSet<String> rows = new HashSet<>();
        for (int i = num; i < res.size(); i++) {
            rows.add(res.get(i).getFirst());
        }
        HashSet<String> left = getRowSet();
        left.removeAll(rows);
        SimMat H = getPart(rows, getColSet());
        SimMat G = getPart(left, getColSet());
        return new Pair<>(H, G);
    }
}
