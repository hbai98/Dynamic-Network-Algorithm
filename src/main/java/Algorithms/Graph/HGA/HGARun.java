///*
// * @Author: Haotian Bai
// * @Github: https://github.com/164140757
// * @Date: 2020-02-18 19:04:13
// * @LastEditors: Haotian Bai
// * @LastEditTime: 2020-05-24 20:05:25
// * @FilePath: \Algorithms\src\main\java\Algorithms\Graph\HGA\HGARun.java
// * @Description:
// */
//package Algorithms.Graph.HGA;
//
//import Algorithms.Graph.Utils.AdjList.SimList;
//import IO.GraphFileReader;
//
//import java.io.IOException;
//import java.util.HashSet;
//
//public class HGARun {
//    HGA hga;
//    SimList graph1;
//    SimList graph2;
//    SimList simList;
//    SimList rev1;
//    SimList rev2;
//
//    /**
//     * @param simPath path of similarity matrix
//     * @param graph_1Path path of graph1
//     * @param graph_2Path path of graph2
//     * @param forcedForSame whether map same nodes in a forced way
//     * @param params
//     * factor:   weight of sequence information, 0 <= factor <=1
//     * tolerance:    error tolerance compared with the last matrix
//     * h:    row has at least h nonzero entries
//     * @throws IOException no graphs or similarity matrix have been loaded
//     *  or the similarity matrix doesn't meet the requirement
//     */
//    HGARun(String simPath, String graph_1Path, String graph_2Path, boolean forcedForSame, Double... params) throws IOException {
//        GraphFileReader reader = new GraphFileReader();
//        simList = reader.readToSimList(simPath,false);
//        if(simList.size()==0){
//            throw new IOException("similarity matrix has not been loaded.");
//        }
//        graph1 = reader.readToSimList(graph_1Path,false);
//        if(graph1.size()==0){
//            throw new IOException("graph1 has not been loaded.");
//        }
//        rev1 = reader.getRevAdjList();
//        // all nodes in graph1[faster than getAllNodes(), because it's from IO]
//        HashSet<String> set1 = reader.getSourceNodesSet();
//        set1.addAll(reader.getTargetNodesSet());
//
//        graph2 = reader.readToSimList(graph_2Path,true);
//        if(graph2.size()==0){
//            throw new IOException("graph2 has not been loaded.");
//        }
//        rev2 = reader.getRevAdjList();
//        // all nodes in graph2
//        HashSet<String> set2 = reader.getSourceNodesSet();
//        set2.addAll(reader.getTargetNodesSet());
//
//        // check whether the simList contains all nodes from graph1 and graph2
//        simListCheck(graph1, graph2);
//        hga = new HGA(simList,graph1,rev1,graph2,rev2);
//        //get stable similarity matrix
//        hga.run(params[0],params[1],params[2].intValue(),forcedForSame);
//    }
//    /**
//     * @param simList similarity matrix
//     * @param graph_1Path path of graph1
//     * @param graph_2Path path of graph2
//     * @param forcedForSame whether map same nodes in a forced way
//     * @param params
//     * factor:   weight of sequence information, 0 <= factor <=1
//     * tolerance:    error tolerance compared with the last matrix
//     * h:    row has at least h nonzero entries
//     * @throws IOException no graphs or similarity matrix have been loaded
//     *  or the similarity matrix doesn't meet the requirement
//     */
//    HGARun(SimList simList, String graph_1Path, String graph_2Path, boolean forcedForSame, Double... params) throws IOException {
//        if(simList == null){
//            throw new IOException("similarity matrix has not been loaded.");
//        }
//        if (params.length!=3){
//            throw new IOException("Your parameters number should be 3.");
//        }
//        GraphFileReader reader = new GraphFileReader();
//        graph1 = reader.readToSimList(graph_1Path,false);
//        if(graph1.size()==0){
//            throw new IOException("graph1 has not been loaded.");
//        }
//        rev1 = reader.getRevAdjList();
//        HashSet<String> set1 = reader.getSourceNodesSet();
//        set1.addAll(reader.getTargetNodesSet());
//
//        graph2 = reader.readToSimList(graph_2Path,true);
//        if(graph2.size()==0){
//            throw new IOException("graph2 has not been loaded.");
//        }
//        rev2 = reader.getRevAdjList();
//        HashSet<String> set2 = reader.getSourceNodesSet();
//        set2.addAll(reader.getTargetNodesSet());
//        // check whether the simList contains all nodes from graph1 and graph2
//        simListCheck(graph1, graph2);
//        hga = new HGA(simList,graph1,rev1,graph2,rev2);
//        //get stable similarity matrix
//        hga.run(params[0],params[1],params[2].intValue(),forcedForSame);
//    }
//
//    /**
//     * Use String path alternatives for better performance
//     * @param simList similarity matrix
//     * @param graph1  graph1
//     * @param graph2  graph2
//     * @param forcedForSame whether map same nodes in a forced way
//     * @param params
//     * factor:  weight of sequence information, 0 <= factor <=1
//     * tolerance:    error tolerance compared with the last matrix
//     * h:    row has at least h nonzero entries
//     * @throws IOException no graphs or similarity matrix have been loaded
//     *  or the similarity matrix doesn't meet the requirement
//     */
//    public HGARun(SimList simList, SimList graph1, SimList graph2, boolean forcedForSame, Double... params) throws IOException {
//        if(simList == null){
//            throw new IOException("similarity matrix has not been loaded.");
//        }
//        if(graph1 == null){
//            throw new IOException("graph1 has not been loaded.");
//        }
//        if(graph2 == null){
//            throw new IOException("graph2 has not been loaded.");
//        }
//        if (params.length!=3){
//            throw new IOException("Your parameters number should be 3.");
//        }
//        this.simList = simList;
//        // check whether the simList contains all nodes from graph1 and graph2
//        simListCheck(graph1, graph2);
//        hga = new HGA(simList,graph1,graph2);
//        hga.run(params[0],params[1],params[2].intValue(),forcedForSame);
//    }
//    /**
//     * Use String path alternatives for better performance
//     * @param simList similarity matrix
//     * @param graph1  graph1
//     * @param rev1 reversed graph1
//     * @param graph2  graph2
//     * @param rev2 reversed graph2
//     * @param forcedForSame whether map same nodes in a forced way
//     * @param params
//     * factor:  weight of sequence information, 0 <= factor <=1
//     * tolerance:    error tolerance compared with the last matrix
//     * h:    row has at least h nonzero entries
//     * @throws IOException no graphs or similarity matrix have been loaded
//     *  or the similarity matrix doesn't meet the requirement
//     */
//    HGARun(SimList simList, SimList graph1, SimList rev1, SimList graph2, SimList rev2, boolean forcedForSame, Double... params) throws IOException {
//        if(simList == null){
//            throw new IOException("similarity matrix has not been loaded.");
//        }
//        if(graph1 == null){
//            throw new IOException("graph1 has not been loaded.");
//        }
//        if(graph2 == null){
//            throw new IOException("graph2 has not been loaded.");
//        }
//        if (params.length!=3){
//            throw new IOException("Your parameters number should be 3.");
//        }
//        this.simList = simList;
//        simListCheck(graph1,graph2);
//        hga = new HGA(simList,graph1,rev1,graph2,rev2);
//        hga.run(params[0],params[1],params[2].intValue(),forcedForSame);
//    }
//
//    /**
//     * SimList can be larger than graphs
//     */
//    private void simListCheck(SimList graph1, SimList graph2) throws IOException {
//        HashSet<String> nodes_1 = graph1.getAllNodes();
//        HashSet<String> nodes_2 = graph2.getAllNodes();
//        // check whether the simList contains all nodes from graph1 and graph2
//        if (!simList.getRowSet().containsAll(nodes_1) || !simList.getColSet().containsAll(nodes_2)) {
//            nodes_1.removeAll(simList.getRowSet());
//            nodes_2.removeAll(simList.getColSet());
//
//            String g1 = "Loss similarity matrix nodes for graph1:"+ nodes_1.toString();
//            String g2 = "\nLoss similarity matrix nodes for graph2:"+ nodes_2.toString();
//            throw new IOException("Similarity matrix is not complete;\n" +
//                    g1 + g2);
//        }
//    }
//
//    public HGA getHga() {
//        return hga;
//    }
//}
