package DS.Matrix.Alg;

import org.ejml.MatrixDimensionException;
import org.ejml.data.DMatrix;
import org.ejml.data.DMatrix1Row;

public class CommonOps_DDRM_extend{
    /**
     * Invert the matrix when it's diagonal, which cost time O(n)
     * @param mat matrix to be handled
     */
    public static void inverseDig(DMatrix mat){
        if (mat.getNumCols() != mat.getNumRows()) {
            throw new MatrixDimensionException("Must be a square matrix.");
        }
        for (int i = 0; i < mat.getNumCols(); i++) {
            mat.set(i,i,1/mat.get(i,i));
        }
    }
}
