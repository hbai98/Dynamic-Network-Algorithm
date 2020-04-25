package Tools;

import IO.MyMatrixReader;
import org.jblas.DoubleMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static Tools.MatrixFunctions.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MatrixFunctionsSpec {
    MyMatrixReader reader;
    DoubleMatrix mat;
    @BeforeEach
    void init() throws IOException {
        reader = new MyMatrixReader("src/test/java/resources/AlgTest/MatrixFunctions/Bareiss_Mat.txt");
        mat = new DoubleMatrix(reader.out()) ;
    }
    @Test
    void detSpec_1() {
        assertEquals(-137,det(mat));;
    }
}