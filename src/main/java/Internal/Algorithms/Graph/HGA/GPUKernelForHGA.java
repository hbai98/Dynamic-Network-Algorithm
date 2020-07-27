package Internal.Algorithms.Graph.HGA;

import com.aparapi.Kernel;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Vector;


public class GPUKernelForHGA extends Kernel {

    private final float[] inForNei;
    private final float[] out;
    private final float[] inForNonNei;
    private final double bioFactor;
    private final int[] neiSize;
    private final int[] nonNeiSize;
    private final int[] start_nei;
    private final int[] start_nonNei;

    public GPUKernelForHGA(Vector<Float> inForNei, Vector<Float> inForNonNei,
                           int[]start_nei,int[]start_nonNei,
                           int[] neiSize, int[] nonNeiSize,
                           float[] out,
                           float bioFactor){

        Object[] a1 = inForNei.toArray();
        Object[] a2 = inForNonNei.toArray();
        this.inForNei =ArrayUtils.toPrimitive( Arrays.copyOf(a1,a1.length,Float[].class));
        this.inForNonNei = ArrayUtils.toPrimitive(Arrays.copyOf(a2,a2.length,Float[].class));
        this.start_nei = start_nei;
        this.start_nonNei = start_nonNei;
        this.neiSize = neiSize;
        this.nonNeiSize = nonNeiSize;
        this.out = out;
        this.bioFactor = bioFactor;
    }


    @Override
    public void run() {
        int i = getGlobalId();
        double eNeighbors = 0;
        double eNonNeighbors = 0;
        // sum
        for (int i1 = 0; i1 < neiSize[i]; i1++) {
            eNeighbors += inForNei[start_nei[i]+i1];
        }
        for (int i1 = 0; i1 < nonNeiSize[i]; i1++) {
            eNonNeighbors += inForNonNei[start_nonNei[i]+i1];
        }
        // normalize
        eNeighbors /= neiSize[i];
        eNonNeighbors /= nonNeiSize[i];
        double eTP = (eNeighbors + eNonNeighbors) / 2;
        out[i] = (float) (out[i]*bioFactor+eTP * (1 - bioFactor));
    }

}
