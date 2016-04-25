package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShuffleColumnTest {

    private static List<Integer> data = new ArrayList<Integer>();

    private static GenerateData generation = new GenerateData();

    private static List<List<Integer>> columns = new ArrayList<List<Integer>>();

    private static List<String> keys = Arrays.asList(new String[] { "talend", "apple", "français" });

    private ShuffleColumn shuffleColumn = new ShuffleColumn();

    @BeforeClass
    public static void generateData() {
        for (int i = 0; i < 14; i++) {
            data.add(i);
        }

        GenerateData generation = new GenerateData();
        List<Integer> idName = Arrays
                .asList(new Integer[] { generation.getColumnIndex("id"), generation.getColumnIndex("first_name") });
        List<Integer> email = Arrays.asList(new Integer[] { generation.getColumnIndex("email") });
        List<Integer> address = Arrays
                .asList(new Integer[] { generation.getColumnIndex("city"), generation.getColumnIndex("zip code") });

        columns.add(idName);
        columns.add(email);
        columns.add(address);
    }

    @Test
    public void testGetKeyOrder1() {
        String key = "apple";
        List<Integer> keyOrder = shuffleColumn.getOrderFromKey(key);
        int[] expected = new int[] { 0, 3, 4, 2, 1 };

        for (int i = 0; i < keyOrder.size(); i++) {
            Assert.assertEquals(expected[i], keyOrder.get(i).intValue());
        }

    }

    @Test
    public void testGetKeyOrder2() {
        String key = "Talend";
        List<Integer> keyOrder = shuffleColumn.getOrderFromKey(key);
        int[] expected = new int[] { 0, 1, 4, 3, 5, 2 };

        for (int i = 0; i < keyOrder.size(); i++) {
            Assert.assertEquals(expected[i], keyOrder.get(i).intValue());
        }
    }

    @Test
    public void testGetKeyOrder3() {
        String key = "français";
        List<Integer> keyOrder = shuffleColumn.getOrderFromKey(key);
        int[] expected = new int[] { 2, 5, 0, 4, 7, 1, 3, 6 };

        for (int i = 0; i < keyOrder.size(); i++) {
            Assert.assertEquals(expected[i], keyOrder.get(i).intValue());
        }
    }

    @Test
    public void testShuffledIndexArray() {
        int[][] grid = new int[3][5];
        int i = 0;
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 5; column++) {
                grid[row][column] = i++;
            }
        }

        for (int column = 0; column < 4; column++) {
            grid[2][column] = i++;
        }

        List<List<Integer>> allIndex = new ArrayList<List<Integer>>();

        // Original index
        List<Integer> oIndex = new ArrayList<Integer>();
        for (int j = 0; j < data.size(); j++) {
            oIndex.add(j);
        }
        allIndex.add(oIndex);

        // first shiffling
        Integer[] firstOutput = { 0, 5, 10, 1, 6, 11, 2, 7, 12, 3, 8, 13, 4, 9 };
        List<Integer> fList = Arrays.asList(firstOutput);
        List<Integer> fListExpected = Arrays.asList(new Integer[] { 5, 0, 10, 1, 6, 11, 2, 12, 7, 3, 8, 13, 4, 9 });
        shuffleColumn.scrambleOutputArray(fList, allIndex);
        for (int j = 0; j < fList.size(); j++) {
            Assert.assertEquals(fListExpected.get(j), fList.get(j));
        }

        // Second tour
        allIndex.add(fList);
        List<Integer> sList = Arrays.asList(new Integer[] { 5, 11, 8, 0, 2, 13, 10, 12, 4, 1, 7, 9, 6, 3 });
        shuffleColumn.scrambleOutputArray(sList, allIndex);
        List<Integer> sListExpected = Arrays.asList(new Integer[] { 11, 5, 8, 0, 2, 13, 10, 4, 12, 1, 7, 9, 6, 3 });
        for (int j = 0; j < fList.size(); j++) {
            Assert.assertEquals(sListExpected.get(j), sList.get(j));
        }

        // Third tour
        allIndex.add(sList);
        List<Integer> tList = Arrays.asList(new Integer[] { 11, 13, 7, 5, 10, 9, 8, 4, 6, 0, 12, 3, 2, 1 });
        shuffleColumn.scrambleOutputArray(tList, allIndex);
        List<Integer> tListExpected = Arrays.asList(new Integer[] { 13, 11, 7, 5, 10, 9, 8, 6, 4, 0, 12, 3, 2, 1 });
        for (int j = 0; j < tList.size(); j++) {
            Assert.assertEquals(tListExpected.get(j), tList.get(j));
        }

        // Beside the tour, some special situation
        allIndex.add(tList);
        List<Integer> ffList = Arrays.asList(new Integer[] { 11, 5, 7, 10, 9, 8, 6, 4, 13, 0, 12, 3, 2, 1 });
        shuffleColumn.scrambleOutputArray(ffList, allIndex);
        List<Integer> ffListExpected = Arrays.asList(new Integer[] { 7, 10, 11, 9, 5, 8, 4, 13, 6, 12, 0, 1, 3, 2 });
        for (int j = 0; j < fList.size(); j++) {
            Assert.assertEquals(ffListExpected.get(j), ffList.get(j));
        }

    }

    @Test
    public void test1WithSmallIndexArray() {
        List<List<Integer>> allIndex = new ArrayList<List<Integer>>();
        allIndex.add(Arrays.asList(new Integer[] { 1, 2, 3 }));
        allIndex.add(Arrays.asList(new Integer[] { 1, 3, 2 }));
        allIndex.add(Arrays.asList(new Integer[] { 2, 3, 1 }));
        allIndex.add(Arrays.asList(new Integer[] { 2, 1, 3 }));
        allIndex.add(Arrays.asList(new Integer[] { 3, 1, 2 }));
        allIndex.add(Arrays.asList(new Integer[] { 3, 2, 1 }));

        List<Integer> test = Arrays.asList(new Integer[] { 1, 2, 3 });
        shuffleColumn.scrambleOutputArray(test, allIndex);
        for (int i = 0; i < test.size(); i++) {
            Assert.assertEquals(allIndex.get(0).get(i), test.get(i));
        }

    }

    @Test
    public void testOneColumn() {
        GenerateData generation = new GenerateData();

        List<List<Object>> fileDataShuffled = generation.getTableValue();
        List<List<Integer>> columnsOne = new ArrayList<List<Integer>>();
        List<Integer> email = Arrays.asList(new Integer[] { generation.getColumnIndex("email") });
        columnsOne.add(email);
        List<String> keys = Arrays.asList(new String[] { "apple" });

        // test with email

        List<List<Object>> fileData = new ArrayList<List<Object>>();
        fileData = generation.getTableValue();
        shuffleColumn.shuffleColumnsData(fileDataShuffled, columnsOne, keys);

        // test whether email are all changes
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue(!fileData.get(i).get(3).equals(fileDataShuffled.get(i).get(3)));
        }

    }

    @Test
    public void testshuffleColumnsData() {

        List<List<Object>> fileDataShuffled = generation.getTableValue();
        List<List<Object>> fileData = generation.getTableValue();
        shuffleColumn.shuffleColumnsData(fileDataShuffled, columns, keys);

        List<Object> idColumnSL = new ArrayList<Object>();
        List<Object> firstNameColumnSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> zipSL = new ArrayList<Object>();

        List<Object> idColumnL = new ArrayList<Object>();
        List<Object> firstNameColumnL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> zipL = new ArrayList<Object>();

        for (int i = 0; i < fileData.size(); i++) {
            Object idS = fileDataShuffled.get(i).get(0);
            Object firstNameS = fileDataShuffled.get(i).get(1);
            Object emailS = fileDataShuffled.get(i).get(3);
            Object cityS = fileDataShuffled.get(i).get(6);
            Object zipS = fileDataShuffled.get(i).get(7);

            idColumnSL.add(idS);
            firstNameColumnSL.add(firstNameS);
            emailSL.add(emailS);
            citySL.add(cityS);
            zipSL.add(zipS);

            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object zip = fileData.get(i).get(7);

            idColumnL.add(id);
            firstNameColumnL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            zipL.add(zip);
            // test whether all ids are shuffled
            Assert.assertTrue(!id.equals(idS));

            // test whether all email are shuffled
            Assert.assertTrue(!email.equals(emailS));
        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            Assert.assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            Assert.assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object oName = firstNameColumnL.get(i);
            Object oEmail = emailL.get(i);
            Object oZip = zipL.get(i);

            int soIndex = emailSL.indexOf(oEmail);
            Object sZip = zipSL.get(soIndex);
            Object sID = idColumnSL.get(soIndex);

            // test whether the email the real address are masked
            Assert.assertTrue(!oZip.equals(sZip));
            Assert.assertTrue(!oid.equals(sID));

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));
        }

    }

}
