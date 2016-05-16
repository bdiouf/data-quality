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
package org.talend.dataquality.common.inference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

/**
 * Provides a way to combine several {@link Analyzer} together and a
 * {@link org.talend.datascience.common.inference.Analyzers.Result result} that stores all underlying results.
 *
 * @see #with(Analyzer[])
 */
public class Analyzers implements Analyzer<Analyzers.Result> {

    private static final long serialVersionUID = 3718737129904789140L;

    private static final Logger LOGGER = Logger.getLogger(Analyzers.class);

    private final Analyzer<?>[] analyzers;

    private final ResizableList<Result> results = new ResizableList<>(Result.class);

    private Analyzers(Analyzer<?>... analyzers) {
        this.analyzers = analyzers;
    }

    /**
     * Creates a single analyzer with provided {@link Analyzer analyzers}.
     * 
     * @param analyzers The analyzers to be combined together.
     * @return A single analyzer that ensure all underlying analyzers get called.
     */
    public static Analyzer<Analyzers.Result> with(Analyzer<?>... analyzers) {
        return new Analyzers(analyzers);
    }

    public void init() {
        for (Analyzer<?> analyzer : analyzers) {
            analyzer.init();
        }
    }

    public boolean analyze(String... record) {
        boolean result = true;
        results.resize(record.length);
        for (Analyzer<?> analyzer : analyzers) {
            result &= analyzer.analyze(record);
        }
        return result;
    }

    public void end() {
        for (Analyzer<?> executor : analyzers) {
            executor.end();
        }
    }

    public List<Result> getResult() {
        for (Analyzer<?> analyzer : analyzers) {
            final List<?> analysis = analyzer.getResult();
            for (int j = 0; j < analysis.size(); j++) {
                results.get(j).add(analysis.get(j));
            }
        }
        return results;
    }

    /**
     * <<<<<<< HEAD A generic composite result which aggregates several analyzer's results. ======= Composite result
     * aggregates several analyzer's result together. >>>>>>> workitem/TDQ-10955_remove_analyzeArray_method
     *
     */
    public static class Result {

        private final Map<Class<?>, Object> results = new HashMap<>();

        public <T> T get(Class<T> clazz) {
            if (results.containsKey(clazz)) {
                return clazz.cast(results.get(clazz));
            }
            throw new IllegalArgumentException("No result of type '" + clazz.getName() + "'.");
        }

        public <T> boolean exist(Class<T> clazz) {
            return results.containsKey(clazz);
        }

        public void add(Object result) {
            results.put(result.getClass(), result);
        }
    }

    @Override
    public Analyzer<Result> merge(Analyzer<Result> another) {
        throw new NotImplementedException();
    }

    @Override
    public void close() throws Exception {
        for (Analyzer<?> analyzer : analyzers) {
            try {
                analyzer.close();
            } catch (Exception e) {
                LOGGER.error("Unable to close " + analyzer, e);
            }
        }
    }

}
