package Algorithms.Graph;

import Algorithms.Graph.Utils.SimMat;
import IO.GraphFileReader;
import Tools.Stopwatch;
import org.jblas.DoubleMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class HungarianSpec {
    Hungarian alg;
    DoubleMatrix mat;


    @BeforeEach
    void init() {
        GraphFileReader reader = new GraphFileReader(true, false, true);

//        try {
//            MyMatrixReader reader = new MyMatrixReader("src/test/java/resources/AlgTest/Hungarian/coverZerosTest.txt");
//            mat = new DoubleMatrix(reader.out());
//            alg = new Hungarian(mat, Hungarian.ProblemType.maxLoc);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }



//    @Test
//    void speedComparison() throws IOException {
//        GraphFileReader reader = new GraphFileReader();
//        SimList simList = reader.readToSimList("src/test/java/resources/AlgTest/HGA/simMat.txt", false);
//        mat = simList.getMatrix();
//        alg = new Hungarian(simList, minLoc);
//        int[] result = alg.getResult();
//        int r = 0;
//        double max = 0;
//        for (int i : result) {
//            System.out.printf("%d ", i);
//            if (i > 0) {
//                max += mat.get(r++, i);
//            }
//        }
//        System.out.printf("\n大小%f", max);
//
//    }



}