package DS.Matrix;

/**
 * Simple matrix
 */
public class SimpleMatrix {
    // data compressed into the one-dimensional type
    double[] data;

    /**
     * Allocate a matrix with row*col.
     * @param row the row of the matrix
     * @param col the column of the matrix
     */
    public SimpleMatrix(int row, int col){
        if(row <0 || col <0){
            throw new IllegalArgumentException("The width and length of the matrix should be a positive integer.");
        }
        this.data = new double[row*col];
    }
    /**
     * Allocate a matrix with row*col, and data would be transferred into the matrix one by one.
     * @param row the row of the matrix
     * @param col the column of the matrix
     */
    public SimpleMatrix(int row, int col, double[] data){
        if(row <0 || col <0){
            throw new IllegalArgumentException("The width and length of the matrix should be a positive integer.");
        }
        if(data.length > row*col){
            throw new IllegalArgumentException("The data's length is longer than row*col.");
        }
        this.data = new double[row*col];
        // transfer
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    /**
     * Insert a value into the matrix.
     * @param row the row index of the value to be added
     * @param col the col index of the value to be added
     * @param val the value to be added
     */
    public void add(int row, int col, double val){
        // safe-check
         
    }


}
