package DS.Matrix;

import java.util.Arrays;

/**
 * Sparse matrix using the triples
 *
 * @author: Haotian Bai
 * @Blog: www.haotian.life
 */
public class TripleSparseMat {
    // indexes for non_zero entries
    int[] rows;
    int[] cols;
    // non-zero values
    double[] values;
    // current index of the non-zero in the list
    int index = 0;

    /**
     * Initialize triples
     *
     * @param size the size of non-zeros
     */
    public TripleSparseMat(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size of the non-zeros should be a positive integer.");
        }
        this.rows = new int[size];
        this.cols = new int[size];
        this.values = new double[size];
    }

    /**
     * Add a non-zero value into the triple list, if the index exists, the
     * prior one would be replaced by @param value, else, it will add to the
     * last index in order to keep the ascending order.
     *
     * @param row   the row index in the original matrix
     * @param col   the col index in the original matrix
     * @param value the value of the non-zero to be added
     */
    public void add(int row, int col, double value) {
        // safety check
        if(row >= rows.length || row < 0 || col <0 || col >=cols.length){
            throw new IllegalArgumentException("The row or col index are illegal.");
        }
        // binary search
        int idx = Arrays.binarySearch(this.rows, row);
        // not found : add the triple
        if (idx == -1) {
            rows[this.index] = row;
            cols[this.index] = col;
            values[this.index] = value;
            // increase the index
            this.index += 1;
        }
        // found: replace the value
        else {
            rows[idx] = row;
            cols[idx] = col;
            values[idx] = value;
        }
    }
}
