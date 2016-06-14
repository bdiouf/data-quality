package org.talend.dataquality.datamasking.shuffling;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ShuffleColumnTest {

    private String file5000 = "Shuffling_test_data_5000.csv";

    private String file50000 = "Shuffling_test_data_50000.csv";

    private static List<Integer> data = new ArrayList<>();

    private static GenerateData generation = new GenerateData();

    private static List<List<String>> columns = new ArrayList<>();

    private static List<String> allColumns = Arrays
            .asList(new String[] { "id", "first_name", "last_name", "email", "gender", "birth", "city", "zip_code", "country" });

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
    public void testOneColumnBigInteger() {
        int partition = 10000;
        int size = 1000000;

        List<List<String>> id = new ArrayList<>();
        List<String> idc = new ArrayList<>();
        idc.add("id");
        id.add(idc);

        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();
        ShufflingService service = new ShufflingService(id, idc);
        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(partition);

        for (int i = 0; i < size; i++) {
            List<Object> row = Arrays.asList((Object) (i + ""));
            service.addOneRow(row);
        }

        service.setHasFinished(true);
        Assert.assertEquals(size / partition, result.size());
        System.out.println(">>>>>>> " + result.size());
        for (int i = 0; i < result.size(); i++) {
            List<List<Object>> rows = result.poll();
            for (int position = 0; position < rows.size(); position++) {
                int item = Integer.parseInt(rows.get(position).get(0).toString());
                // the partition is good
                Assert.assertTrue(item < partition * (i + 1));
                Assert.assertTrue(item >= partition * i);
                // the position changes
                Assert.assertTrue(item != position);
            }

        }
    }

    @Test
    @Ignore
    public void testOneColumnBigIntegerHasModulo() {
        int partition = 100000;
        int size = 10000999;

        List<List<String>> id = new ArrayList<>();
        List<String> idc = new ArrayList<>();
        idc.add("id");
        id.add(idc);

        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();
        ShufflingService service = new ShufflingService(id, idc);
        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(partition);

        service.setSeperationSize(partition);
        for (int i = 0; i < size; i++) {
            List<Object> row = Arrays.asList((Object) (i + ""));
            service.addOneRow(row);
        }
        service.setHasFinished(true);
        Assert.assertEquals(size / partition, result.size() - 1);
        for (int i = 0; i < size / partition; i++) {
            List<List<Object>> rows = result.poll();
            for (int position = 0; position < rows.size(); position++) {
                int item = Integer.parseInt(rows.get(position).get(0).toString());
                // the partition is good
                Assert.assertTrue(item < partition * (i + 1));
                Assert.assertTrue(item >= partition * i);
                // the position changes
                Assert.assertTrue(item != position);
            }
        }
        // test last rows
        List<List<Object>> rows = result.poll();
        for (int position = 0; position < rows.size(); position++) {
            int item = Integer.parseInt(rows.get(position).get(0).toString());
            // the partition is good
            Assert.assertTrue(item < size);
            Assert.assertTrue(item >= partition * (size / partition));
            // the position changes
            Assert.assertTrue(item != position);
        }
    }

    @Test
    public void testshuffleColumnsData1000() throws InterruptedException {
        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();

        ShufflingService service = new ShufflingService(columns, allColumns);

        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(100000);

        List<List<Object>> fileData = generation.getTableValue(GenerateData.SHUFFLING_DATA_PATH);
        long time1 = System.currentTimeMillis();
        service.setRows(fileData);
        Thread.sleep(1000);
        long time2 = System.currentTimeMillis();
        service.setHasFinished(true);
        System.out.println("1000 line generation time " + (time2 - time1));

        Assert.assertEquals(1, result.size());

        List<Object> idColumnSL = new ArrayList<>();
        List<Object> firstNameColumnSL = new ArrayList<>();
        List<Object> emailSL = new ArrayList<>();
        List<Object> citySL = new ArrayList<>();
        List<Object> zipSL = new ArrayList<>();

        List<Object> idColumnL = new ArrayList<>();
        List<Object> firstNameColumnL = new ArrayList<>();
        List<Object> emailL = new ArrayList<>();
        List<Object> cityL = new ArrayList<>();
        List<Object> zipL = new ArrayList<>();
        // Initialize the shuffled data
        for (int group = 0; group < result.size(); group++) {
            List<List<Object>> rows = result.poll();
            for (List<Object> row : rows) {
                Object idS = row.get(0);
                Object firstNameS = row.get(1);
                Object emailS = row.get(3);
                Object cityS = row.get(6);
                Object zipS = row.get(7);

                idColumnSL.add(idS);
                firstNameColumnSL.add(firstNameS);
                emailSL.add(emailS);
                citySL.add(cityS);
                zipSL.add(zipS);
            }
        }

        // Initialize the original data set
        for (int i = 0; i < fileData.size(); i++) {
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
            Object nid = idColumnSL.get(i);

            Object oemail = emailL.get(i);
            Object nemail = emailSL.get(i);
            Object oName = firstNameColumnL.get(i);
            // test whether email and id information have all changed
            Assert.assertTrue(!oid.equals(nid) || !oemail.equals(nemail));

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));
        }

    }

    @Test
    public void testshuffleColumnsData5000() {
        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();

        ShufflingService service = new ShufflingService(columns, allColumns);

        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(100000);

        List<List<Object>> fileData = generation.getTableValue(file5000);
        long time1 = System.currentTimeMillis();
        service.setRows(fileData);
        long time2 = System.currentTimeMillis();
        service.setHasFinished(true);
        System.out.println("5000 line generation time " + (time2 - time1));

        Assert.assertEquals(1, result.size());

        List<Object> idColumnSL = new ArrayList<>();
        List<Object> firstNameColumnSL = new ArrayList<>();
        List<Object> emailSL = new ArrayList<>();
        List<Object> citySL = new ArrayList<>();
        List<Object> zipSL = new ArrayList<>();

        List<Object> idColumnL = new ArrayList<>();
        List<Object> firstNameColumnL = new ArrayList<>();
        List<Object> emailL = new ArrayList<>();
        List<Object> cityL = new ArrayList<>();
        List<Object> zipL = new ArrayList<>();
        // Initialize the shuffled data
        for (int group = 0; group < result.size(); group++) {
            List<List<Object>> rows = result.poll();
            for (List<Object> row : rows) {
                Object idS = row.get(0);
                Object firstNameS = row.get(1);
                Object emailS = row.get(3);
                Object cityS = row.get(6);
                Object zipS = row.get(7);

                idColumnSL.add(idS);
                firstNameColumnSL.add(firstNameS);
                emailSL.add(emailS);
                citySL.add(cityS);
                zipSL.add(zipS);
            }
        }

        // Initialize the original data set
        for (int i = 0; i < fileData.size(); i++) {
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
            Object nid = idColumnSL.get(i);

            Object oemail = emailL.get(i);
            Object nemail = emailSL.get(i);
            Object oName = firstNameColumnL.get(i);
            // test whether email and id information have all changed
            Assert.assertTrue(!oid.equals(nid) || !oemail.equals(nemail));

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));

        }

    }

    @Test
    @Ignore
    public void testshuffleColumnsData50000() {

        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();

        ShufflingService service = new ShufflingService(columns, allColumns);

        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(100000);

        List<List<Object>> fileData = generation.getTableValue(file50000);
        long time1 = System.currentTimeMillis();
        service.setRows(fileData);
        long time2 = System.currentTimeMillis();
        service.setHasFinished(true);
        System.out.println("50000 line generation time " + (time2 - time1));

        Assert.assertEquals(1, result.size());
        System.out.println("result size " + result.size());
        long time3 = System.currentTimeMillis();
        List<Object> idColumnSL = new ArrayList<>();
        List<Object> firstNameColumnSL = new ArrayList<>();
        List<Object> emailSL = new ArrayList<>();
        List<Object> citySL = new ArrayList<>();
        List<Object> zipSL = new ArrayList<>();

        List<Object> idColumnL = new ArrayList<>();
        List<Object> firstNameColumnL = new ArrayList<>();
        List<Object> emailL = new ArrayList<>();
        List<Object> cityL = new ArrayList<>();
        List<Object> zipL = new ArrayList<>();
        // Initialize the shuffled data
        for (int group = 0; group < result.size(); group++) {
            List<List<Object>> rows = result.poll();
            for (List<Object> row : rows) {
                Object idS = row.get(0);
                Object firstNameS = row.get(1);
                Object emailS = row.get(3);
                Object cityS = row.get(6);
                Object zipS = row.get(7);

                idColumnSL.add(idS);
                firstNameColumnSL.add(firstNameS);
                emailSL.add(emailS);
                citySL.add(cityS);
                zipSL.add(zipS);
            }
        }

        // Initialize the original data set
        for (int i = 0; i < fileData.size(); i++) {
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
            Object nid = idColumnSL.get(i);

            Object oemail = emailL.get(i);
            Object nemail = emailSL.get(i);
            Object oName = firstNameColumnL.get(i);
            // test whether email and id information have all changed
            Assert.assertTrue(!oid.equals(nid) || !oemail.equals(nemail));

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            Assert.assertTrue(oName.equals(sFirstName));

        }
        long time4 = System.currentTimeMillis();
        System.out.println("50000 line generation time " + (time4 - time3));

    }

}
