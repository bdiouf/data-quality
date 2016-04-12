package org.talend.dataquality.sampling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FastReservoirSampler<T> {

    private final int nbSamples; // number of elements to sample.

    private List<T> candidates; // the reservoir.

    private int count = 0;

    private boolean done = false;

    private Random rand;

    private final int threshold;

    private int gap = 0;

    public FastReservoirSampler(int nbSamples, long seed) {
        this.nbSamples = nbSamples;
        this.threshold = 4 * nbSamples;
        this.candidates = new ArrayList<T>(nbSamples);
        this.rand = new Random(seed);
    }

    public FastReservoirSampler(int samples) {
        this(samples, System.currentTimeMillis());
    }

    public void onCompleted(boolean b) {
        done = b;
    }

    public void onNext(T v) {
        if (done) {
            return;
        }

        if (count < nbSamples) {
            // for the first n elements.
            candidates.add(v);
            count++;
            return;
        }

        if (count < threshold) {
            // do classic reservoir sampling.
            long replace = (long) Math.floor(count * rand.nextDouble());
            if (replace < nbSamples) {
                candidates.set((int) replace, v);
            }
        } else {
            if (gap == 0) {
                // draw gap size (g) from geometric distribution with probability p = R/j
                double p = (double) nbSamples / count;
                double u = rand.nextDouble();
                gap = (int) Math.floor(Math.log10(u) / Math.log10(1 - p));
            } else {
                gap--; // count down
                if (gap == 0) {
                    int replace = (int) Math.floor(nbSamples * rand.nextDouble());
                    candidates.set(replace, v);
                }
            }
        }
        count++;
    }

    /**
     * Returns an unmodifiable reference to the sample list.
     */
    public List<T> sample() {
        return Collections.unmodifiableList(candidates);
    }

    public void clear() {
        candidates.clear();
    }
}
