package Algorithms.Graph;
// Author: Haotian Bai
// Shanghai University, department of computer science

import Algorithms.Graph.IO.MyReader;
import org.jblas.DoubleMatrix;

import java.io.IOException;

/**
 * For the high computation performance, the matrix (DoubleMatrix) is based on
 * jblas-1.2.4 jar
 * please visit http://jblas.org/javadoc/ for further information
 *
 * <p>
 *     Hungarian algorithm is the one used to address the problems in allocation within
 *     minimum/maximum resources.
 * </p>
 * <br>
 * <p>
 *     The result will be a vector used to identify the right allocation strategy.
 * </p>
 *<br>
 * <p>
 *     Please refer to https://en.wikipedia.org/wiki/Hungarian_algorithm for the clear description of
 *     the algorithm.
 * </p>
 * <p>
 *     thanks for https://github.com/amirbawab/Hungarian-Algorithm/blob/master/HungarianDouble.java
 * </p>
 */
//TODO implement the maximum mapping.
public class Hungarian {
    protected DoubleMatrix mat;

    /**
     * Start the algorithm.
     * @param sourcePath file to read
     * @throws IOException read false.
     */
    protected Hungarian(String sourcePath) throws IOException {
        MyReader reader = new MyReader(sourcePath);
        mat = new DoubleMatrix(reader.out());
        //Hungarian algorithm
//        subtractRowMinimal(); // step 1
//        subtractColMinimal();//step 2
    }

    /**
     * STEP 1:
     *  subtract from every element the minimum value of its row
     */
    protected void subtractRowMinimal() {
        int rows = mat.getRows();
        for (int r = 0; r < rows; r++) {
            DoubleMatrix curRow = mat.getRow(r);
            double minRowVal = curRow.min();
            mat.putRow(r,curRow.sub(minRowVal));
        }
    }
    /**
     * Step 2
     * Subtract from every element the minimum value from its column
     * */
    protected void subtractColMinimal(){
        int cols = mat.getColumns();
        for (int c = 0; c < cols; c++) {
            DoubleMatrix curCol = mat.getColumn(c);
            double minColVal = curCol.min();
            mat.putColumn(c,curCol.sub(minColVal));
        }
    }

    /**
     * Step 3
     * Loop through all elements, and run cover when the element visited is equal to zero
     * <p>
     *     This is a greedy algorithm with time complexity O(n^3)->O{(m*n)*(2*n+m+2)}, which can split into 3 parts:
     *     <ol>
     *         <li>coverZeros() iterates over the whole </li>
     *     </ol>
     * </p>
     */
    protected void GreedyCoverZeros(){

    }
}
