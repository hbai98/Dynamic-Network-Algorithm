package DS.Matrix;

import org.ejml.data.DMatrixSparseCSC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SparseMatrixTest {

    private DMatrixSparseCSC csc;

    @BeforeEach
    void setUp() {
        csc = new DMatrixSparseCSC(3,2);
    }

    @Test
    void test(){
        csc.set(2,1, 0.2);
        csc.set(2,0, 8.);
        csc.set(1, 1, 2.2);
        csc.set(2,0,1.2);
    }
}