/*
 * @Author: Haotian Bai
 * @Github: https://github.com/164140757
 * @Date: 2020-06-04 07:50:51
 * @LastEditors: Haotian Bai
 * @LastEditTime: 2020-06-04 07:58:36
 * @FilePath: \Algorithms\src\main\java\Algorithms\Alignment\Blast\BLASTLocal.java
 * @Description:  
 */ 
package Algorithms.Alignment.Blast;

import Algorithms.Graph.Network.AdjList;

public class BLASTLocal {
    public void run(String seqPath,String tgtPath){
        StringBuffer command = new StringBuffer();
        command.append("cmd /c d: ");
        command.append(String.format("&& makeblastdb -in %s 
        -parse",seqPath));
        
    }
}
