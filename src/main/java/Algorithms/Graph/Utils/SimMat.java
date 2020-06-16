package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.EdgeHashSet;
import Algorithms.Graph.Utils.AdjList.SimList;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.util.*;

public class SimMat {
    private DoubleMatrix mat;
    //------------------similarity matrix----------
    private HashMap<String, Integer> rowMap;
    private HashMap<String, Integer> colMap;
    //-----------------index name map---------------------
    private HashMap<Integer, String> rowIndexNameMap;
    private HashMap<Integer, String> colIndexNameMap;
    private ArrayList<Double> maxOfRowMap;
    private ArrayList<Double> nonZerosOfRowMap;

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
     * The rowMat have to be in the same length
     * @param rowMat a row matrix
     */
    public SimMat(DoubleMatrix... rowMat){
        mat = new DoubleMatrix(rowMat.length,rowMat[1].length);
//        mat.putRow();
    }
    public SimMat(HashSet<String> graph1Nodes,HashSet<String> graph2Nodes, ArrayList<Double> maxOfRowMap,ArrayList<Double> nonZerosOfRowMap){
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
        this.maxOfRowMap = maxOfRowMap;
        this.nonZerosOfRowMap = nonZerosOfRowMap;
    }


    protected void put(int i,int j,double val){
        mat.put(i,j,val);
    }
    public void put(String node1, String node2, double val){
        int i = rowMap.get(node1);
        int j = colMap.get(node2);
        mat.put(i,j,val);
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
    protected double getVal(int i,int j){
        return mat.get(i,j);
    }
    protected double getVal(String node1,String node2){
        int i = rowMap.get(node1);
        int j = colMap.get(node2);
        return mat.get(i,j);
    }

    /**
     * return the split result and the left result for adjList in which rows have at least h nonzero elements
     *
     * @param h number of nonzero elements
     * @return matrix for split
     */
    public Pair<SimMat, SimMat> getSplit(int h) {
        Vector<Integer> record = new Vector<>();
        for (int i = 0; i < mat.length; i++) {
            if(nonZerosOfRowMap.get(i)>=h){
                record.add(i);
            }
        }
        SimMat split = new SimMat(record.size(),);
        SimMat left = new SimMat();
        return new Pair<>(split,left);
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

    public ArrayList<Double> getMaxOfRowMap() {
        return maxOfRowMap;
    }

    public ArrayList<Double> getNonZerosOfRowMap() {
        return nonZerosOfRowMap;
    }
}
