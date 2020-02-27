package Tools;

import org.jblas.DoubleMatrix;

/**
 * extended functions for DoubleMatrix in jblas-1.2.4 jar
 * please visit http://jblas.org/javadoc/ for further information
 */
public class MatrixFunctions {
    /**
     * Compute the determinant of the square matrix by Bareiss algorithm
     * <p>
     * Reference:Efficiently Calculating the Determinant of a Matrix
     * Felix Limanta 13515065 Program Studi Teknik Informatika
     * Sekolah Teknik Elektro dan Informatika
     * Institut Teknologi Bandung, Jl. Ganesha 10 Bandung 40132, Indonesia
     * </p>
     * <br>
     * <p>Keep the mat unchanged</p>
     *
     * @param mat DoubleMatrix in jblas
     */
    public static double det(DoubleMatrix mat) {
        // clone mat
        DoubleMatrix tpMat = mat.dup();
        if(SwapRowsWith0Pivot(tpMat)){
            return 0;
        }
        else{
            // nonSingular matrix
            int row = mat.rows;
            // a00 = 1
            double pivot = 1;
            // traverse pivots
            for (int k = 0; k < row - 1; k++) {
                // traverse rows
                for(int i = k+1; i < row; i++){
                    // traverse columns
                    for(int j = k+1; j < row; j++){
                        // apply formula
                        tpMat.put(i,j,(tpMat.get(k,k)*tpMat.get(i,j) - tpMat.get(i,k)*tpMat.get(k,j))
                        /pivot);
                    }
                }
                // set next pivot
                pivot = tpMat.get(k,k);
            }
            return tpMat.get(row-1,row-1);
        }
    }

    /**
     * rows whose pivots are 0 must first
     * be swapped in order to avoid a divide by 0 problem
     * @return true for singular
     */
    private static boolean SwapRowsWith0Pivot(DoubleMatrix mat) {
        int row = mat.rows;
        if (row != mat.columns) {
            throw new IllegalArgumentException("The matrix should be square.");
        }
        boolean singular = false;
        // search for pivots
        int i = 0;
        while (i < row && !singular) {
            if (mat.get(i, i) == 0) {
                int j = 0;
                // search for swappable rows
                while (j < row - 1 && mat.get(j, i) == 0) {
                    j ++;
                    if (mat.get(j, i) != 0) {
                        // swap rows
                        DoubleMatrix tmpRow = mat.getRow(j);
                        mat.putRow(j, mat.getRow(i));
                        mat.putRow(i, tmpRow);
                        break;
                    }
                }
                // check search result
                if (j == row - 1) {
                    // not found
                    singular = true;
                }
            }
            i++;
        }
        return singular;
    }
}
