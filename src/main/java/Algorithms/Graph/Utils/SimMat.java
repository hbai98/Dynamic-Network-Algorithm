package Algorithms.Graph.Utils;

import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.util.*;

public class SimMat implements Cloneable {
    private DoubleMatrix mat;
    //------------------similarity matrix----------
    private HashMap<String, Integer> rowMap;
    private HashMap<String, Integer> colMap;
    //-----------------index name map---------------------
    private HashMap<Integer, String> rowIndexNameMap;
    private HashMap<Integer, String> colIndexNameMap;
    private HashMap<String,HashSet<String>> nonZerosIndexMap;
    //----------------preferences---------------------
    public boolean updateNonZerosForRow;

    public SimMat(HashSet<String> graph1Nodes,HashSet<String> graph2Nodes){
        mat = new DoubleMatrix(graph1Nodes.size(),graph2Nodes.size());
        rowMap = new HashMap<>(graph1Nodes.size());
        colMap = new HashMap<>(graph2Nodes.size());
        rowIndexNameMap = new HashMap<>(graph1Nodes.size());
        colIndexNameMap = new HashMap<>(graph2Nodes.size());

        // init row,col Map
        int i = 0;
        for (String node : graph1Nodes) {
            rowMap.put(node,i++);
        }
        i = 0;
        for (String node : graph2Nodes) {
            colMap.put(node,i++);
        }
        getIndexNameMap();
    }

    /**
     * The rowMat have to be in the same length and same order
     */
    public SimMat(HashMap<String,Integer> rowMap,HashMap<String,Integer> colMap,DoubleMatrix matrix){
        mat = matrix;
        rowIndexNameMap = new HashMap<>(rowMap.size());
        colIndexNameMap = new HashMap<>(colMap.size());

        // init row,col Map
        this.rowMap = rowMap;
        this.colMap = colMap;
        getIndexNameMap();
    }
    public SimMat(HashSet<String> graph1Nodes,HashSet<String> graph2Nodes,HashMap<String,HashSet<String>>  nonZerosIndexMap){
        mat = new DoubleMatrix(graph1Nodes.size(),graph2Nodes.size());
        rowMap = new HashMap<>(graph1Nodes.size());
        colMap = new HashMap<>(graph2Nodes.size());
        rowIndexNameMap = new HashMap<>(graph1Nodes.size());
        colIndexNameMap = new HashMap<>(graph2Nodes.size());

        // init row,col Map
        int i = 0;
        for (String node : graph1Nodes) {
            rowMap.put(node,i++);
        }
        i = 0;
        for (String node : graph2Nodes) {
            colMap.put(node,i++);
        }
        getIndexNameMap();
        this.nonZerosIndexMap = nonZerosIndexMap;
    }

    public void put(String node1, String node2, double val){
        updateNonZerosForRow(node1,node2,val);
        int i = rowMap.get(node1);
        int j = colMap.get(node2);
        mat.put(i,j,val);
    }

    private void updateNonZerosForRow(String node1, String node2, double val){
        if(updateNonZerosForRow){
            // update nonZeros
            HashSet<String> nonZeros = nonZerosIndexMap.get(node1);
            if(val!=0 && !nonZeros.contains(node2)){
                nonZeros.add(node2);
                nonZerosIndexMap.put(node1,nonZeros);
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
    public double getVal(String node1, String node2){
        int i = rowMap.get(node1);
        int j = colMap.get(node2);
        return mat.get(i,j);
    }
    protected double getVal(int i,int j){
        return mat.get(i,j);
    }
    /**
     * return the split result and the left result for adjList in which rows have at least h nonzero elements
     *
     * @param h number of nonzero elements
     * @return matrix for split
     */
    public Pair<SimMat, SimMat> getSplit(int h) {
        HashMap<String,Integer> rowMap_split = new HashMap<>();
        HashMap<String,Integer> rowMap_left = new HashMap<>();
        for (int i = 0; i < mat.rows; i++) {
            if(nonZerosIndexMap.get(rowIndexNameMap.get(i)).size()>=h){
                rowMap_split.put(this.rowIndexNameMap.get(i),i);
            }
            else{
                rowMap_left.put(this.rowIndexNameMap.get(i),i);
            }
        }
        SimMat split_ = setUpSimMat(rowMap_split);
        SimMat left_ = setUpSimMat(rowMap_left);

        return new Pair<>(split_,left_);
    }

    private SimMat setUpSimMat(HashMap<String,Integer> map) {
        DoubleMatrix res = new DoubleMatrix(map.size(),colMap.size());
        // copy other info
        HashMap<String,HashSet<String>> nonZerosOfRowMap = new HashMap<>();
        int j = 0;
        // map values -> indexes in selection
        for(Map.Entry<String,Integer> e:map.entrySet()){
            res.putRow(j, mat.getRow(e.getValue()));
            nonZerosOfRowMap.put(e.getKey(),this.nonZerosIndexMap.get(e.getKey()));
            j++;
        }

        SimMat res_ = new SimMat(map,colMap,res);
        res_.setNonZerosIndexMap(nonZerosOfRowMap);
        return res_;
    }
    /**
     * find the max value node with tgt nodes not been assigned,

     * @param assign previous mapping result
     * @return the best node for mapping by greedy algorithm, null for all nodes has been assigned
     */
    public String getMax(int row, HashSet<String> assign) {
        double max = -Double.MAX_VALUE;
        String res = null;
        HashSet<String> nonZeros = nonZerosIndexMap.get(rowIndexNameMap.get(row));
        // graph2 has not been allocated completely
        if(!assign.equals(nonZeros)){
            for(String s:nonZeros) {
                int j = colMap.get(s);
                double val = mat.get(row,j);
                if (!assign.contains(s) && val > max) {
                    max = val;
                    res = colIndexNameMap.get(j);
                }
            }
        }
        else{
            // start zeros allocation
            Set <String> zeros = colMap.keySet();
            zeros.removeAll(nonZeros);
            for (String s : zeros) {
                res = s;
                assign.add(s);
                break;
            }
        }
        return res;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
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

    public HashMap<String,HashSet<String>>  getNonZerosIndexMap() {
        return nonZerosIndexMap;
    }

    public void setNonZerosIndexMap(HashMap<String,HashSet<String>>  nonZerosIndexMap) {
        this.nonZerosIndexMap = nonZerosIndexMap;
    }


}
