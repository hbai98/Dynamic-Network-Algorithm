package Alignment.align;

import Algorithms.Graph.IO.GraphFileReader;
import Algorithms.Graph.IO.GraphFileWriter;
import Algorithms.Graph.Utils.AdjList;
import Alignment.IO.FastaFileReader;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;

import java.util.LinkedHashMap;

public class Smith_Waterman {
    public static AdjList run(String filePath_1, String filePath_2) throws Exception {
        FastaFileReader reader = new FastaFileReader(filePath_1, filePath_2);
        LinkedHashMap<String, ProteinSequence> list1 = reader.getSeq_1();
        LinkedHashMap<String, ProteinSequence> list2 = reader.getSeq_2();
        SubstitutionMatrix<AminoAcidCompound> matrix = SubstitutionMatrixHelper.getBlosum62();
        AdjList simMat = new AdjList();

        list1.forEach((key1, seq1) -> {
            key1 = key1.split("[")[0];
            list2.forEach((key2,seq2)->{
                SequencePair<ProteinSequence, AminoAcidCompound> pair = Alignments.getPairwiseAlignment(seq1, seq2, Alignments.PairwiseSequenceAlignerType.LOCAL, new SimpleGapPenalty(), matrix);
                simMat.sortAddOneNode(key1,key2,pair.getPercentageOfIdentity(true));
            });
        });
        return simMat;
    }

    public static void outPutToTxt(String srcFilePath_1, String srcFilePath_2,String outputName,String outPutLoc) throws Exception {
        AdjList simMat = run(srcFilePath_1,srcFilePath_2);
        GraphFileWriter writer = new GraphFileWriter();
        writer.writeToTxt(simMat,outputName,outPutLoc);
    }
}
