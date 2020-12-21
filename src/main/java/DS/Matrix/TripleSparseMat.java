package DS.Matrix;

import java.util.*;

/**
 * Sparse matrix using the triples
 *
 * @author: Haotian Bai
 * @Blog: www.haotian.life
 */
public class TripleSparseMat {
    // use a triple to store a non-zero indexes and a value
    static class Triple{
        public Triple(int row){
            this.row = row;
            this.cols = new ArrayList<>();
            this.vals = new ArrayList<>();
        }
        public int row;
        // column indexes in the row
        public List<Integer> cols;
        public List<Double> vals;

        /**
         * Add a col index and val into the triple
         * @param col column index
         * @param val value
         */
        public void add(int col, double val){
            int idx = Collections.binarySearch(this.cols,col);
            // not found
            if(idx == -1){
                this.cols.add(col);
                this.vals.add(val);
            }
            // found, then replace
            else{
                this.cols.set(idx, col);
                this.vals.set(idx, val);
            }
        }
    }
    // indexes for non_zero entries
    List<Triple> non_zero_list;

    /**
     * Initialize triples
     *
     */
    public TripleSparseMat() {
        non_zero_list = new ArrayList<>();
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
        if(row < 0 || col <0){
            throw new IllegalArgumentException("The row or col index are illegal.");
        }
        // binary search
        int idx = Collections.binarySearch(this.non_zero_list, new Triple(row), Comparator.comparingInt(o -> o.row));
        // not found : add the triple
        if (idx == -1) {
            Triple triple = new Triple(row);
            triple.add(col, value);
            non_zero_list.add(triple);
        }
        // found: insert
        else {
            non_zero_list.get(idx).add(col, value);
        }
    }
}
