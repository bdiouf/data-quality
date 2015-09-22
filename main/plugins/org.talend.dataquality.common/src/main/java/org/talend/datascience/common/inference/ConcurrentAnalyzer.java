// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.datascience.common.inference;

import java.util.List;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.talend.datascience.common.inference.Analyzer;
/**
 * 
 * @author zhao
 *
 * @param <T>
 */
public class ConcurrentAnalyzer<T> implements Analyzer<T> {

    private static final long serialVersionUID = 6896234073310039985L;

    private final KeyedObjectPool<Thread, Analyzer<T>> pool;

    private ConcurrentAnalyzer(int maxSize, AnalyzerSupplier<Analyzer<T>> supplier) {
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        config.maxTotal = maxSize;
        config.maxActive = maxSize;
        config.maxIdle = maxSize;
        config.maxWait = 3000;
        this.pool = new GenericKeyedObjectPool<>(new Factory<>(supplier), config);
    }

    public static <T> Analyzer<T> make(AnalyzerSupplier<Analyzer<T>> supplier, int maxSize) {
        return new ConcurrentAnalyzer<>(maxSize, supplier);
    }

    private synchronized Analyzer<T> get() {
        try {
            return pool.borrowObject(Thread.currentThread());
        } catch (Exception e) {
            throw new RuntimeException("Unable to get analyzer for current thread.", e);
        }
    }

    @Override
    public void init() {
        Analyzer<T> analyzer = get();
        analyzer.init();
        returnObject(analyzer);
    }

    @Override
    public  boolean analyze(String... record) {
        Analyzer<T> analyzer = get();
        boolean result = analyzer.analyze(record);
        returnObject(analyzer);
        return result;
    }

    private synchronized void returnObject(Analyzer<T> analyzer) throws RuntimeException {
        try {
            pool.returnObject(Thread.currentThread(), analyzer);
        } catch (Exception e) {
            throw new RuntimeException("Unable to return analyzer for current thread.", e);
        }
    }

    @Override
    public void end() {
        Analyzer<T> analyzer = get();
        analyzer.end();
        returnObject(analyzer);
    }

    @Override
    public List<T> getResult() {
        Analyzer<T> analyzer = get();
        List<T> result =  analyzer.getResult();
        returnObject(analyzer);
        return result;
    }

    @Override
    public Analyzer<T> merge(Analyzer<T> another) {
        Analyzer<T> analyzer = get();
        Analyzer<T> result =  analyzer.merge(another);
        returnObject(analyzer);
        return result;
    }

    private static class Factory<T> implements KeyedPoolableObjectFactory<Thread, Analyzer<T>> {

        private final AnalyzerSupplier<Analyzer<T>> supplier;

        public Factory(AnalyzerSupplier<Analyzer<T>> supplier) {
            this.supplier = supplier;
        }

        @Override
        public synchronized Analyzer<T> makeObject(Thread key) throws Exception {
            return supplier.get();
        }

        @Override
        public void destroyObject(Thread key, Analyzer<T> obj) throws Exception {
        }

        @Override
        public boolean validateObject(Thread key, Analyzer<T> obj) {
            return true;
        }

        @Override
        public void activateObject(Thread key, Analyzer<T> obj) throws Exception {
        }

        @Override
        public void passivateObject(Thread key, Analyzer<T> obj) throws Exception {
        }
    }

    @Override
    public void close() throws Exception {
        try {
            pool.clear(Thread.currentThread());
        } catch (Exception e) {
            throw new RuntimeException("Unable to close analyzer for current thread.", e);
        }
    }

}
