package Algorithms.Alignment.align;

import IO.FastaFileReader;
import IO.GraphFileWriter;
import Algorithms.Graph.Utils.AdjList.SimList;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;

import java.io.IOException;
import java.util.*;

/**
 * Use mapped proportion
 */
public class Smith_Waterman {
    HashSet<String> seqs1Set;
    HashSet<String> seqs2Set;
    HashSet<String> graph1Nodes;
    HashSet<String> graph2Nodes;
    LinkedHashMap<String, ProteinSequence> seqs2;
    LinkedHashMap<String, ProteinSequence> seqs1;
    // record missing sequences
    HashSet<String> missing1;
    HashSet<String> missing2;

    public Smith_Waterman(HashSet<String> graph1Nodes, HashSet<String> graph2Nodes) {
        seqs1Set = new HashSet<>();
        seqs2Set = new HashSet<>();
        this.graph2Nodes = graph2Nodes;
        this.graph1Nodes = graph1Nodes;
        missing1 = new HashSet<>();
        missing2 = new HashSet<>();
    }

    public SimList run(String seqFilePath_1, String seqFilePath_2) throws Exception {
        FastaFileReader reader = new FastaFileReader(seqFilePath_1, seqFilePath_2);
        seqs1 = reader.getSeq_1();
        seqs2 = reader.getSeq_2();
        SubstitutionMatrix<AminoAcidCompound> matrix = SubstitutionMatrixHelper.getBlosum62();
        SimList simMat = new SimList();
        updateSeqMap();

        for (String graph1Node : graph1Nodes) {
            ProteinSequence seq1 = seqs1.get(graph1Node);
            for (String graph2Node : graph2Nodes) {
                ProteinSequence seq2 = seqs2.get(graph2Node);
                SequencePair<ProteinSequence, AminoAcidCompound> pair = Alignments.getPairwiseAlignment(seq1, seq2,
                        Alignments.PairwiseSequenceAlignerType.LOCAL, new SimpleGapPenalty(), matrix);
                simMat.sortAddOneNode(graph1Node,graph2Node, pair.getPercentageOfIdentity(true));

            }
        }
        return simMat;
    }

    private void updateSeqMap() throws IOException {
        LinkedHashMap<String, ProteinSequence> seqs1_ = new LinkedHashMap<>();
        LinkedHashMap<String, ProteinSequence> seqs2_ = new LinkedHashMap<>();
        for (Map.Entry<String, ProteinSequence> entry : seqs1.entrySet()) {
            String key = entry.getKey().split("\\s+")[0].split("\\[")[0];// "["
            seqs1_.put(key,entry.getValue());
        }
        for (Map.Entry<String, ProteinSequence> entry : seqs2.entrySet()) {
            String key = entry.getKey().split("\\s+")[0].split("\\[")[0];// "["
            seqs2_.put(key,entry.getValue());
        }
        // update
        seqs1 = seqs1_;
        seqs2 = seqs2_;
        //check
        Set<String> keys1 = seqs1.keySet();
        Set<String> keys2= seqs2.keySet();
        if(!keys1.containsAll(graph1Nodes)||!keys2.containsAll(graph2Nodes)){
            graph1Nodes.removeAll(keys1);
            missing1.addAll(graph1Nodes);
            graph2Nodes.removeAll(keys2);
            missing2.addAll(graph2Nodes);
            throw new IOException("similarity matrix can't match with graphs!\n" +
                    "Graph1 miss:"+ Arrays.toString(missing1.toArray())+"\n" +
                    "Graph2 miss:"+ Arrays.toString(missing2.toArray()));
        }

    }

    public void outPutToTxt(String srcFilePath_1, String srcFilePath_2, String outputName, String outPutLoc) throws Exception {
        SimList simMat = run(srcFilePath_1, srcFilePath_2);
        GraphFileWriter writer = new GraphFileWriter();
        writer.writeToTxt(simMat, outputName);
    }

//    --------------------------PUBLIC------------------------------


    public HashSet<String> getSeqs1Set() {
        assert (seqs1Set != null);
        return seqs1Set;
    }

    public HashSet<String> getSeqs2Set() {
        assert (seqs2Set != null);
        return seqs2Set;
    }

}
