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
package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTest.class);

    private static final int REPLICATE = 333;

    @Test
    public void testIsDate() throws IOException {

        final InputStream stream = SystemDateTimePatternManager.class.getResourceAsStream("DateSampleTable.txt");
        final List<String> lines = IOUtils.readLines(stream);

        SystemDateTimePatternManager.isDate("12/02/99");// init DateTimeFormatters

        final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        final long cpuBefore = mxBean.getCurrentThreadCpuTime();

        for (int i = 1; i < lines.size(); i++) {
            for (int n = 0; n < REPLICATE; n++) {
                final String line = lines.get(i);
                if (!"".equals(line.trim())) {
                    final String[] sampleLine = line.trim().split("\t");
                    final String sample = sampleLine[0];
                    CustomDateTimePatternManager.isDate(sample, Collections.emptyList());
                }
            }
        }

        final long cpuAfter = mxBean.getCurrentThreadCpuTime();

        final long difference = cpuAfter - cpuBefore;
        assertTrue("The method isDate() is slower than expected. Actual CPU time spent: " + difference, difference < 1.3e9);
    }

    @Test
    public void testGetPatterns() throws IOException {

        final InputStream stream = SystemDateTimePatternManager.class.getResourceAsStream("DateSampleTable.txt");
        final List<String> lines = IOUtils.readLines(stream);

        SystemDateTimePatternManager.datePatternReplace("12/02/99");// init DateTimeFormatters

        final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        final long cpuBefore = mxBean.getCurrentThreadCpuTime();

        for (int i = 1; i < lines.size(); i++) {
            for (int n = 0; n < REPLICATE; n++) {
                final String line = lines.get(i);
                if (!"".equals(line.trim())) {
                    final String[] sampleLine = line.trim().split("\t");
                    final String sample = sampleLine[0];
                    SystemDateTimePatternManager.datePatternReplace(sample);
                }
            }
        }

        final long cpuAfter = mxBean.getCurrentThreadCpuTime();

        final long difference = cpuAfter - cpuBefore;
        assertTrue("The method getPatterns() is slower than expected. Actual CPU time spent: " + difference, difference < 1.4e9);
    }

}
