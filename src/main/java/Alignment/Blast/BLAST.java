package Alignment.Blast;

import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.template.AbstractCompound;
import org.biojava.nbio.core.sequence.template.AbstractSequence;

import java.util.ArrayList;

/**
 * Class BLAST is an abstract base class that implements the original BLAST ungapped
 * alignment algorithm. This class implements components common to both nucleotide and
 * protein sequence alignments.
 * <br>
 * <p>NOTICE: The Class is different than the common BLAST usage, and the main goal is just to
 * output E-Value , which is a bio-tag as a homologous coefficient to indicate to what extend the
 * query sequence is similar to the target -</p>
 * <br>
 * <p>Reference:https://github.com/dstar4138/pjblast</p>
 * @author: Haotian Bai
 * Shanghai University, the department of computer science
 */
abstract public class BLAST {
    protected int scoreCutoff;
    protected int wordLength;
    protected double eCutoff, K, LAM;
    protected long dbLength;

    /**
     * Determines the score between a pair of residues
     *
     * @param a the first residue
     * @param b the second residue
     * @return the score of a paired with b
     */
    protected abstract int getScore(AminoAcidCompound a, AminoAcidCompound b) ;

    /**
     * Finds the set of significant words from a query sequence
     *
     * @param querySeq a sequence to be decomposed into words
     * @return an array of indices representing the position of each word
     */
    protected abstract int[] findSeeds(AbstractSequence<AbstractCompound> querySeq);

    /**
     * Performs the BLAST ungapped alignment by finding all significant
     * matches of the query in the subject
     *
     * @param querySeq the query sequence
     * @param subjectSeq the subject sequence
     * @return E-VALUE
     */
    public double align(AbstractSequence<AbstractCompound> querySeq, AbstractSequence<AbstractCompound> subjectSeq){
        ArrayList<HSP> hits = new ArrayList<HSP>();
        int[] seeds;
        int queryIndex, subjectIndex;
        double eScore;

        //0 ： find the significant seeds
        seeds = findSeeds(querySeq);
    }
}
