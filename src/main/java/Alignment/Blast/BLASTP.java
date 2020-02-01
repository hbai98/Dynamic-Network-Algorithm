package Alignment.Blast;

import org.biojava.nbio.core.alignment.matrices.SimpleSubstitutionMatrix;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.template.AbstractCompound;
import org.biojava.nbio.core.sequence.template.AbstractSequence;

import java.util.ArrayList;
import java.util.function.IntFunction;

/**
 * <p>Reference:https://github.com/dstar4138/pjblast</p>
 *
 * @author: Haotian Bai
 * Shanghai University, the department of computer science
 */
public class BLASTP extends BLAST {
    private SubstitutionMatrix<AminoAcidCompound> scoringMatrix;
    private int wordCutoff;

    /**
     * The default constructor sets parameters for use with the BLOSUM62 matrix
     *
     * @param dbLength the total number of amino acids in the database
     */
    public BLASTP(long dbLength) {
        //these are based on NCBI's defaults
        this.wordLength = 3;
        this.wordCutoff = 13;
        this.scoreCutoff = 23;
        this.scoringMatrix = SimpleSubstitutionMatrix.getBlosum62();
        this.eCutoff = 10;
        this.K = 0.134;    //values obtained from BLAST, page 66
        this.LAM = 0.318;
        this.dbLength = dbLength;
    }

    /**
     * Constructor for using a user-provided scoring matrix. All parameters must be customized for that matrix
     * since the use of another scoring matrix strongly implies differing statistical measures for scoring and analysis
     */
    public BLASTP(int wordLength, int wordCutoff, int scoreCutoff, double eCut, SimpleSubstitutionMatrix<AminoAcidCompound> userScoringMatrix) {
        this.wordLength = wordLength;
        this.scoreCutoff = scoreCutoff;
        this.scoringMatrix = userScoringMatrix;
        this.wordCutoff = wordCutoff;
        this.eCutoff = eCut;
    }

    /**
     * getScore applies the scoring matrix to the two amino acids represented
     * by a and b
     *
     * @param a first amino acid
     * @param b second amino acid
     * @return the score from the matrix
     */
    @Override
    protected <T extends AbstractCompound>int getScore(T a, T b) {
        return scoringMatrix.getValue((AminoAcidCompound) a,(AminoAcidCompound) b);
    }


    /**
     * Finds high scoring words by scanning each word along the query
     * and returning only ones that yield a pairwise score above a cutoff
     * (the sequences used to compare are all the sequences with the size = wordLength )
     * @param querySeq the query
     * @return an integer array of indexes into the query representing high scoring words
     */
    @Override
    protected <T extends AbstractSequence<? extends AbstractCompound>>int[] findSeeds( T querySeq) {
        ArrayList<Integer> foundSeeds = new ArrayList<Integer>();
        int currScore;
        // this loop walks through the query, breaking it into words
        for (int i = 1; i <= querySeq.getLength() - wordLength; i++) {
            //this loops steps through the full length of the query
            for (int j = 1; j <= querySeq.getLength() - wordLength; j++) {
                currScore = 0;
                //score the current word across the entire query
                for(int k = 0; k < wordLength; k++)
                {
                    // specify the compound as the AminoAcidCompound
                    AminoAcidCompound compound_1 = (AminoAcidCompound) querySeq.getCompoundAt(i+k);
                    AminoAcidCompound compound_2 = (AminoAcidCompound) querySeq.getCompoundAt(j+k);
                    currScore += getScore(compound_1,compound_2);
                }
                //if the word obtains a sufficient score against any portion of the query, keep it and stop
                //i: the high-scoring word as indicated by index
                if(currScore >= wordCutoff)
                {
                    foundSeeds.add(i);
                    break;
                }
            }
        }
       return foundSeeds.stream().mapToInt(i->i).toArray();
    }


}
