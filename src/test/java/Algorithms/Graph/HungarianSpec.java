package Algorithms.Graph;

import IO.GraphFileReader;
import IO.MyMatrixReader;
import Algorithms.Graph.Network.AdjList;
import org.jblas.DoubleMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static Algorithms.Graph.Hungarian.ProblemType.minLoc;
import static org.assertj.core.api.Assertions.assertThat;

class HungarianSpec {
    Hungarian alg;
    DoubleMatrix mat;

    @BeforeEach
    void init() {
        try {
            MyMatrixReader reader = new MyMatrixReader("src/test/java/resources/AlgTest/Hungarian/coverZerosTest.txt");
            mat = new DoubleMatrix(reader.out());
            alg = new Hungarian(mat, Hungarian.ProblemType.maxLoc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void maxLoc() {
        int[] result = alg.getResult();
        assertThat(result).containsSequence(1, 2, 0);
    }

    @Test
    void speedComparison() throws IOException {
        GraphFileReader reader = new GraphFileReader();
        AdjList simList = reader.readToAdjL("src/test/java/resources/AlgTest/HGA/simMat.txt", false);
        mat = simList.toMatrix();
        alg = new Hungarian(simList, minLoc);
        int[] result = alg.getResult();
        int r = 0;
        double max = 0;
        for (int i : result) {
            System.out.printf("%d ", i);
            if (i > 0) {
                max += mat.get(r++, i);
            }
        }
        System.out.printf("\n大小%f", max);

    }



}