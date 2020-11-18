

package Algorithms.Graph.Alignment.HGA;

import com.aparapi.Kernel;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Vector;


public class GPUKernelForHGA extends Kernel {

    private final double[] pre;
    private final double[] ori;
    private final double[] out;
    private final double bio;
    private final int[] nei_x;
    private final int[] nei_y;
    private final int[] sx;
    private final int[] sy;
    private final int n1;
    private final int n2;
    private final double preSum;

    public GPUKernelForHGA(double[] pre, double[] ori, double[] out,
                           Vector<Integer> nei_x, int[] start_x,
                           Vector<Integer> nei_y, int[] start_y,
                           double preSum,
                           double bioFactor) {
        this.pre = pre;
        this.ori = ori;
        this.out = out;
        this.bio = bioFactor;
        Object[] neix = nei_x.toArray();
        Object[] neiy = nei_y.toArray();
        this.nei_x = ArrayUtils.toPrimitive(Arrays.copyOf(neix, neix.length, Integer[].class));
        this.nei_y = ArrayUtils.toPrimitive(Arrays.copyOf(neiy, neiy.length, Integer[].class));
        this.sx = start_x;
        this.sy = start_y;
        this.n1 = start_x.length - 1;
        this.n2 = start_y.length - 1;
        this.preSum = preSum;
        setExplicit(true);
    }


    @Override
    public void run() {
        int id = getGlobalId();
        int c = id / n1;
        int r = id - c * n1;
        double eNeighbors = 0;
        double eNonNeighbors = 0;
        // sum
        int nei1Size = sx[r + 1] - sx[r];
        int nei2Size = sy[c + 1] - sy[c];
        // neighbors
        if (nei1Size != 0 && nei2Size != 0) {
            for (int i = sx[r]; i < sx[r + 1]; i++) {
                for (int j = sy[c]; j < sy[c + 1]; j++) {
                    int index = nei_x[i] + nei_y[j] * n1;
                    eNeighbors = eNeighbors + pre[index];
                }
            }
            eNeighbors = eNeighbors / (nei1Size * nei2Size);
        } else if (nei1Size == 0 && nei2Size == 0) {
            eNeighbors = preSum / (n1 * n2);
        } else {
            eNeighbors = 0;
        }
        int non1Size = n1 - 1 - nei1Size;
        int non2Size = n2 - 1 - nei2Size;
        // non-neighbors
        if (non1Size != 0 && non2Size != 0) {
            for (int i = 0; i < n1; i++) {
                if (isIn(i, sx, nei_x, r)) {
                    for (int j = 0; j < n2; j++) {
                        if (isIn(j, sy, nei_y, c)) {
                            int index = i + j * n1;
                            eNonNeighbors = eNonNeighbors + pre[index];
                        }
                    }
                }
            }
            eNonNeighbors = eNonNeighbors / (non1Size * non2Size);
        } else if (non1Size == 0 && non2Size == 0) {
            eNeighbors = preSum / (n1 * n2);
        } else {
            eNeighbors = 0;
        }

        double eTP = (eNeighbors + eNonNeighbors) / 2;
        out[r + n1 * c] = (double) (ori[r + n1 * c] * bio + eTP * (1 - bio));
    }


    private static boolean isIn(int i, int[] s, int[] nei, int l) {
        if (i == l) {
            return false;
        }
        for (int j = s[l]; j < s[l + 1]; j++) {
            if (i == nei[j]) {
                return false;
            }
        }
        return true;
    }

}
