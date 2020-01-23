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
 * Hungarian algorithm is the one used to address the problems in allocation within
 * minimum/maximum resources.
 * </p>
 * <br>
 * <p>
 * The result will be a vector used to identify the right allocation strategy.
 * </p>
 * <br>
 * <p>
 * Please refer to https://en.wikipedia.org/wiki/Hungarian_algorithm for the clear description of
 * the algorithm.
 * </p>
 * <p>
 * thanks for https://github.com/amirbawab/Hungarian-Algorithm/blob/master/HungarianDouble.java
 * </p>
 */
//TODO implement the maximum mapping.
public class Hungarian {
    protected DoubleMatrix mat;
    private DoubleMatrix matCp;
    protected int matCol;
    protected int matRow;
    // record lines to connect zeros
    private int numLine;
    // horizontal, vertical, cross lines(2 lines), no lines
    enum Direction {h, v, c}
    protected Direction[][] status;
    // record the mapping result
    private int[] result;

    /**
     * Start the algorithm.
     *
     * @param sourcePath file to read
     * @throws IOException read false.
     */
    protected Hungarian(String sourcePath) throws IOException {
        MyReader reader = new MyReader(sourcePath);
        mat = new DoubleMatrix(reader.out());
        // Get min to initialize the result size
        int tpMin = Math.min(mat.columns,mat.rows);
        //Index of the column selected by every row (The final result)
        result = new int[tpMin];
        numLine = 0;
        // Fill zeros if not the same width and height, it will change the shape of the matrix.
        fillZeros();
        status = new Direction[matCol][matRow];
        //Hungarian algorithm
        subtractRowMinimal(); // step 1
        subtractColMinimal();//step 2
        greedyCoverZeros(); // step 3
        while (numLine < matRow) {
            shiftZeros(); // step 4
            greedyCoverZeros(); // step 3
        }
        optimize(); // step 5
    }



    private void fillZeros() {
        matRow = mat.rows;
        matCol = mat.columns;
        if (matRow == matCol) {
            return;
        }
        if (matRow < matCol) {
            addRow(matCol - matRow);
        } else {
            addCol(matRow - matCol);
        }
    }

    private void addCol(int dev) {
        assert (dev > 0);
        double[][] tpArray = new double[matRow][matCol + dev];
        copyMatToArray(tpArray);
    }

    private void addRow(int dev) {
        assert (dev > 0);
        double[][] tpArray = new double[matRow + dev][matCol];
        copyMatToArray(tpArray);
    }

    private void copyMatToArray(double[][] tpArray) {
        for (int i = 0; i < matRow; i++) {
            for (int j = 0; j < matCol; j++) {
                tpArray[i][j] = mat.get(i, j);
            }
        }
        mat = new DoubleMatrix(tpArray);
        matRow = mat.getRows();
        matCol = mat.getColumns();
    }

    /**
     * STEP 1:
     * subtract from every element the minimum value of its row
     */
    protected void subtractRowMinimal() {
        int rows = mat.getRows();
        for (int r = 0; r < rows; r++) {
            DoubleMatrix curRow = mat.getRow(r);
            double minRowVal = curRow.min();
            mat.putRow(r, curRow.sub(minRowVal));
        }
    }

    /**
     * Step 2
     * Subtract from every element the minimum value from its column
     */
    protected void subtractColMinimal() {
        int cols = mat.getColumns();
        for (int c = 0; c < cols; c++) {
            DoubleMatrix curCol = mat.getColumn(c);
            double minColVal = curCol.min();
            mat.putColumn(c, curCol.sub(minColVal));
        }
    }

    /**
     * Step 3
     * Loop through all elements, and run cover when the element visited is equal to zero
     * <p>
     * This is a greedy algorithm with time complexity O(n^3)->O{(m*n)*(2*n+m+2)}, which can split into 3 parts:
     *     <ol>
     *         <li>coverZeros() iterates over the whole matrix to find the element which is equal to 0 and
     *         greedily cover zeros as many as possible(cover(i,j,detect(i,j))).</li>
     *         <li>cover(i,j,detect(i,j)) find the right direction (horizontal or vertical) to cover more zeros
     *         detect(i,j) will determine the direction.</li>
     *         <li>detect(i,j) gives the direction.</li>
     *     </ol>
     * </p>
     */
    protected void greedyCoverZeros() {
        assert (matRow == matCol);

        for (int i = 0; i < mat.getRows(); i++) {
            for (int j = 0; j < mat.getColumns(); j++) {
                if (mat.get(i, j) == 0) {
                    cover(i, j, detect(i, j));
                }
            }
        }
    }


    /**
     * cover(i,j,detect(i,j)) find the right direction (horizontal or vertical) to cover more zeros
     * detect(i,j) will determine the direction.
     * <p>
     * Previously, the fillZeros() has already assure : mat.rows = mat.cols.
     * </p>
     *
     * @param i      index of row
     * @param j      index of column
     * @param detect detect(i,j) result
     */
    protected void cover(int i, int j, Direction detect) {
        // if cell is colored twice before (intersection cell), don't color it again
        if (status[i][j] == Direction.c) {
            return;
        }
        // if cell colored vertically and needs to be recolored vertically, don't color it again (Allowing this step, will color the same line (result won't change)
        if (status[i][j] == Direction.h && detect == Direction.h) {
            return;
        }
        // if cell colored horizontally and needs to be recolored horizontally, don't color it again (Allowing this step, will color the same line (result won't change)
        if (status[i][j] == Direction.v && detect == Direction.v) {
            return;
        }
        // cover
        for (int k = 0; k < matRow; k++) {
            if (detect == Direction.v) {
                // if cell was colored before as horizontal (h), and now needs to be colored vertical (v), so this cell is an intersection (c).
                // Else if this value was not colored before, color it vertically
                status[k][j] = status[k][j] == Direction.h ? Direction.c : Direction.v;
            } else {
                // if cell was colored before as vertical (v), and now needs to be colored horizontal (h), so this cell is an intersection (c).
                // Else if this value was not colored before, color it horizontally.
                status[i][k] = status[i][k] == Direction.v ? Direction.c : Direction.h;
            }
        }
        numLine++;

    }

    /**
     * detect(i,j) gives the chosen direction.
     * <p>
     * Previously, the fillZeros() has already assure : mat.rows = mat.cols.
     * </p>
     *
     * @return Direction
     */
    protected Direction detect(int i, int j) {

        int result = 0;
        for (int t = 0; t < matCol; t++) {
            if (mat.get(i, t) == 0.) {
                result++;
            }
            if (mat.get(t, j) == 0.) {
                result--;
            }
        }
        return result > 0 ? Direction.h : Direction.v;
    }

    /**
     * Step 4
     * This step is not always executed. (Check the algorithm in the constructor)
     * subtract minimum value k of uncovered cells (cells not colored by any line), and add k to all elements that are covered twice.
     */
    protected void shiftZeros() {
        double min = Double.MAX_VALUE;
        // Find the min in the uncovered numbers
        for (int r = 0; r < matRow; r++) {
            for (int c = 0; c < matCol; c++) {
                if (status[r][c] == null) {
                    double tpVal = mat.get(r, c);
                    if (tpVal < min) {
                        min = tpVal;
                    }
                }
            }
        }

        // Subtract min form all uncovered elements, and add it to all elements covered twice
        for (int r = 0; r < matRow; r++) {
            for (int c = 0; c < matCol; c++) {
                if (status[r][c] == null) {
                    double tmpVal = mat.get(r, c);
                    mat.put(r, c, tmpVal - min);
                }
                if (status[r][c] == Direction.c) {
                    double tmpVal = mat.get(r, c);
                    mat.put(r, c, tmpVal + min);
                }
            }
        }
    }
    /**
     * Step 5 : get the assignment result, one row - > one column.
     * brute force solution
     */
    //TODO find a better solution!
    private void optimize(){
        boolean[] colReserved = new boolean[matCol];
        recurse(0,colReserved);
    }
    private boolean recurse(int row, boolean[] colReserved) {

        if(row == result.length){
            return true;
        }
        for (int c = 0; c < matCol; c++) {
            double tpVal = mat.get(row,c);
            if(tpVal == 0 && !colReserved[c]){
                result[row] = c;
                colReserved[c] = true;
                if(recurse(row+1,colReserved)){
                    return true;
                }
                colReserved[c] = false;
            }
        }
        return false;

    }

    public int[] getResult() {
        return result;
    }

    private DoubleMatrix getCopyOfMatrix(){
        return mat;
    }
}
