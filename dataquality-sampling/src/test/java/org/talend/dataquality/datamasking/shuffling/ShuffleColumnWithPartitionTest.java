package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShuffleColumnWithPartitionTest {

    private static List<Integer> group = new ArrayList<Integer>();

    private static List<List<Integer>> numColumn = new ArrayList<List<Integer>>();

    private static GenerateData generator = new GenerateData();

    private ShuffleColumnWithPartition partition = new ShuffleColumnWithPartition();

    @BeforeClass
    public static void prepareData() {
        group.add(6);
        group.add(7);
        group.add(8);

        List<Integer> column1 = Arrays.asList(new Integer[] { 0, 1 });
        List<Integer> column2 = Arrays.asList(new Integer[] { 3 });
        numColumn.add(column1);
        numColumn.add(column2);
    }

    @Test
    public void testshuffleColumnDataByGroup() {
        List<List<Object>> fileDataShuffled = generator.getTableValue();
        List<List<Object>> fileData = generator.getTableValue();
        partition.shuffleColumnByGroup(fileDataShuffled, numColumn, Arrays.asList(new String[] { "talend", "computer" }), group);

        List<Object> idSL = new ArrayList<Object>();
        List<Object> firstNameSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> zipSL = new ArrayList<Object>();

        List<Object> idL = new ArrayList<Object>();
        List<Object> firstNameL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> zipL = new ArrayList<Object>();

        for (int i = 0; i < 1000; i++) {
            Object idS = fileDataShuffled.get(i).get(0);
            Object firstNameS = fileDataShuffled.get(i).get(1);
            Object emailS = fileDataShuffled.get(i).get(3);
            Object cityS = fileDataShuffled.get(i).get(6);
            Object zipS = fileDataShuffled.get(i).get(7);

            idSL.add(idS);
            firstNameSL.add(firstNameS);
            emailSL.add(emailS);
            citySL.add(cityS);
            zipSL.add(zipS);

            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object zip = fileData.get(i).get(7);

            idL.add(id);
            firstNameL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            zipL.add(zip);
        }

        for (int i = 0; i < 1000; i++) {

            Object zip = zipL.get(i);

            // test whether the city and the zip code are unique
            if (zipL.indexOf(zip) == zipL.lastIndexOf(zip)) {
                // only one record in the table, checks whether the information retains the same
                int idcmp = (int) idL.get(i);
                String fncmp = (String) firstNameL.get(i);
                String emailcmp = (String) emailL.get(i);

                int idscmp = (int) idSL.get(i);
                String fnscmp = (String) firstNameSL.get(i);
                String emailscmp = (String) emailSL.get(i);

                Assert.assertEquals(idcmp, idscmp);
                Assert.assertEquals(fncmp, fnscmp);
                Assert.assertEquals(emailcmp, emailscmp);

            }
        }

    }

}
