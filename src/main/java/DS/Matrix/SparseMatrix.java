package DS.Matrix;

import org.ejml.data.DMatrixSparseCSC;
import org.ejml.sparse.csc.CommonOps_DSCC;

public class SparseMatrix extends StatisticsMatrix {
    public SparseMatrix() {

    }

    public SparseMatrix(int rows, int cols, int dataLen){
        // create new mat
        DMatrixSparseCSC mat = new DMatrixSparseCSC(rows, cols, dataLen);
        // setMat
        this.setMatrix(mat);
    }

    /**
     * @param rows the row number of the matrix
     * @param cols the column number of the matrix
     * @param row non-zero indices of rows
     * @param col non-zero indices of cols
     * @param data non-zero entries
     */
    public SparseMatrix(int rows, int cols, int[] row, int[] col, double[] data) {
        // check whether the triple has the same size and is out of the boundary
        if (row.length > rows*cols || row.length != col.length || row.length != data.length) {
            throw new IllegalArgumentException("Illegal matrix initialization.");
        }
        // create new mat
        DMatrixSparseCSC mat = new DMatrixSparseCSC(rows, cols, data.length);
        // iterate the index and put the value into the matrix
        for (int i = 0; i < row.length; i++) {
            int r = row[i];
            int c = col[i];
            double val = data[i];
            // r, c will be checked at set()
            mat.set(r, c, val);
        }
        // setMat
        this.setMatrix(mat);
    }

    /**
     * Create a diagonal matrix
     *
     * @param data diagonal matrix's data
     * @return diagonal matrix
     */
    public static SparseMatrix createDia(double[] data) {
        SparseMatrix mat = new SparseMatrix();
        mat.setMatrix(CommonOps_DSCC.diag(data));
        return mat;
    }


    public static SparseMatrix createIdentity(int tgtSize) {
        SparseMatrix mat = new SparseMatrix();
        mat.setMatrix(CommonOps_DSCC.identity(tgtSize));
        return mat;
    }
}
