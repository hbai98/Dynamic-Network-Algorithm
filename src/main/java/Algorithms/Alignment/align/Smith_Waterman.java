package Algorithms.Alignment.align;

import Algorithms.Alignment.IO.FastaFileReader;
import Algorithms.Graph.IO.GraphFileWriter;
import Algorithms.Graph.Network.AdjList;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Use mapped proportion
 */
public class Smith_Waterman {
    HashSet<String> graph1;
    HashSet<String> graph2;
    public Smith_Waterman(){
        graph1 = new HashSet<>();
        graph2 = new HashSet<>();
    }
    public AdjList run(String filePath_1, String filePath_2) throws Exception {
        FastaFileReader reader = new FastaFileReader(filePath_1, filePath_2);
        LinkedHashMap<String, ProteinSequence> list1 = reader.getSeq_1();
        LinkedHashMap<String, ProteinSequence> list2 = reader.getSeq_2();
        SubstitutionMatrix<AminoAcidCompound> matrix = SubstitutionMatrixHelper.getBlosum62();
        AdjList simMat = new AdjList();

        for (Map.Entry<String, ProteinSequence> entry : list1.entrySet()) {
            String key1 = entry.getKey().split("\\u005B")[0];// "["
            graph1.add(key1);
            ProteinSequence seq1 = entry.getValue();
            for (Map.Entry<String, ProteinSequence> entry2 : list2.entrySet()) {
                String key2 = entry2.getKey().split("\\u005B")[0];
                graph2.add(key2);
                ProteinSequence seq2 = entry2.getValue();
                SequencePair<ProteinSequence, AminoAcidCompound> pair = Alignments.getPairwiseAlignment(seq1, seq2, Alignments.PairwiseSequenceAlignerType.LOCAL, new SimpleGapPenalty(), matrix);
                simMat.sortAddOneNode(key1, key2, pair.getPercentageOfIdentity(true));
            }
        }
        return simMat;
    }

    public void outPutToTxt(String srcFilePath_1, String srcFilePath_2, String outputName, String outPutLoc) throws Exception {
        AdjList simMat = run(srcFilePath_1, srcFilePath_2);
        GraphFileWriter writer = new GraphFileWriter();
        writer.writeToTxt(simMat, outputName, outPutLoc);
    }

//    --------------------------PUBLIC------------------------------


    public HashSet<String> getGraph1() {
        assert (graph1 != null);
        return graph1;
    }

    public HashSet<String> getGraph2() {
        assert (graph2 != null);
        return graph2;
    }
    public static void main(String[] args) throws Exception {
        Smith_Waterman alg = new Smith_Waterman();
        alg.outPutToTxt("src/main/java/resources/coronavirus2019.faa", "src/main/java/resources/sars2003.faa", "smith_watermanRes", "src/main/java/resources");
    }
}
