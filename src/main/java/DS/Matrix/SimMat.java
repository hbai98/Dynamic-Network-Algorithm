package DS.Matrix;

import org.ejml.data.DMatrixRMaj;
import org.ejml.data.MatrixType;
import org.jgrapht.alg.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class SimMat<K> {
    protected StatisticsMatrix mat;
    //------------------similarity matrix----------
    protected HashMap<K, Integer> rowMap;
    protected HashMap<K, Integer> colMap;
    //-----------------index name map---------------------
    protected HashMap<Integer, K> rowIndexNameMap;
    protected HashMap<Integer, K> colIndexNameMap;
    public Class<K> mapKeyType;

    /**
     * Construct a similarity matrix based on nodes from two graph
     *
     * @param g1          graph1 all nodes
     * @param g2          graph2 all nodes
     * @param mapKeyClass similarity matrix row and column index maps, key class
     */
    public SimMat(Set<K> g1, Set<K> g2, Class<K> mapKeyClass) {
        initMat(g1, g2);
        this.rowMap = new HashMap<>(g1.size());
        this.colMap = new HashMap<>(g2.size());
        this.rowIndexNameMap = new HashMap<>(g1.size());
        this.colIndexNameMap = new HashMap<>(g2.size());
        this.mapKeyType = mapKeyClass;
        // init row,col Map
        initRowColMap(g1, g2);
        getIndexNameMap();
    }

    private void initMat(Set<K> g1, Set<K> g2) {
        this.mat = new StatisticsMatrix(g1.size(), g2.size());
    }


    private void initRowColMap(Set<K> graph1Nodes, Set<K> graph2Nodes) {
        int i = 0;
        for (K node : graph1Nodes) {
            rowMap.put(node, i++);
        }
        i = 0;
        for (K node : graph2Nodes) {
            colMap.put(node, i++);
        }
    }

    /**
     * The rowMat have to be in the same length and same order
     */
    public SimMat(HashMap<K, Integer> rowMap, HashMap<K, Integer> colMap,
                  StatisticsMatrix Matrix,
                  Class<K> mapKeyType
    ) {
        mat = Matrix;
        rowIndexNameMap = new HashMap<>(rowMap.size());
        colIndexNameMap = new HashMap<>(colMap.size());
        this.rowMap = rowMap;
        this.colMap = colMap;
        this.mapKeyType = mapKeyType;
        getIndexNameMap();
    }


    public SimMat(HashMap<K, Integer> rowMap, HashMap<Integer, K> rowIndexNameMap, HashMap<K, Integer> colMap,
                  HashMap<Integer, K> colIndexNameMap, StatisticsMatrix Matrix,
                  Class<K> mapKeyType) {
        mat = Matrix;
        this.rowIndexNameMap = rowIndexNameMap;
        this.colIndexNameMap = colIndexNameMap;
        this.rowMap = rowMap;
        this.colMap = colMap;
    }


    public void put(K node1, K node2, double val) {
        // only input the nodes in need
        if (rowMap.containsKey(node1) && colMap.containsKey(node2)) {
            int i = rowMap.get(node1);
            int j = colMap.get(node2);
            mat.set(i, j, val);
        }
    }


    private void getIndexNameMap() {
        // switch key and value only because of the bi-direction relationship maintained by the NodeList
        // sorted by natural order of the key
        TreeMap<K, Integer> colTree = new TreeMap<>(colMap);
        TreeMap<K, Integer> rowTree = new TreeMap<>(rowMap);
        // switch
        colTree.forEach((name, index) -> colIndexNameMap.put(index, name));
        rowTree.forEach((name, index) -> rowIndexNameMap.put(index, name));
    }

    public double getVal(K node1, K node2) {
        int i = rowMap.get(node1);
        int j = colMap.get(node2);
        return getVal(i, j);
    }


    public double getVal(int i, int j) {
        return mat.get(i, j);
    }

    /**
     * Split the matrix which contains only rows in rowSet and cols in colSet
     *
     * @return split result
     */
    public SimMat<K> getPart(Collection<K> rowSet, Collection<K> colSet) {
        assert (getRowSet().containsAll(rowSet) && getColSet().containsAll(colSet));
        if (rowSet.equals(this.getRowSet()) && colSet.equals(this.getColSet())) {
            return this;
        }
        int i = 0;
        int j = 0;
        int[] rowIndexes = new int[rowSet.size()];
        int[] colIndexes = new int[colSet.size()];
        HashMap<K, Integer> rowMap = new HashMap<>();
        HashMap<K, Integer> colMap = new HashMap<>();
        for (K s : rowSet) {
            rowIndexes[i] = this.rowMap.get(s);
            rowMap.put(s, i++);
        }
        for (K s : colSet) {
            colIndexes[j] = this.colMap.get(s);
            colMap.put(s, j++);
        }
        StatisticsMatrix res = mat.getMat(mat, rowIndexes, colIndexes);
        return new SimMat<>(rowMap, colMap, res, mapKeyType);
    }

    /**
     * Deep copy
     *
     * @return deep copy result
     */
    public SimMat<K> dup() {
        StatisticsMatrix mat = this.mat.copy();
        //------------------similarity matrix----------
        HashMap<K, Integer> rowMap = new HashMap<>(this.rowMap);
        HashMap<K, Integer> colMap = new HashMap<>(this.colMap);
        //-----------------index name map---------------------
        HashMap<Integer, K> rowIndexNameMap = new HashMap<>(this.rowIndexNameMap);
        HashMap<Integer, K> colIndexNameMap = new HashMap<>(this.colIndexNameMap);
        return new SimMat<>(rowMap, rowIndexNameMap, colMap, colIndexNameMap, mat,mapKeyType);
    }

    public StatisticsMatrix getMat() {
        return mat.copy();
    }


    public HashMap<Integer, K> getRowIndexNameMap() {
        return new HashMap<>(rowIndexNameMap);
    }

    public HashMap<Integer, K> getColIndexNameMap() {
        return new HashMap<>(colIndexNameMap);
    }

    public HashMap<K, Integer> getColMap() {
        return new HashMap<>(colMap);
    }

    public HashMap<K, Integer> getRowMap() {
        return new HashMap<>(rowMap);
    }


    public HashSet<K> getRowSet() {

        return new HashSet<>(this.rowMap.keySet());
    }


    public Set<K> getColSet() {
        return new HashSet<>(this.colMap.keySet());
    }

    public void setColIndexNameMap(HashMap<Integer, K> colIndexNameMap) {
        this.colIndexNameMap = colIndexNameMap;
    }

    public void setRowIndexNameMap(HashMap<Integer, K> rowIndexNameMap) {
        this.rowIndexNameMap = rowIndexNameMap;
    }

    public void setColMap(HashMap<K, Integer> colMap) {
        this.colMap = colMap;
    }

    public void setRowMap(HashMap<K, Integer> rowMap) {
        this.rowMap = rowMap;
    }

    public void setMat(StatisticsMatrix mat) {
        this.mat = mat;
    }

    /**
     * @param account Hungarian account
     * @return simMat with rows' average similarity which meet the account[Hungarian mat], Greedy mat
     */
    public Pair<SimMat<K>, SimMat<K>> splitByPercentage(double account) {
        Vector<Pair<K, Double>> rowAves = new Vector<>();
        rowMap.keySet().parallelStream().forEach(r -> {
            int row = rowMap.get(r);
            double ave = mat.getRow(row).elementSum()/ colMap.size();
            rowAves.add(new Pair<>(r, ave));
        });
        // sort
        List<Pair<K, Double>> res = rowAves.stream().sorted(Comparator.comparingDouble(Pair::getSecond)).collect(Collectors.toList());
        int num = (int) (account * rowMap.size());
        HashSet<K> rows = new HashSet<>();
        for (int i = num; i < res.size(); i++) {
            rows.add(res.get(i).getFirst());
        }
        HashSet<K> left = getRowSet();
        left.removeAll(rows);
        SimMat<K> H = getPart(rows, getColSet());
        SimMat<K> G = getPart(left, getColSet());
        return new Pair<>(H, G);
    }

    /**
     * find the max value node with tgt nodes not been assigned,
     *
     * @param assign previous mapping result
     * @return the best node for mapping by greedy algorithm, null for all nodes has been assigned
     */
    public K getMax(int row, HashSet<K> assign) {
        double max = -Double.MAX_VALUE;
        K res = null;
        for (K s : getColSet()) {
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

    /**
     * Set data using array
     * @param out one dimension array
     */
    public void setData(double[] out) {
        assert(mat.getMatrix().getType().equals(MatrixType.DDRM));
        mat.getDDRM().setData(out);
    }

}
