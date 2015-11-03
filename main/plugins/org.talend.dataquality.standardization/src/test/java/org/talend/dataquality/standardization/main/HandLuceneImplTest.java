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
package org.talend.dataquality.standardization.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

public class HandLuceneImplTest extends TestCase {

    public final static String PLUGIN_ID = "org.talend.dataquality.standardization.test"; // $NON-NLS-1$

    public final static String indexfolder = "data/TalendGivenNames_custom"; // $NON-NLS-1$

    public final static String filename = "src/test/resources/data/TalendGivenNames.TXT"; // $NON-NLS-1$

    private HandleLucene hl;

    public HandLuceneImplTest() {
        hl = new HandleLuceneImpl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        File f = new File(indexfolder);
        if (!f.exists()) {
            f.mkdir();
        }
        boolean back = hl.createIndex(filename, indexfolder);
        assertTrue("Index " + indexfolder + " not created.", back); // $NON-NLS-1$
    }

    @Test
    public void testGetSearchResultStringStringMapOfStringStringBoolean() {
        Map<String, String> information2value = new HashMap<String, String>();
        information2value.put("gender", "0"); // $NON-NLS-1$ // $NON-NLS-2$
        Map<String, String[]> hits = null;
        try {
            final String inputName = "Edou";
            hits = hl.getSearchResult(indexfolder, inputName, information2value, false); // $NON-NLS-1$
            String[] soreDocs = hits.get(inputName); // $NON-NLS-1$
            assertNotNull(soreDocs);
            if (soreDocs != null) {
                boolean found = false;
                for (String doc : soreDocs) {
                    if (inputName.equals(doc)) {
                        found = true;
                        break;
                    }
                    System.out.println(doc);
                }
                assertTrue(inputName + " was not found", found);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHandle() {
        try {

            String res = hl.replaceName(indexfolder, "Philippe", false);//$NON-NLS-1$
            assertEquals("Philippe", res);
            try {
                String res1 = hl.replaceNameWithCountryInfo(indexfolder, "Philippe", "china", false);//$NON-NLS-1$ $NON-NLS-2$
                assertEquals("Philippe", res1);

                String res2 = hl.replaceNameWithGenderInfo(indexfolder, "Philippe", "0", false);//$NON-NLS-1$ $NON-NLS-2$
                assertEquals("Philippe", res2);
                String res3 = hl.replaceNameWithCountryGenderInfo(indexfolder, "Philippe", "china", "1", false);//$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                assertEquals("Philippe", res3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
