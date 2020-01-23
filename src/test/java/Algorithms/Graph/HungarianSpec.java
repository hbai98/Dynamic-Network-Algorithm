package Algorithms.Graph;

import org.jblas.DoubleMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("The Hungarian algorithm is able to")
class HungarianSpec {
    Hungarian alg;
    @BeforeEach
    void init(){
        try {
            alg = new Hungarian("src/test/java/resources/AlgTest/SmallMatrix.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @DisplayName("read")
    @Test
    void read(){
        alg.mat.print();
    }
    @DisplayName("subtract every row minimal from the matrix.")
    @Test
    void subtractRowMinimal(){
        double[][] testData ={{0.,1.,2.,3.,4.},{0.,1.,2.,3.,4.}};
        DoubleMatrix mat = new DoubleMatrix(testData);

        alg.subtractRowMinimal();

        assertEquals(mat,alg.mat);
    }
    @DisplayName("cover 0 greedily.")
    @Test
    void coverZeroTest(){
        try {
            alg = new Hungarian("src/test/java/resources/AlgTest/Hungarian/coverZerosTest.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        alg.subtractRowMinimal();
        alg.subtractColMinimal();
        alg.greedyCoverZeros();
        int tmp = 0;
        for (int i = 0; i < alg.mat.rows; i++) {
            for (int j = 0; j < alg.mat.columns; j++) {
                if(alg.status[i][j] == Hungarian.Direction.c){
                    tmp ++;
                }
            }
        }
        assertEquals(2,tmp);
    }
    @DisplayName("is a final Test")
    @Test
    void FinalTest(){
        try {
            alg = new Hungarian("src/test/java/resources/AlgTest/Hungarian/coverZerosTest.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int[] result = alg.getResult();
        assertThat(result).containsSequence(2,0,1);
    }
}