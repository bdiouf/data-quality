package org.talend.dataquality.datamasking.shuffling;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShuffleColumnTest {

    private String file = "Shuffling_test_data.csv";

    private String file5000 = "Shuffling_test_data_5000.csv";

    private String file10000 = "Shuffling_test_data_10000.csv";

    private String file20000 = "Shuffling_test_data_20000.csv";

    private String file50000 = "Shuffling_test_data_50000.csv";

    private String file100000 = "Shuffling_test_data_100000.csv";

    private static List<Integer> data = new ArrayList<Integer>();

    private static GenerateData generation = new GenerateData();

    private static List<List<String>> columns = new ArrayList<List<String>>();

    private static List<String> allColumns = Arrays
            .asList(new String[] { "id", "first_name", "last_name", "email", "gender", "birth", "city", "zip_code", "country" });

    private static ShuffleColumn shuffleColumn = null;

    @BeforeClass
    public static void generateData() {
        for (int i = 0; i < 14; i++) {
            data.add(i);
        }

        List<String> column1 = Arrays.asList(new String[] { "id", "first_name" });
        List<String> column2 = Arrays.asList(new String[] { "email" });
        List<String> column3 = Arrays.asList(new String[] { "city", "zip_code" });
        columns.add(column1);
        columns.add(column2);
        columns.add(column3);

        shuffleColumn = new ShuffleColumn(columns, allColumns);

    }

    @Test
    public void testReplacement() {
        int size = 995;
        int prime = 97;
        int shift = 0;
        do {
            shift = new Random().nextInt(size);
        } while (shift == 0);

        List<Integer> list1 = new ArrayList<Integer>();
        List<Integer> list2 = new ArrayList<Integer>();
        List<Integer> list3 = new ArrayList<Integer>();

        for (int i = 0; i < size; i++) {
            list1.add(((i + shift) < size) ? i + shift : i + shift - size);
            list2.add(((list1.get(i) + shift) < size) ? list1.get(i) + shift : list1.get(i) + shift - size);
            list3.add(((list2.get(i) + shift) < size) ? list2.get(i) + shift : list2.get(i) + shift - size);
        }

        List<Integer> replacements = shuffleColumn.calculateReplacementInteger(size, prime);
        for (int i = 0; i < size; i++) {
            // test whether the three cells can still cover the information
            int index1 = replacements.get(list1.get(i));
            int index2 = replacements.get(list2.get(i));
            int index3 = replacements.get(list3.get(i));
            assertTrue(index1 != index2);
            assertTrue(index3 != index2);
            assertTrue(index1 != index3);
        }
    }

    @Test
    public void testReplacementBigInteger() {
        int size = 23000000;
        int prime = 198491329;
        // System.out.println((long) Integer.MAX_VALUE * Integer.MAX_VALUE);

        for (long i = 0; i < size; i++) {
            int result = (int) (((i + 1) * prime) % size);
            if (result == i || (result < 0)) {
                System.out.println(i + " => " + result);
                fail("result is identical");
            }
        }
    }

    @Test
    public void testReplacement5000() {
        int size = 5000;
        int prime = 47;
        int shift = 0;
        do {
            shift = new Random().nextInt(size);
        } while (shift == 0);

        List<Integer> list1 = new ArrayList<Integer>();
        List<Integer> list2 = new ArrayList<Integer>();
        List<Integer> list3 = new ArrayList<Integer>();

        for (int i = 0; i < size; i++) {
            list1.add(((i + shift) < size) ? i + shift : i + shift - size);
            list2.add(((list1.get(i) + shift) < size) ? list1.get(i) + shift : list1.get(i) + shift - size);
            list3.add(((list2.get(i) + shift) < size) ? list2.get(i) + shift : list2.get(i) + shift - size);
        }

        List<Integer> replacements = shuffleColumn.calculateReplacementInteger(size, prime);
        for (int i = 0; i < size; i++) {
            // test whether the three cells can still cover the information
            int index1 = replacements.get(list1.get(i));
            int index2 = replacements.get(list2.get(i));
            int index3 = replacements.get(list3.get(i));
            assertTrue(index1 != index2);
            assertTrue(index3 != index2);
            assertTrue(index1 != index3);
        }
    }

    @Test
    public void testshuffleColumnsData1000() {

        List<List<Object>> fileDataShuffled = generation.getTableValue(file);
        List<List<Object>> fileData = generation.getTableValue(file);

        shuffleColumn.setRows(fileDataShuffled);

        shuffleColumn.shuffle();

        fileDataShuffled = shuffleColumn.getRows();

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
        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            Assert.assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            Assert.assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object oName = firstNameColumnL.get(i);

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));

        }

    }

    @Test
    public void testshuffleColumnsData5000() {

        List<List<Object>> fileDataShuffled = generation.getTableValue(file5000);
        List<List<Object>> fileData = generation.getTableValue(file5000);

        shuffleColumn.setRows(fileDataShuffled);

        shuffleColumn.shuffle();

        fileDataShuffled = shuffleColumn.getRows();

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
        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            Assert.assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            Assert.assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object oName = firstNameColumnL.get(i);

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));

        }

    }

    @Test
    public void testshuffleColumnsData10000() {

        List<List<Object>> fileDataShuffled = generation.getTableValue(file10000);
        List<List<Object>> fileData = generation.getTableValue(file10000);

        shuffleColumn.setRows(fileDataShuffled);

        shuffleColumn.shuffle();

        fileDataShuffled = shuffleColumn.getRows();

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
        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            Assert.assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            Assert.assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object oName = firstNameColumnL.get(i);

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));

        }

    }

    @Test
    public void testshuffleColumnsData20000() {

        List<List<Object>> fileDataShuffled = generation.getTableValue(file20000);
        List<List<Object>> fileData = generation.getTableValue(file20000);

        shuffleColumn.setRows(fileDataShuffled);

        shuffleColumn.shuffle();

        fileDataShuffled = shuffleColumn.getRows();

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
        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            Assert.assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            Assert.assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object oName = firstNameColumnL.get(i);

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));

        }

    }

    @Test
    public void testshuffleColumnsData50000() {

        List<List<Object>> fileDataShuffled = generation.getTableValue(file50000);
        List<List<Object>> fileData = generation.getTableValue(file50000);

        shuffleColumn.setRows(fileDataShuffled);

        shuffleColumn.shuffle();

        fileDataShuffled = shuffleColumn.getRows();

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
        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            Assert.assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            Assert.assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object oName = firstNameColumnL.get(i);

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));

        }

    }

    @Test
    public void testshuffleColumnsData100000() {

        List<List<Object>> fileDataShuffled = generation.getTableValue(file100000);
        List<List<Object>> fileData = generation.getTableValue(file100000);

        shuffleColumn.setRows(fileDataShuffled);

        shuffleColumn.shuffle();

        fileDataShuffled = shuffleColumn.getRows();
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
        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            Assert.assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            Assert.assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object oName = firstNameColumnL.get(i);

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));

        }

    }

}
