package Algorithms.Alignment.align;

import Algorithms.Alignment.IO.FastaFileReader;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("the algorithm is able to ")
class Smith_WatermanSpec {
    @DisplayName("split the string with [. ")
    @Test
    void splitTest(){
        String test = "sdkfksf kdjfk [fdfdf] ";
        assertEquals("sdkfksf kdjfk",test.split("\\u005B")[0]);
    }

}