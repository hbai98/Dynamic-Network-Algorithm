package Internal.Algorithms.Tools;

import com.aparapi.Kernel;


public class GPUKernelForHGA extends Kernel {

    private final float[] inForNei;
    private final float[] out;
    private final float[] inForNonNei;
    private final double bioFactor;
    private final int[] neiSize;
    private final int[] nonNeiSize;
    private final int[] start_nei;
    private final int[] start_nonNei;

    public GPUKernelForHGA(float[] inForNei, float[] inForNonNei,
                           int[]start_nei,int[]start_nonNei,
                           int[] neiSize, int[] nonNeiSize,
                           float[] out,
                           float bioFactor){
        this.inForNei = inForNei;
        this.inForNonNei = inForNonNei;
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
