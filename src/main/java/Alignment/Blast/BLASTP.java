package Alignment.Blast;

import org.biojava.nbio.core.alignment.matrices.SimpleSubstitutionMatrix;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.template.AbstractCompound;
import org.biojava.nbio.core.sequence.template.AbstractSequence;

import java.util.ArrayList;

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
     *
     * @param wordLength
     * @param wordCutoff
     * @param scoreCutoff
     * @param eCut
     * @param userScoringMatrix
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
    protected int getScore(AminoAcidCompound a, AminoAcidCompound b) {
        return scoringMatrix.getValue(a, b);
    }

    /**
     * Finds high scoring words by scanning each word along the query
     * and returning only ones that yield a pairwise score above a cutoff
     *
     * @param querySeq the query
     * @return an integer array of indexes into the query representing high scoring words
     */
    @Override
    protected int[] findSeeds(AbstractSequence<AbstractCompound> querySeq) {
        ArrayList<Integer> foundSeeds = new ArrayList<Integer>();
        int currScore;
        // this loop walks through the query, breaking it into words
        for (int i = 1; i <= querySeq.getLength() - wordLength; i++) {
            //this loops steps through the full length of the query
            for (int j = 1; j <= querySeq.getLength() - wordLength; j++) {
                currScore = 0;

            }
        }
    }
}
