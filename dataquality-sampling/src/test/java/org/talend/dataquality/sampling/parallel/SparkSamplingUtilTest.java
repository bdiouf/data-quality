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

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.duplicating.AllDataqualitySamplingTests;

public class SparkSamplingUtilTest implements Serializable {

    private static final int SAMPLE_SIZE = 10;

    private static final int ORIGINAL_COUNT = 100;

    private ReservoirSamplerWithBinaryHeap<TestRowStruct> sampler;

    private TestRowStruct[] testers;

    private static final Integer[] EXPECTED_SAMPLES_LIST = { 31, 79, 93, 32, 45, 90, 15, 59, 91, 89 };

    private static JavaSparkContext sc;

    @BeforeClass
    public static void beforeClass() {
        sc = new JavaSparkContext(new SparkConf().setAppName("Simple Application").setMaster("local[1]"));
    }

    @Before
    public void init() {
        testers = new TestRowStruct[ORIGINAL_COUNT];
        for (int j = 0; j < ORIGINAL_COUNT; j++) {
            TestRowStruct struct = new TestRowStruct();
            struct.id = j + 1;
            testers[j] = struct;
        }
    }

    @Test
    public void testSamplePairList() {
        JavaRDD<TestRowStruct> rdd = sc.parallelize(Arrays.asList(testers));
        SparkSamplingUtil<TestRowStruct> sampler = new SparkSamplingUtil<>(AllDataqualitySamplingTests.RANDOM_SEED);
        List<ImmutablePair<Double, TestRowStruct>> topPairs = sampler.getSamplePairList(rdd, SAMPLE_SIZE);

        for (int i = 0; i < topPairs.size(); i++) {
            assertTrue("The ID " + topPairs.get(i).getRight().getId() + " is expected",
                    Arrays.asList(EXPECTED_SAMPLES_LIST).contains(topPairs.get(i).getRight().getId()));
        }
    }

    @Test
    public void testGetSampleList() {
        JavaRDD<TestRowStruct> rdd = sc.parallelize(Arrays.asList(testers));
        SparkSamplingUtil<TestRowStruct> sampler = new SparkSamplingUtil<>(AllDataqualitySamplingTests.RANDOM_SEED);
        List<TestRowStruct> sampleList = sampler.getSampleList(rdd, SAMPLE_SIZE);

        for (int i = 0; i < sampleList.size(); i++) {
            assertTrue("The ID " + sampleList.get(i).getId() + " is expected",
                    Arrays.asList(EXPECTED_SAMPLES_LIST).contains(sampleList.get(i).getId()));
        }
    }

    class TestRowStruct implements Serializable {

        public Integer id;

        public Integer getId() {
            return this.id;
        }

        public String city;

        public String getCity() {
            return this.city;
        }

        @Override
        public String toString() {
            return id + " -> " + city; //$NON-NLS-1$
        }
    }
}
