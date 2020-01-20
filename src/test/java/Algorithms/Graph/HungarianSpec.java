package Algorithms.Graph;

import org.jblas.DoubleMatrix;
import org.jblas.ranges.AllRange;
import org.jblas.ranges.IntervalRange;
import org.jblas.ranges.Range;
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


}