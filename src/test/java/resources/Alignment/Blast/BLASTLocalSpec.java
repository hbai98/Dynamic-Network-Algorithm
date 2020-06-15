///*
// * @Author: Haotian Bai
// * @Github: https://github.com/164140757
// * @Date: 2020-06-04 08:05:00
// * @LastEditors: Haotian Bai
// * @LastEditTime: 2020-06-04 14:42:37
// * @FilePath: \Algorithms\src\test\java\Algorithms\Alignment\Blast\BLASTLocalSpec.java
// * @Description:
// */
//package Algorithms.Alignment.Blast;
//
//import java.io.IOException;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class BLASTLocalSpec {
//    BLASTLocal blast;
//
//    @BeforeEach
//    void init() {
//        blast = new BLASTLocal();
//    }
//
//    @Test
//    void createDBTest() throws IOException {
//       blast.createDB("src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/Human-20120513.fasta", "src/test/java/resources/TestModule/HGATestData/Human-Yeast/DB/Human-20120513",null);
//       blast.createDB("src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/YEAST-uniprot-20120518.fasta", "src/test/java/resources/TestModule/HGATestData/Human-Yeast/DB/YEAST-uniprot-20120518",null);
//    }
//
//    @Test
//    void blastp() throws IOException {
//        blast.blastp("src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/YEAST-uniprot-20120518.fasta",
//                "src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/Human-20120513.fasta",
//                "src/test/java/resources/TestModule/HGATestData/Human-Yeast/fasta/yeastHumanSimList_EvalueLessThan1e-10.txt");
//    }
////    blastp -query "YEAST-uniprot-20120518.fasta" -subject "Human-20120513.fasta" -out "yeastHumanSimList_EvalueLessThan1e-10.txt" -outfmt "6 qseqid sseqid evalue" -evalue 1e-10
//
//}
