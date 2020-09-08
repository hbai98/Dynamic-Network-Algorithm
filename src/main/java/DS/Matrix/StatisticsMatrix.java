package DS.Matrix;


import DS.Matrix.Alg.CommonOps_DDRM_extend;
import org.ejml.data.*;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.simple.SimpleBase;
import org.ejml.simple.SimpleMatrix;
import org.ejml.sparse.csc.CommonOps_DSCC;

public class StatisticsMatrix extends SimpleBase<StatisticsMatrix> {
    public StatisticsMatrix(int numRows, int numCols) {
        super(numRows, numCols);
    }

    public StatisticsMatrix(int row, int col, boolean rowMajor, double[] data) {
        DMatrixRMaj mat = new DMatrixRMaj(row, col, rowMajor, data);
        this.setMatrix(mat);
    }

    public StatisticsMatrix(double[] data) {
        this.setMatrix(new DMatrixRMaj(data));
    }

    protected StatisticsMatrix() {
    }
    public StatisticsMatrix(int numRows, int numCols, MatrixType type) {
        switch(type) {
            case DDRM:
                this.setMatrix(new DMatrixRMaj(numRows, numCols));
                break;
            case FDRM:
                this.setMatrix(new FMatrixRMaj(numRows, numCols));
                break;
            case ZDRM:
                this.setMatrix(new ZMatrixRMaj(numRows, numCols));
                break;
            case CDRM:
                this.setMatrix(new CMatrixRMaj(numRows, numCols));
                break;
            case DSCC:
                this.setMatrix(new DMatrixSparseCSC(numRows, numCols));
                break;
            case FSCC:
                this.setMatrix(new FMatrixSparseCSC(numRows, numCols));
                break;
            default:
                throw new RuntimeException("Unknown matrix type");
        }

    }

    /**
     * Returns a matrix of StatisticsMatrix type so that SimpleMatrix functions create matrices
     * of the correct type.
     */
    @Override
    protected StatisticsMatrix createMatrix(int numRows, int numCols, MatrixType matrixType) {
        return new StatisticsMatrix(numRows, numCols,matrixType);
    }

    /**
     * Wraps a StatisticsMatrix around 'm'.  Does NOT create a copy of 'm' but saves a reference
     * to it.
     */
    @Override
    protected StatisticsMatrix wrapMatrix(Matrix matrix) {
        StatisticsMatrix ret = new StatisticsMatrix();
        ret.mat = matrix;
        return ret;
    }

    /**
     * Get all elements from the specified rows and columns.
     *
     * @param mat  mat to be handled
     * @param rows row indexes
     * @param cols col indexes
     * @return sub-matrix
     */
    public StatisticsMatrix getMat(StatisticsMatrix mat, int[] rows, int[] cols) {
        StatisticsMatrix ret = new StatisticsMatrix(rows.length,cols.length);
        double[] array = cut(mat, rows, cols);
        ret.setMatrix(new DMatrixRMaj(rows.length,cols.length,true,array));
        return ret;
    }

    private double[] cut(StatisticsMatrix mat, int[] rows, int[] cols) {
        double[] array = new double[rows.length * cols.length];
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < cols.length; j++) {
                array[i * cols.length + j] = mat.get(rows[i], cols[j]);
            }
        }
        return array;
    }


    /**
     * Return a row of elements from the matrix in SimpleBase
     *
     * @param row row to get
     * @return a row from matrix
     */
    public StatisticsMatrix getRow(int row) {
        return this.rows(row, row + 1);
    }

    /**
     * Get the minimum value of the matrix
     *
     * @return minimum of the matrix
     */
    public double getMinDouble() {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < this.numRows(); i++) {
            for (int j = 0; j < this.numCols(); j++) {
                double val = get(i, j);
                if (val < min) {
                    min = val;
                }
            }
        }
        return min;
    }


    /**
     * Replace the row using one-dimensional matrix
     *
     * @param row    row to replace
     * @param matrix one 1D row matrix
     */
    public void rRow(int row, StatisticsMatrix matrix) {
        if (matrix.numRows() != 1) {
            throw new RuntimeException("The new matrix input should be a row vector.");
        }
        for (int i = 0; i < numCols(); i++) {
            this.set(row, i, matrix.get(0, i));
        }
    }

    /**
     * Get all matrix double data as one dimensional array
     *
     * @return all matrix data
     */
    public double[] data() {
        DMatrixRMaj mat_ = (DMatrixRMaj) mat;
        return mat_.data;
    }

    private int index(int i, int j) {
        return i * numCols() + j;
    }

    public static StatisticsMatrix createIdentity(int n) {
        DMatrix ret;
        StatisticsMatrix res = new StatisticsMatrix();
        ret = CommonOps_DDRM.identity(n);
        res.setMatrix(ret);
        return res;
    }

    /**
     * Invert the matrix when it's diagonal, which cost time O(n)
     */
    public StatisticsMatrix inverseDig() {
        CommonOps_DDRM_extend.inverseDig((DMatrix) this.mat);
        return this;
    }

}