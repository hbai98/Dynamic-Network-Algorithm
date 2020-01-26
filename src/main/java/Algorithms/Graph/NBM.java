package Algorithms.Graph;

import Algorithms.Graph.IO.AdjListFileReader;
import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.Node;
import Algorithms.Graph.Network.NodeList;
import Algorithms.Graph.Utils.AdjList;
import org.jblas.DoubleMatrix;
import org.jgrapht.alg.util.Pair;

import java.io.IOException;
import java.util.*;

public class NBM {
    protected AdjList simList;
    private HashSet<String> graph_1;
    private HashSet<String> graph_2;
    private HashMap<String,String> bestPairNodeG1;
    private HashMap<String,Double> bestPairWeightG1;

    protected PriorityQueue<Edge> pqEdge;

    /**
     * NBM
     * @param simList AdjList to represent the similarity matrix
     */
    protected NBM(AdjList simList){
        init(simList);
        // step 1
//        findBestPairs();
        // step 2

    }

    /**
     * NBM
     * get HashSet from the reader.
     * @param simList AdjList to represent the similarity matrix
     * @param graph_1 HashSet from reader
     * @param graph_2 HashSet from reader
     */
    protected NBM(AdjList simList,HashSet<String> graph_1,HashSet<String> graph_2){
        init(simList,graph_1,graph_2);
        // step 1
//        findBestPairs();
    }

    private void init(AdjList simList, HashSet<String> graph_1, HashSet<String> graph_2) {
        this.graph_1 = graph_1;
        this.graph_2 = graph_2;
        this.simList = simList;
        // pq
        pqEdge = new PriorityQueue<>(Comparator.comparingDouble(Edge::getWeight));
    }

    private void init(AdjList simList){
        this.simList = simList;
        this.graph_1 = simList.getRowSet();
        this.graph_2 = simList.getColSet();
        // pq
        pqEdge = new PriorityQueue<>(Comparator.comparingDouble(Edge::getWeight));
    }
    /**
     * iterate(nodeList) nodes in Graph1 and finds every node it's best pair( with the greatest weight )
     */
    protected void findBestPairs() {
        assert(simList!=null&&graph_1!=null&&graph_2!=null);
        HashMap<String,Integer> rowMap = simList.getRowMap();
        graph_1.forEach(strNode->{
            int row = rowMap.get(strNode);
            Pair<Integer, Node> res = simList.findMaxOfList(row);
            int col = res.getFirst();
            Node tgtNode = res.getSecond();
            pqEdge.add(new Edge(new Node(strNode),tgtNode,row,col,tgtNode.getValue()));
            bestPairNodeG1.put(strNode, tgtNode.getStrName());
            bestPairWeightG1.put(strNode,tgtNode.getValue());
        });
    }


}
