package DS.Matrix;

import org.ejml.data.DMatrixRMaj;

public class DenseMatrix extends StatisticsMatrix{
    public DenseMatrix(){

    }

    public DenseMatrix(int row, int col){
        DMatrixRMaj mat = new DMatrixRMaj(row, col);
        this.setMat(mat);

    }

    public DenseMatrix(double[] data){
        DMatrixRMaj mat = new DMatrixRMaj(data);
        this.setMat(mat);
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

    /**
     * Get all elements from the specified rows and columns.
     * @param mat  mat to be handled
     * @param rows row indexes
     * @param cols col indexes
     * @return sub-matrix
     */
    public static DenseMatrix getMat(DenseMatrix mat, int[] rows, int[] cols) {
        DenseMatrix ret = new DenseMatrix();
        double[] array = cut(mat, rows, cols);
        ret.setMat(new DMatrixRMaj(rows.length,cols.length,true,array));
        return ret;
    }

    private static double[] cut(DenseMatrix mat, int[] rows, int[] cols) {
        double[] array = new double[rows.length * cols.length];
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < cols.length; j++) {
                array[i * cols.length + j] = mat.get(rows[i], cols[j]);
            }
        }
        return array;
    }

    public DenseMatrix copy() {
        DMatrixRMaj mat_ = this.getDDRM();
        DenseMatrix matrix = new DenseMatrix();
        matrix.setMat(mat_.copy());
        return matrix;
    }
}
