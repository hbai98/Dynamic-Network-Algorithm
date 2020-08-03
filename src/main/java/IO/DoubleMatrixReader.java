package IO;

import org.jblas.DoubleMatrix;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Reader class for matrix reading
 * Mainly for test.
 */
public class DoubleMatrixReader {
    Scanner input;
    int rows = 0;
    int columns = 0;
    double[][] result;
    public DoubleMatrixReader(String sourcePath) throws IOException {
        input = new Scanner(new File(sourcePath));
        // pre-read in the number of rows/columns
        while(input.hasNextLine())
        {
            ++rows;
            // only read when it is the first row
            if(columns==0){
                Scanner colReader = new Scanner(input.nextLine());
                while(colReader.hasNextDouble())
                {
                    ++columns;
                }
            }
        }
        input.close();
        // read in the data
        input = new Scanner(new File(sourcePath));
        for(int i = 0; i < rows; ++i)
        {
            for(int j = 0; j < columns; ++j)
            {
                if(input.hasNextInt())
                {
                    result[i][j] = input.nextDouble();
                }
            }
        }
    }

    public DoubleMatrix getMat(){
        return new DoubleMatrix(result);
    }

}
