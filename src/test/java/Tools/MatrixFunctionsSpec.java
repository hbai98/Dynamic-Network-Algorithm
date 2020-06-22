package Tools;

import IO.DoubleMatrixReader;
import org.jblas.DoubleMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static Tools.MatrixFunctions.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MatrixFunctionsSpec {
    DoubleMatrixReader reader;
    DoubleMatrix mat;
    @BeforeEach
    void init() throws IOException {
        reader = new DoubleMatrixReader("src/test/java/resources/AlgTest/MatrixFunctions/Bareiss_Mat.txt");
//        mat = new DoubleMatrix(reader.out()) ;
    }
    @Test
    void detSpec_1() {
        assertEquals(-137,det(mat));
    }
}