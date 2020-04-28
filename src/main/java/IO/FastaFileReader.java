package IO;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Read files from the gene bank.
 */
public class FastaFileReader {
    LinkedHashMap<String, ProteinSequence> seq_1;
    LinkedHashMap<String, ProteinSequence> seq_2;

    public FastaFileReader(String filePath_1, String filePath_2) throws Exception {
        //create fasta readers to read the sequence file
        seq_1 = FastaReaderHelper.readFastaProteinSequence(new File(filePath_1));
        seq_2 = FastaReaderHelper.readFastaProteinSequence(new File(filePath_2));
    }
//----------------PUBLIC ACCESS-------------------------------------------
    public LinkedHashMap<String, ProteinSequence> getSeq_1() {
        return seq_1;
    }

    public LinkedHashMap<String, ProteinSequence> getSeq_2() {
        return seq_2;
    }

    public static void main(String[] args) throws Exception {
        String root = "src/main/java/resources/";
        FastaFileReader reader = new FastaFileReader(root+"coronavirus2019.faa",root+"sars2003.faa");
    }
}
