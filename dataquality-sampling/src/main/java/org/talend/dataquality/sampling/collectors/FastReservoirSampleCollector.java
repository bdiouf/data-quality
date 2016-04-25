package org.talend.dataquality.sampling.collectors;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class FastReservoirSampleCollector<T> implements Collector<T, List<T>, List<T>> {

    final Random rand;

    final int nbSamples;

    int count = 0;

    private final int threshold;

    private int g = 0;

    public FastReservoirSampleCollector(int nbSamples, long seed) {
        this.nbSamples = nbSamples;
        threshold = 4 * nbSamples;
        rand = new Random(seed);
    }

    public FastReservoirSampleCollector(int nbSamples) {
        this.nbSamples = nbSamples;
        threshold = 4 * nbSamples;
        rand = new SecureRandom();
    }

    private void addIt(final List<T> candidates, T v) {

        if (count < nbSamples) {
            // for the first n elements.
            candidates.add(v);
            count++;
            return;
        }

        if (count < threshold) {

            // do reservoir sampling.
            // rand.nextDouble gets a pseudo random value between 0.0 and 1.0
            long replace = (long) Math.floor(count * rand.nextDouble());
            if (replace < nbSamples) {
                // probability says replace.
                candidates.set((int) replace, v);
            }
            // else keep the current sample reservoir
        } else {
            if (g == 0) {
                // draw gap size (g) from geometric distribution with probability p = R/j
                double p = (double) nbSamples / count;
                double u = rand.nextDouble(); // random float > 0 and <= 1
                g = (int) Math.floor(Math.log10(u) / Math.log10(1 - p));

            } else {
                g--;
                if (g == 0) {
                    int replace = (int) Math.floor(nbSamples * rand.nextDouble());
                    candidates.set(replace, v);
                }
            }

        }

        count++;

    }

    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return this::addIt;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (left, right) -> {
            left.addAll(right);
            return left;
        };
    }

    @Override
    public Set<java.util.stream.Collector.Characteristics> characteristics() {
        return EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH);
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return (i) -> i;
    }
}
