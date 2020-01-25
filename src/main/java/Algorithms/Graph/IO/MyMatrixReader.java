package Algorithms.Graph.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

/**
 * Reader class for matrix reading
 * Mainly for test.
 */
//TODO make it adapted to byte[] reading and work with jblas.
public class MyMatrixReader {
    Scanner input;
    int rows = 0;
    int cols = 0;
    int MAX = 0;
    double[][] result;
    public MyMatrixReader(String sourcePath) throws IOException {
        input = new Scanner(new File(sourcePath));
        if(input.hasNextLine()){
            if(input.hasNextInt()){
                rows = input.nextInt();
            }
            if(input.hasNextInt()){
                cols = input.nextInt();
            }
            if(input.hasNextInt()){
                MAX = input.nextInt();
            }
            if(cols*rows<MAX){
                throw new IllegalArgumentException("MAX is greater than the original size. ");
            }
        }
        else throw new IllegalArgumentException("head numbers are not right.");
        result = new double[rows][cols];
        // read
        if(input.hasNextLine()){
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if(i*cols+j+1 > MAX){
                        return;
                    }
                    if(input.hasNextDouble()){
                        result[i][j] = input.nextDouble();
                    }
                }
            }
        }
        else{
            throw new IOException("no data found!");
        }
    }


    public double[][] out() {
        // reshape matrix before release
        // upper bound division
        int realRow = (int)Math.ceil((double)MAX/cols);
        double[][] res = new double[realRow][cols];
        for (int i = 0; i < realRow; i++) {
            for (int j = 0; j < cols ; j++) {
                if(i*cols+j+1<=MAX){
                    res[i][j] = result[i][j];
                }
            }
        }
        return res;
    }
}
