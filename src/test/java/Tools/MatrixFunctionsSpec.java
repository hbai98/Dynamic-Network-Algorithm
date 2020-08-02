package Tools;

<<<<<<< Updated upstream:src/test/java/Tools/MatrixFunctionsSpec.java
import IO.DoubleMatrixReader;
=======
import Internal.Algorithms.IO.SimMatReader;
>>>>>>> Stashed changes:src/test/java/Internal/Algorithms/tools/MatrixFunctionsSpec.java
import org.jblas.DoubleMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static Tools.MatrixFunctions.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MatrixFunctionsSpec {
    SimMatReader reader;
    DoubleMatrix mat;
    @BeforeEach
    void init() throws IOException {
        reader = new SimMatReader("src/test/java/resources/AlgTest/MatrixFunctions/Bareiss_Mat.txt");
//        mat = new DoubleMatrix(reader.out()) ;
    }
    @Test
    void detSpec_1() {
        assertEquals(-137,det(mat));
    }
}