package org.talend.dataquality.shuffling;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.datamasking.shuffling.ShuffleMultipleColumns;

public class ShuffleMultipleColumnsTest {

    private static List<List<String>> data = new ArrayList<List<String>>();

    private ShuffleMultipleColumns test = new ShuffleMultipleColumns("Talend");

    private ShuffleMultipleColumns test2 = new ShuffleMultipleColumns("abcdefghij");

    private ShuffleMultipleColumns test3 = new ShuffleMultipleColumns("0123456789");

    @BeforeClass
    public static void prepareData() {
        try {
            data = new GenerateData().getMultipalData(new String[] { "email", "city", "zip code" });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        List<List<String>> output = new ArrayList<List<String>>();
        output = test.shuffleColumnsData(data);

        List<String> originalEmail = data.get(0);
        List<String> shuffledEmail = output.get(0);
        List<String> originalCity = data.get(1);
        List<String> shuffledCity = output.get(1);
        List<String> originalZip = data.get(2);
        List<String> shuffledZip = output.get(2);
        Assert.assertEquals(originalEmail.size(), shuffledEmail.size());
        for (int i = 0; i < originalEmail.size(); i++) {
            // test whether all the orders changes in email columns
            Assert.assertTrue(!originalEmail.get(i).equals(shuffledEmail.get(i)));
            // test whether it guarantees the information of zip code and city
            String email = shuffledEmail.get(i);
            String city = shuffledCity.get(i);
            String zip = shuffledZip.get(i);
            int position = originalEmail.indexOf(email);
            String ocity = originalCity.get(position);
            String ozip = originalZip.get(position);
            Assert.assertEquals(ocity, city);
            Assert.assertEquals(ozip, zip);
        }

    }

    @Test
    public void test2() {
        List<List<String>> output = new ArrayList<List<String>>();
        output = test3.shuffleColumnsData(data);

        List<String> originalEmail = data.get(0);
        List<String> shuffledEmail = output.get(0);
        List<String> originalCity = data.get(1);
        List<String> shuffledCity = output.get(1);
        List<String> originalZip = data.get(2);
        List<String> shuffledZip = output.get(2);
        Assert.assertEquals(originalEmail.size(), shuffledEmail.size());
        for (int i = 0; i < originalEmail.size(); i++) {
            // test whether all the orders changes in email columns
            Assert.assertTrue(!originalEmail.get(i).equals(shuffledEmail.get(i)));
            // test whether it guarantees the information of zip code and city
            String email = shuffledEmail.get(i);
            String city = shuffledCity.get(i);
            String zip = shuffledZip.get(i);
            int position = originalEmail.indexOf(email);
            String ocity = originalCity.get(position);
            String ozip = originalZip.get(position);
            Assert.assertEquals(ocity, city);
            Assert.assertEquals(ozip, zip);
        }

    }

    @Test
    public void test3() {
        List<List<String>> output = new ArrayList<List<String>>();
        output = test3.shuffleColumnsData(data);

        List<String> originalEmail = data.get(0);
        List<String> shuffledEmail = output.get(0);
        List<String> originalCity = data.get(1);
        List<String> shuffledCity = output.get(1);
        List<String> originalZip = data.get(2);
        List<String> shuffledZip = output.get(2);
        Assert.assertEquals(originalEmail.size(), shuffledEmail.size());
        for (int i = 0; i < originalEmail.size(); i++) {
            // test whether all the orders changes in email columns
            Assert.assertTrue(!originalEmail.get(i).equals(shuffledEmail.get(i)));
            // test whether it guarantees the information of zip code and city
            String email = shuffledEmail.get(i);
            String city = shuffledCity.get(i);
            String zip = shuffledZip.get(i);
            int position = originalEmail.indexOf(email);
            String ocity = originalCity.get(position);
            String ozip = originalZip.get(position);
            Assert.assertEquals(ocity, city);
            Assert.assertEquals(ozip, zip);
        }

    }
}
