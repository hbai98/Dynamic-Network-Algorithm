package Algorithms.Graph;

import Algorithms.Graph.IO.MyMatrixReader;
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
    DoubleMatrix mat;
    @BeforeEach
    void init(){
        try {
            MyMatrixReader reader = new MyMatrixReader("src/test/java/resources/AlgTest/Hungarian/coverZerosTest.txt");
            mat = new DoubleMatrix(reader.out());
            alg = new Hungarian(mat, Hungarian.ProblemType.minLoc);
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
    @DisplayName("is a final Test fro minLoc.")
    @Test
    void FinalTest(){
        int[] result = alg.getIndexResult();
        assertThat(result).containsSequence(2,0,1);
    }

    @DisplayName("is a final Test fro maxLoc.")
    @Test
    void FinalTest_2() throws IOException {
        alg = new Hungarian(mat, Hungarian.ProblemType.maxLoc);
        int[] result = alg.getIndexResult();
        assertThat(result).containsSequence(1,2,0);
    }


}