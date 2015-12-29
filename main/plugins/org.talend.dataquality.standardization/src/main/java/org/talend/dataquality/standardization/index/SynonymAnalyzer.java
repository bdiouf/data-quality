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
package org.talend.dataquality.standardization.index;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SynonymAnalyzer extends Analyzer {

    public SynonymAnalyzer() {
    }

    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream result = new StandardFilter(new StopFilter(true, new LowerCaseFilter(new StandardTokenizer(Version.LUCENE_30,
                reader)), StopAnalyzer.ENGLISH_STOP_WORDS_SET));
        return result;
    }
    /*
     * public TokenStream tokenStream(String fieldName, Reader reader) { TokenStream result = new SynonymFilter(new
     * StandardFilter(new LowerCaseFilter(new StandardTokenizer(Version.LUCENE_30, reader))), engine); return result; }
     */
}
