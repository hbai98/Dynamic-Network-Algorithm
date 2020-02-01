package Alignment.Blast;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.search.io.Hsp;
import org.biojava.nbio.core.sequence.IntronSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.template.*;

import java.util.ArrayList;
import java.util.Iterator;

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
    protected abstract<T extends AbstractCompound> int getScore(T a, T b) ;

    /**
     * Finds the set of significant words from a query sequence
     *
     * @param querySeq a sequence to be decomposed into words
     * @return an array of indices representing the position of each word
     */
    protected abstract<T extends AbstractSequence<? extends AbstractCompound>> int[] findSeeds(T querySeq);

    /**
     * Performs the BLAST ungapped alignment by finding all significant
     * matches of the query in the subject
     *
     * @param querySeq the query sequence
     * @param subjectSeq the subject sequence
     * @return E-VALUE
     */
    public <T extends AbstractSequence<? extends AbstractCompound>>double align( T querySeq, T subjectSeq){
        ArrayList<HSP> hits = new ArrayList<HSP>();
        int[] seeds;
        int queryIndex, subjectIndex;
        double eScore = 0.0;

        //0 ： find the significant seeds
        seeds = findSeeds(querySeq);

        //1: find all exact matches between a word and some position in the subject
        //start from 2 position, 1 position is unused
        int pos = 2;
        for (int seed : seeds) {
            pos = indexOf(subjectSeq, subSeq(querySeq, seed, seed + wordLength), pos);
            while(pos != -1){
                //HSP(word's position in query, word's position in subject)
                hits.add(new HSP(seed, pos));
                //HSP region
                pos = indexOf(subjectSeq,subSeq(querySeq,seed,seed+wordLength-1),pos+1);
            }
            pos = 2;
        }

        //2: look for exact matches for seed words in the subject
        for(HSP hsp: hits){
            int currScore, fScore = 0, rScore = 0, alignscore = 0, difference = 0, startRange = 0, endRange = 0;

            //extend forward
            queryIndex = hsp.qPos + wordLength;
            subjectIndex = hsp.sPos + wordLength;
            while(queryIndex < querySeq.getLength() && subjectIndex < subjectSeq.getLength())
            {
                fScore = alignscore;
                alignscore += getScore(querySeq.getCompoundAt(queryIndex),subjectSeq.getCompoundAt(subjectIndex));
                difference += (fScore - alignscore);

                //stop extending if the accumulated difference reaches the cutoff
                if(difference >= scoreCutoff) break;

                endRange++;
                queryIndex++;
                subjectIndex++;
            }
            //reset for next extension
            difference = 0;
            alignscore = 0;
            queryIndex = hsp.qPos - 1;
            subjectIndex = hsp.sPos - 1;

            //extend backwards
            while(queryIndex > 0 && subjectIndex > 0)
            {
                rScore = alignscore;
                alignscore += getScore(querySeq.getCompoundAt(queryIndex),subjectSeq.getCompoundAt(subjectIndex));
                difference += (rScore - alignscore);

                //stop extending if the accumulated difference reaches the cutoff
                if(difference >= scoreCutoff) break;

                startRange++;
                queryIndex--;
                subjectIndex--;
            }
            currScore = rawScore(subSeq(querySeq,hsp.qPos - startRange,hsp.qPos + wordLength + endRange),subSeq(subjectSeq,hsp.sPos - startRange,hsp.sPos + wordLength + endRange));
            eScore = findEScore(currScore,querySeq.getLength()-1,subjectSeq.getLength()-1);
        }
        return eScore;
    }

    /**
     * mark the start and end of the sequence, [start, end-1]
     * @return the polished sequence
     */
    protected <T extends AbstractSequence<? extends AbstractCompound>> T subSeq(T sequence, int start, int end){
        sequence.setBioBegin(start);
        sequence.setBioEnd(end);
        return sequence;
    }

    /**
     * A basic search function that finds the first occurrence of pat in
     * seq, beginning at start
     *
     * @param seq a sequence
     * @param pat the substring to be searched for
     * @param start the start index in str
     * @return the index of the first occurrence of pat in seq, or -1 if not found
     */
    <T extends AbstractSequence<? extends AbstractCompound>>int indexOf(T seq, T pat, int start)
    {
        int found = -1;
        int patLen = pat.getBioEnd() - pat.getBioBegin();
        if(patLen == 0) {
            return found;
        }
        int seqLen = seq.getLength();
        for(int i = start; i < seqLen - patLen && found == -1; i++)
        {
            int j = 0;
            // getCompoundAt() index starts from 1.
            while(j < patLen && seq.getCompoundAt(i+j) == pat.getCompoundAt(pat.getBioBegin()+j)) {
                j++;
            }
            if(j == patLen){
                found = i;
            }
        }
        return found;
    }

    /**
     * Calculates the raw score (sum of all pairwise scores)
     * of two aligned strings, as scored by the scoring function
     *
     * @param seq1 first sequence
     * @param seq2 second sequence
     * @return the raw score
     */
    <T extends AbstractSequence<? extends AbstractCompound>> int rawScore(T seq1, T seq2)
    {
        int score = 0;
        for(int i = seq1.getBioBegin(), j = seq2.getBioBegin(); i < seq1.getBioEnd() && j < seq2.getBioEnd(); i++,j++)
        {
            score += getScore(seq1.getCompoundAt(i),seq2.getCompoundAt(j));
        }
        return score;
    }

    /**
     * Computes the expect score for an alignment
     *
     * @param score the alignment's raw score
     * @param qLen the length of the aligned range in the query
     * @param sLen the total length of the subject space (ex. the database size)
     * @return the alignment's E score
     */
    private double findEScore(int score, int qLen, long sLen)
    {
        double result;
        result = K * qLen * sLen * Math.exp(-score*LAM);
        return result;
    }

}
