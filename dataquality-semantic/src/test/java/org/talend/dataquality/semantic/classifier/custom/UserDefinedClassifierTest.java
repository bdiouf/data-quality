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
package org.talend.dataquality.semantic.classifier.custom;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.ISubCategory;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class UserDefinedClassifierTest {

    // private UserDefinedClassifier userDefinedClassifier = null;

    /**
     * created by talend on 2015-07-28 Detailled comment.
     *
     */
    private static enum STATE {
        Alabama,
        Alaska,
        Arizona,
        Arkansas,
        California,
        Colorado,
        Connecticut,
        Delaware,
        Florida,
        Georgia,
        Hawaii,
        Idaho,
        Illinois,
        Indiana,
        Iowa,
        Kansas,
        Kentucky,
        Louisiana,
        Maine,
        Maryland,
        Massachusetts,
        Michigan,
        Minnesota,
        Mississippi,
        Missouri,
        Montana,
        Nebraska,
        Nevada,
        New_Hampshire,
        New_Jersey,
        New_Mexico,
        New_York,
        North_Carolina,
        North_Dakota,
        Ohio,
        Oklahoma,
        Oregon,
        Pennsylvania,
        Rhode_Island,
        South_Carolina,
        South_Dakota,
        Tennessee,
        Texas,
        Utah,
        Vermont,
        Virginia,
        Washington,
        West_Virginia,
        Wisconsin,
        Wyoming;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return super.toString().replace("_", " "); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public static Map<String, String[]> TEST_DATA = new LinkedHashMap<String, String[]>() {

        private static final long serialVersionUID = -5067273062214728849L;

        {
            // put (value, expected categories)
            put("CDG", new String[] {}); //$NON-NLS-1$
            put("suresnes", new String[] {});//$NON-NLS-1$
            put("Paris", new String[] {});//$NON-NLS-1$
            put("France", new String[] {});//$NON-NLS-1$
            put("? - lfd", new String[] {});//$NON-NLS-1$
            put("CHN", new String[] {});//$NON-NLS-1$
            put("cat", new String[] {});//$NON-NLS-1$
            put("2012-02-03 7:08PM", new String[] {});//$NON-NLS-1$
            put("1/2/2012", new String[] {});//$NON-NLS-1$
            put("january", new String[] { "EN_MONTH" });//$NON-NLS-1$ //$NON-NLS-2$
            put("jan", new String[] { "EN_MONTH_ABBREV" });//$NON-NLS-1$ //$NON-NLS-2$
            put("february", new String[] { "EN_MONTH" });//$NON-NLS-1$ //$NON-NLS-2$
            put("march", new String[] { "EN_MONTH" });//$NON-NLS-1$ //$NON-NLS-2$
            put("auG", new String[] { "EN_MONTH_ABBREV" });//$NON-NLS-1$ //$NON-NLS-2$
            put("MAY", new String[] { "EN_MONTH", "EN_MONTH_ABBREV" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            put("januar", new String[] {});//$NON-NLS-1$
            put("janvier", new String[] {});//$NON-NLS-1$

            put("AB123456C", new String[] { "UK_SSN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("AB 12 34 56 C", new String[] { "UK_SSN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("TN 31 12 58 F", new String[] { "UK_SSN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("20120101-3842", new String[] { "SE_SSN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("120101-3842", new String[] {});//$NON-NLS-1$

            put("christophe", new String[] {});//$NON-NLS-1$
            put("sda@talend.com", new String[] { "EMAIL" });//$NON-NLS-1$ //$NON-NLS-2$
            put("abc@gmail.com", new String[] { "EMAIL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put(" abc@gmail.com ", new String[] { "EMAIL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("abc@gmail.com ", new String[] { "EMAIL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put(" abc@gmail.com", new String[] { "EMAIL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("abc@gmail", new String[] {}); //$NON-NLS-1$
            put("12345", new String[] { "FR_CODE_COMMUNE_INSEE", "FR_POSTAL_CODE", "DE_POSTAL_CODE", "US_POSTAL_CODE" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            put("2A345", new String[] { "FR_CODE_COMMUNE_INSEE" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("12345-6789", new String[] { "US_POSTAL_CODE" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            put("Talend", new String[] {}); //$NON-NLS-1$
            put("9 rue pages, 92150 suresnes", new String[] {}); //$NON-NLS-1$
            put("avenue des champs elysees", new String[] {}); //$NON-NLS-1$
            put("MA", new String[] { "US_STATE_CODE" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("FL", new String[] { "US_STATE_CODE" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("FLorIda", new String[] { "US_STATE" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("FLORIDA", new String[] { "US_STATE" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("New Hampshire", new String[] { "US_STATE" });//$NON-NLS-1$ //$NON-NLS-2$
            put("Arizona", new String[] { "US_STATE" });//$NON-NLS-1$ //$NON-NLS-2$
            put("Alabama", new String[] { "US_STATE" });//$NON-NLS-1$ //$NON-NLS-2$
            put("F", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("M", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Male", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("female", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$

            put("http://www.talend.com", new String[] { "URL" });//$NON-NLS-1$ //$NON-NLS-2$
            put("www.talend.com", new String[] { "WEB_DOMAIN" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            put("talend.com", new String[] { "WEB_DOMAIN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("talend.com", new String[] { "WEB_DOMAIN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("talend.veryLongTDL", new String[] { "WEB_DOMAIN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("talend.TDLlongerThan25Characters", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("talendSmallerThan63Charactersxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.com", new String[] { "WEB_DOMAIN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("talendLongerThan63Charactersxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.com", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$

            put("1 81 04 95 201 569 62", new String[] { "FR_SSN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("1810495201569", new String[] { "FR_SSN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("123-45-6789", new String[] { "US_SSN" });//$NON-NLS-1$ //$NON-NLS-2$
            put("azjfnskjqnfoajr", new String[] {});//$NON-NLS-1$
            put("ISBN 9-787-11107-5", new String[] { "ISBN_10" });//$NON-NLS-1$ //$NON-NLS-2$
            put("SINB 9-787-11107-5", new String[] {});//$NON-NLS-1$
            put("ISBN 2-711-79141-6", new String[] { "ISBN_10" });//$NON-NLS-1$ //$NON-NLS-2$
            put("ISBN-13: 978-2711791415", new String[] { "ISBN_13" });//$NON-NLS-1$ //$NON-NLS-2$
            put("ISBN: 978-2711791415", new String[] { "ISBN_13" });//$NON-NLS-1$ //$NON-NLS-2$
            put("ISBN 978-2711791415", new String[] { "ISBN_13" });//$NON-NLS-1$ //$NON-NLS-2$

            put("A4:4E:31:B9:C5:B4", new String[] { "MAC_ADDRESS" });//$NON-NLS-1$ //$NON-NLS-2$
            put("A4:4E:31:B9:C5:B4:B4", new String[] {});//$NON-NLS-1$
            put("A4-4E-31-B9-C5-B4", new String[] {});//$NON-NLS-1$

            put("$3,000", new String[] { "EN_MONEY_AMOUNT" });//$NON-NLS-1$ //$NON-NLS-2$
            put("$3000", new String[] { "EN_MONEY_AMOUNT" });//$NON-NLS-1$ //$NON-NLS-2$
            put("$ 3000", new String[] {});//$NON-NLS-1$
            put("CA$3000", new String[] { "EN_MONEY_AMOUNT" });//$NON-NLS-1$ //$NON-NLS-2$
            put("€3000", new String[] { "EN_MONEY_AMOUNT" });//$NON-NLS-1$ //$NON-NLS-2$
            put("3000 €", new String[] { "FR_MONEY_AMOUNT" });//$NON-NLS-1$ //$NON-NLS-2$
            put("345,56 €", new String[] { "FR_MONEY_AMOUNT" });//$NON-NLS-1$ //$NON-NLS-2$
            put("35 k€", new String[] { "FR_MONEY_AMOUNT" });//$NON-NLS-1$ //$NON-NLS-2$
            put("35 054 T€", new String[] { "FR_MONEY_AMOUNT" });//$NON-NLS-1$ //$NON-NLS-2$
            put("35 456 544 k£", new String[] { "FR_MONEY_AMOUNT" });//$NON-NLS-1$ //$NON-NLS-2$

            put("00496-8738059275", new String[] { "DE_PHONE" });//$NON-NLS-1$ //$NON-NLS-2$
            put("00338.01345678", new String[] { "FR_PHONE" });//$NON-NLS-1$ //$NON-NLS-2$

            put("John Doe", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush Jr.", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush, Jr.", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush II", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush III", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush IV", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges Bush IV", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Jean-Michel Louis", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("David F Walker", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("J. S. Smith, Jr.", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Catherine Zeta-Jones", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$

            put("#990000", new String[] { "COLOR_HEX_CODE" });//$NON-NLS-1$ //$NON-NLS-2$
            put("#AAAAAA", new String[] { "COLOR_HEX_CODE" });//$NON-NLS-1$ //$NON-NLS-2$
            put("#cc3366", new String[] { "COLOR_HEX_CODE" });//$NON-NLS-1$ //$NON-NLS-2$
            put("#c1d906", new String[] { "COLOR_HEX_CODE" });//$NON-NLS-1$ //$NON-NLS-2$

            put("BG123456789", new String[] { "BG_VAT_NUMBER" });//$NON-NLS-1$ //$NON-NLS-2$
            put("BG123456789", new String[] { "BG_VAT_NUMBER" });//$NON-NLS-1$ //$NON-NLS-2$
            put("AT12345678", new String[] { "AT_VAT_NUMBER" });//$NON-NLS-1$ //$NON-NLS-2$

            // put("132.2356", new String[] { "LONGITUDE_LATITUDE_COORDINATE" });
            put("40.7127837,-74.00594130000002", new String[] { "GPS_COORDINATE" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("00:00", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$
            put("12:00", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$
            put("11:23", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$
            put("15:53", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$
            put("23:59", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$

            put("Monday", new String[] { "EN_WEEK_DAY" }); //$NON-NLS-1$ //<$NON-NLS-2$
            put("MonDay", new String[] { "EN_WEEK_DAY" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("MOnDay", new String[] { "EN_WEEK_DAY" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("MOn", new String[] { "EN_WEEK_DAY" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("Tue", new String[] { "EN_WEEK_DAY" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("Wed", new String[] { "EN_WEEK_DAY" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("Wednesday", new String[] { "EN_WEEK_DAY" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("Thurs", new String[] { "EN_WEEK_DAY" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("25:59", new String[] {}); // does not match TIME (as expected) //$NON-NLS-1$

            put("0067340", new String[] { "SEDOL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("4155586", new String[] { "SEDOL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("(541) 754-3010", new String[] { "US_PHONE" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("B01HL06", new String[] { "SEDOL" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("132.2356", new String[] {}); //$NON-NLS-1$
            put("R&D", new String[] {}); //$NON-NLS-1$

            put("hdfs://127.0.0.1/user/luis/sample.txt", new String[] { "HDFS_URL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("hdfs://toto.com/user/luis/sample.txt", new String[] { "HDFS_URL" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("file://localhost/c/WINDOWS/clock.avi", new String[] { "FILE_URL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("file://localhost/c|/WINDOWS/clock.avi", new String[] { "FILE_URL" }); //$NON-NLS-1$ //$NON-NLS-2$ "
            put("file://localhost/c:/WINDOWS/clock.avi", new String[] { "FILE_URL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("file:///C:/WORKSPACE/reports.html", new String[] { "FILE_URL" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("mailto:?to=&subject=mailto%20with%20examples&body=http://en.wikipedia.org/wiki/Mailto", //$NON-NLS-1$
                    new String[] { "MAILTO_URL" }); //$NON-NLS-1$
            put("mailto:someone@example.com?subject=This%20is%20the%20subject", new String[] { "MAILTO_URL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("mailto:p.dupond@example.com?subject=Sujet%20du%20courrier&cc=pierre@example.org&cc=jacques@example.net&body=Bonjour", //$NON-NLS-1$
                    new String[] { "MAILTO_URL" }); //$NON-NLS-1$

            put("data:text/html;charset=US-ASCII,%3Ch1%3EHello!%3C%2Fh1%3E", new String[] { "DATA_URL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("data:text/html;charset=,%3Ch1%3EHello!%3C%2Fh1%3E", new String[] { "DATA_URL" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQAQMAAAAlPW0iAAAABlBMVEUAAAD///+l2Z/dAAAA", //$NON-NLS-1$
                    new String[] { "DATA_URL" }); //$NON-NLS-1$
            put("data:,Hello World!", new String[] { "DATA_URL" }); //$NON-NLS-1$ //$NON-NLS-2$
        }
    };

    @Before
    public void prepare() {
        for (STATE state : STATE.values()) {
            TEST_DATA.put(state.toString(), new String[] { "US_STATE" }); //$NON-NLS-1$
        }
    }

    @Test
    public void testClassify() throws IOException {
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        for (String str : TEST_DATA.keySet()) {
            Set<String> cats = userDefinedClassifier.classify(str);
            String[] expect_values = TEST_DATA.get(str);
            assertEquals("unexpected size for " + str, expect_values.length, cats.size()); //$NON-NLS-1$
            Object[] catsArray = new String[cats.size()];
            int i = 0;
            for (String cat : cats) {
                catsArray[i++] = cat;
            }
            Arrays.sort(catsArray);
            Arrays.sort(expect_values);
            assertArrayEquals("wrong category found for input string: " + str, expect_values, catsArray); //$NON-NLS-1$
        }
    }

    @Test
    public void testUniqueNames() throws IOException {
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        Set<ISubCategory> classifiers = userDefinedClassifier.getClassifiers();
        Set<String> names = new HashSet<>();
        for (ISubCategory iSubCategory : classifiers) {
            String name = iSubCategory.getName();
            assertTrue("Category Name: " + name + " is duplicated!", names.add(name)); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    @Test
    public void testUniqueIds() throws IOException {
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        Set<ISubCategory> classifiers = userDefinedClassifier.getClassifiers();
        Set<String> ids = new HashSet<>();
        for (ISubCategory iSubCategory : classifiers) {
            String id = iSubCategory.getId();
            assertTrue("Category Id: " + id + " is duplicated!", ids.add(id)); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    @Test
    public void testAddAndRemoveSubCategory() throws IOException {
        UserDefinedClassifier userDefinedClassifier = new UserDefinedClassifier();
        addAndRemoveCategories(userDefinedClassifier);

        userDefinedClassifier = UDCategorySerDeser.readJsonFile();
        addAndRemoveCategories(userDefinedClassifier);
    }

    @Test
    public void testInsertOrUpdate() {
        UserDefinedClassifier userDefinedClassifier = new UserDefinedClassifier();
        String id = "this is the Id"; //$NON-NLS-1$
        UserDefinedCategory cat = new UserDefinedCategory(id);
        assertTrue(userDefinedClassifier.insertOrUpdateSubCategory(cat));
        assertEquals("by default, the name should be same as the id!", id, cat.getName()); //$NON-NLS-1$
        cat.setName("my name"); //$NON-NLS-1$
        assertTrue(userDefinedClassifier.insertOrUpdateSubCategory(cat));
        Iterator<ISubCategory> it = userDefinedClassifier.getClassifiers().iterator();
        while (it.hasNext()) {
            ISubCategory c = it.next();
            assertEquals(cat.getName(), c.getName());
        }
    }

    private void addAndRemoveCategories(UserDefinedClassifier userDefinedClassifier) {
        int sizeBefore = userDefinedClassifier.getClassifiers().size();
        String id = "this is the Id"; //$NON-NLS-1$
        UserDefinedCategory cat = new UserDefinedCategory(id);
        userDefinedClassifier.removeSubCategory(cat);
        int sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals("Expect to have the same size because the removed category does not exist in the list of categories. Size=" //$NON-NLS-1$
                + userDefinedClassifier.getClassifiers().size(), sizeBefore, sizeAfter);

        userDefinedClassifier.addSubCategory(cat);
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have a different size because we add a category that does not exist in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

        userDefinedClassifier.addSubCategory(cat);
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have only one more element than the original size because the category now exists in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

        userDefinedClassifier.removeSubCategory(cat);
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals("Expect to have the same size because we removed the added category. Size=" //$NON-NLS-1$
                + userDefinedClassifier.getClassifiers().size(), sizeBefore, sizeAfter);

        userDefinedClassifier.removeSubCategory(cat);
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals("Expect to have the same size because we removed twice the same category. Size=" //$NON-NLS-1$
                + userDefinedClassifier.getClassifiers().size(), sizeBefore, sizeAfter);

        // now add again twice
        assertTrue(userDefinedClassifier.addSubCategory(cat));
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have a different size because we add a category that does not exist in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

        assertFalse(userDefinedClassifier.addSubCategory(cat));
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have a still have the same size because we add a category that already exists in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

        UserDefinedCategory cat2 = new UserDefinedCategory(id);
        cat2.setName("my name"); //$NON-NLS-1$
        assertFalse(userDefinedClassifier.addSubCategory(cat2));
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have a still have the same size because we add a category that already exists in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

    }
}
