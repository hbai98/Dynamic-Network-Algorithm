package Algorithms.Graph.Utils;

import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

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

    protected AdjList(){
    }

    /**
     * <ol>
     *     <li>Find the longest nodeList,and then initialize the matrix.</li>
     *     <li>Iterate other nodeLists mapping the selected longest.</li>
     * </ol>
     * <p>
     *     Time complexity: O(n+(n-1)*1/2*(1+n)*n) - > O(n^3) -> brute force
     * </p>
     * <p>
     *     construct a HashMap <K,V> where:
     *         <ol>
     *             <li>K: nodeName </li>
     *             <li>V: column position of the matrix</li>
     *         </ol>
     *     <p>
     *         time complexity: O(n + (n-1)*n) -> O(n^2)
     *     </p>
     * </p>
     * @return Similar matrix
     */
    private DoubleMatrix toMatrix() {
        // step 1

        HashMap<String,Integer> map = new HashMap<>();

    }
    // step 1: find the longest nodeList.
    private Pair<Integer,Integer> findLongest(){
        int MaxCol = 0;
        forEach(
                HNodeList ->{
                    
                }
        );
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
     * @param geneToSearch target node
     */
    public HNodeList get(String geneToSearch){
        for(HNodeList hNodeList : this){
            if(hNodeList.signName.equals(geneToSearch)){
                return hNodeList;
            }
        }
        return null;
    }

    /**
     * remove tgtNode from list(tgtHead).
     * @param tgtHead headName of the list.
     * @param tgtNode name of the node to be removed.
     * @return true for success.
     */
    public boolean removeOneNode(String tgtHead, String tgtNode){
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
}
