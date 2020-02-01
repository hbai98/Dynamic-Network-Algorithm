package Alignment.Blast;

import Alignment.IO.FastaFileReader;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.template.AbstractCompound;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.core.sequence.template.SequenceView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Blast is able to ")
class BLASTSpec {
    DNASequence seq1;
    DNASequence seq2;
    BLAST blast;
    @BeforeEach
    void init() throws CompoundNotFoundException {
      seq1 = new DNASequence("ATGCACGTC");
      seq2 = new DNASequence("ACG");
      blast = new BLAST() {
          @Override
          protected<T extends AbstractCompound> int getScore( T a, T b) {
              return 0;
          }

          @Override
          protected <T extends AbstractSequence<? extends AbstractCompound>> int[] findSeeds(T querySeq) {
              return new int[0];
          }
      };
    }
    @DisplayName("find subSequences.")
    @Test
    void indexOfTest() throws CompoundNotFoundException {
        DNASequence pat = blast.subSeq(seq1,5,8);
        int res = blast.indexOf(seq1,pat,1);
        assertEquals(5,res);

//        -------------------------------
        pat = new DNASequence("AAA");
        res = blast.indexOf(seq1,pat,2);
        assertEquals(-1,res);
    }
    @DisplayName("sign the subSequences.")
    @Test
    void findSub() throws CompoundNotFoundException {
        blast.subSeq(seq1,1,2);
        assertEquals(1,seq1.getBioBegin());
        assertEquals(2, seq1.getBioEnd());
    }

    @DisplayName("give the right evalue ")
    @Test
    void E_value() throws Exception {
        FastaFileReader reader = new FastaFileReader("src/main/java/resources/coronavirus2019.faa","src/main/java/resources/sars2003.faa");
        ProteinSequence sequence1 = reader.getSeq_1().get("QHD43415.1 orf1ab polyprotein (pp1ab) [Wuhan seafood market pneumonia virus]");
        ProteinSequence sequence2 = reader.getSeq_2().get("AAP41037.1 spike glycoprotein [Severe acute respiratory syndrome-related coronavirus]");
        BLASTP blastp = new BLASTP();
        System.out.println(blastp.align(sequence1,sequence2));
    }

}