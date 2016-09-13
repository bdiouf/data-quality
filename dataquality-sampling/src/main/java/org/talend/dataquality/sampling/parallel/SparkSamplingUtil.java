// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.sampling.parallel;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;

/**
 * Sampling API for Spark components.
 */
public class SparkSamplingUtil<T> implements Serializable {

    private Long seed = null;

    public SparkSamplingUtil() {
        this(null);
    }

    /**
     * constructor with random seed as parameter
     * 
     * @param seed
     */
    public SparkSamplingUtil(Long seed) {
        this.seed = seed;
    }

    /**
     * do sampling on RDD
     * 
     * @param rdd
     * @param nbSamples
     * @return list of sample pairs, with generated score as left value and original data as right value.
     */
    public List<ImmutablePair<Double, T>> getSamplePairList(JavaRDD<T> rdd, int nbSamples) {
        JavaRDD<ImmutablePair<Double, T>> mappedRdd = rdd.mapPartitions(new SamplingMapFunction(nbSamples));
        List<ImmutablePair<Double, T>> topPairs = mappedRdd.top(nbSamples, new PairComparator());
        return topPairs;
    }

    /**
     * do sampling on DF
     *
     * @param df
     * @param nbSamples
     * @return list of sample pairs, with generated score as left value and original data as right value.
     */
    public List<ImmutablePair<Double, Row>> getSamplePairList(DataFrame df, int nbSamples) {
        JavaRDD<ImmutablePair<Double, Row>> mappedRdd = df.javaRDD().mapPartitions(new SamplingMapFunction<Row>(nbSamples));
        List<ImmutablePair<Double, Row>> topPairs = mappedRdd.top(nbSamples, new PairComparator());
        return topPairs;
    }

    /**
     * do sampling on DateFrame
     * 
     * @param rdd
     * @param nbSamples
     * @return list of sample values
     */
    public List<T> getSampleList(JavaRDD<T> rdd, int nbSamples) {
        List<ImmutablePair<Double, T>> topPairs = getSamplePairList(rdd, nbSamples);
        List<T> result = new ArrayList<T>();
        for (ImmutablePair<Double, T> pair : topPairs) {
            result.add(pair.getRight());
        }
        return result;
    }

    private class SamplingMapFunction<T> implements FlatMapFunction<Iterator<T>, ImmutablePair<Double, T>> {

        private final int nbSamples;

        public SamplingMapFunction(int nbSamples) {
            this.nbSamples = nbSamples;
        }

        @Override
        public Iterable<ImmutablePair<Double, T>> call(Iterator<T> tIterator) throws Exception {
            if (seed == null) {
                seed = new Random().nextLong();
            }
            ReservoirSamplerWithBinaryHeap<T> sampler = new ReservoirSamplerWithBinaryHeap<T>(nbSamples, seed);
            sampler.clear();
            while (tIterator.hasNext()) {
                sampler.onNext(tIterator.next());
            }
            sampler.onCompleted(true);

            Iterable<ImmutablePair<Double, T>> samplePairs = sampler.samplePairs();

            return samplePairs;
        }
    }

    private class PairComparator<T> implements Serializable, Comparator<ImmutablePair<Double, T>> {

        @Override
        public int compare(ImmutablePair<Double, T> o1, ImmutablePair<Double, T> o2) {
            if (o1.left > o2.left) {
                return 1;
            } else if (o1.left < o2.left) {
                return -1;
            }
            return 0;
        }
    }

}
