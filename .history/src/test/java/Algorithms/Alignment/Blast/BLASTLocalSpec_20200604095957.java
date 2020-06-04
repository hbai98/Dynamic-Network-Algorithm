/*
 * @Author: Haotian Bai
 * @Github: https://github.com/164140757
 * @Date: 2020-06-04 08:05:00
 * @LastEditors: Haotian Bai
 * @LastEditTime: 2020-06-04 09:51:51
 * @FilePath: \Algorithms\src\test\java\Algorithms\Alignment\Blast\BLASTLocalSpec.java
 * @Description:  
 */ 
package Algorithms.Alignment.Blast;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BLASTLocalSpec {
    BLASTLocal blast;

    @BeforeEach
    void init() {
        blast = new BLASTLocal();
    }

    @Test
    void createDBTest() throws IOException {
       blast.createDB("src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/Human-20120513.fasta", "src/test/java/resources/TestModule/HGATestData/Human-Yeast/DB/Human-20120513","log.txt"); 
     、  blast.createDB("src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/YEAST-uniprot-20120518.fasta", "src/test/java/resources/TestModule/HGATestData/Human-Yeast/DB/YEAST-uniprot-20120518.asnb"); 
    }
    
}
