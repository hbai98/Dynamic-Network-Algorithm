package Alignment;
// Author: Haotian Bai
// Shanghai University, department of computer science

import Alignment.IO.FastaFileReader;
import org.biojava.nbio.core.alignment.SimpleAlignedSequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * This class is used to do protein sequence alignment to get E-VALUE.
 * Reference :
 * <ol>
 *     <li>https://en.wikipedia.org/wiki/BLAST_(biotechnology)#Algorithm</li>
 *      <li>https://github.com/biojava/biojava</li>
 *      <li>javaDoc(bioJava):https://biojava.org/docs/api5.3.0/index.html</li>
 * </ol>
 *
 */
public class GlobalAlignment {
    LinkedHashMap<String, ProteinSequence> seqs_1;
    LinkedHashMap<String, ProteinSequence> seqs_2;
    GlobalAlignment(){

    }
    void readFromFile(String filePath_1,String filePath_2) throws Exception {
        FastaFileReader reader = new FastaFileReader(filePath_1,filePath_2);
        seqs_1 = reader.getSeq_1();
        seqs_2 = reader.getSeq_2();
    }
    void run(){
        
    }
}
