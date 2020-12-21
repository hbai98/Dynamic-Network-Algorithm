package DS.Matrix;


import org.ejml.MatrixDimensionException;
import org.ejml.data.*;
import org.ejml.simple.SimpleBase;

public class StatisticsMatrix extends SimpleBase<StatisticsMatrix> {
    public StatisticsMatrix() {
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

    public void setMat(Matrix matrix){
        this.setMatrix(matrix);
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
     * Invert the matrix when it's diagonal, which cost time O(n)
     */
    public StatisticsMatrix inverseDig() {
        inverseDig((DMatrix) this.mat);
        return this;
    }

    /**
     * Invert the matrix when it's diagonal, which cost time O(n)
     * @param mat matrix to be handled
     */
    public void inverseDig(DMatrix mat){
        if (mat.getNumCols() != mat.getNumRows()) {
            throw new MatrixDimensionException("Must be a square matrix.");
        }
        for (int i = 0; i < mat.getNumCols(); i++) {
            mat.set(i,i,1/mat.get(i,i));
        }
    }



}