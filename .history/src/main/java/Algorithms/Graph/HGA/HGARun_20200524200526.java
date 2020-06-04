/*
 * @Author: Haotian Bai
 * @Github: https://github.com/164140757
 * @Date: 2020-02-18 19:04:13
 * @LastEditors: Haotian Bai
 * @LastEditTime: 2020-05-24 20:05:25
 * @FilePath: \Algorithms\src\main\java\Algorithms\Graph\HGA\HGARun.java
 * @Description:  
 */ 
package Algorithms.Graph.HGA;

import IO.GraphFileReader;
import Algorithms.Graph.Network.AdjList;

import java.io.IOException;
import java.util.HashSet;
import java.util.IllegalFormatException;

public class HGARun {
    HGA hga;
    AdjList graph1;
    AdjList graph2;
    AdjList simList;
    AdjList rev1;
    AdjList rev2;

    HGARun(String simPath, String graph_1Path, String graph_2Path) throws IOException {
        GraphFileReader reader = new GraphFileReader();
        simList = reader.readToAdjL(simPath,false);
        HashSet<String> headSim = reader.getHeadSet();
        HashSet<String> listSim = reader.getListSet();

        graph1 = reader.readToAdjL(graph_1Path,false);
        if(graph1.size()==0){
            throw new IOException("graph1 has not been loaded.");
        }
        rev1 = reader.getRevAdjList();
        HashSet<String> set1 = reader.getHeadSet();
        set1.addAll(reader.getListSet());

        graph2 = reader.readToAdjL(graph_2Path,true);
        if(graph2.size()==0){
            throw new IOException("graph2 has not been loaded.");
        }
        rev2 = reader.getRevAdjList();
        HashSet<String> set2 = reader.getHeadSet();
        set2.addAll(reader.getListSet());

        hga = new HGA(simList,graph1,rev1,graph2,rev2);
        //get stable similarity matrix
        hga.run(0.5,0.01,5);
    }
    HGARun(AdjList simList, String graph_1Path, String graph_2Path) throws IOException {
        GraphFileReader reader = new GraphFileReader();
        HashSet<String> headSim = reader.getHeadSet();
        HashSet<String> listSim = reader.getListSet();

        graph1 = reader.readToAdjL(graph_1Path,false);
        if(graph1.size()==0){
            throw new IOException("graph1 has not been loaded.");
        }
        rev1 = reader.getRevAdjList();
        HashSet<String> set1 = reader.getHeadSet();
        set1.addAll(reader.getListSet());

        graph2 = reader.readToAdjL(graph_2Path,true);
        if(graph2.size()==0){
            throw new IOException("graph2 has not been loaded.");
        }
        rev2 = reader.getRevAdjList();
        HashSet<String> set2 = reader.getHeadSet();
        set2.addAll(reader.getListSet());

        hga = new HGA(simList,graph1,rev1,graph2,rev2);
        //get stable similarity matrix
        hga.run(0.5,0.01,5);
    }

    HGARun(AdjList simList, AdjList graph1, AdjList graph2) throws IOException {

        hga = new HGA(simList,graph1,graph2);
        hga.run(0.5,0.01,5);
    }
    HGARun(AdjList simList, AdjList graph1,AdjList rev1, AdjList graph2,AdjList rev2) throws IOException {
//

        hga = new HGA(simList,graph1,rev1,graph2,rev2);
        hga.run(0.5,0.01,5);
    }




    public HGA getHga() {
        return hga;
    }
}
