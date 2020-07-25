package Internal.Algorithms.tools;

import com.aparapi.Kernel;
import com.aparapi.Range;
import org.junit.jupiter.api.Test;

class GPUKernelForHGATest {

    @Test
    void test1(){
        final float inA[] = {5,2,3};// get a float array of data from somewhere
        final float inB[] = {1,8,2};// get a float array of data from somewhere
        final float result[] = new float[inA.length];
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                result[i] = inA[i] + inB[i];
            }
        };
        Range range = Range.create(result.length);
        kernel.execute(range);
        for (float v : result) {
            System.out.println(v);
        }
    }
}