/*
 * @Author: Haotian Bai
 * @Github: https://github.com/164140757
 * @Date: 2020-06-04 08:05:00
 * @LastEditors: Haotian Bai
 * @LastEditTime: 2020-06-04 08:41:26
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
       blast.createDB("src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/Human-20120513.fasta", "src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/Human-20120513.asnb"); 
    }
    
}
