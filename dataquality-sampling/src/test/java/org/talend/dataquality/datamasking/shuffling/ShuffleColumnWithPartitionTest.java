package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ShuffleColumnWithPartitionTest {

    private String file1000000 = "Shuffling_test_data_1000000.csv";

    private static List<String> group = new ArrayList<String>();

    private static List<List<String>> numColumn = new ArrayList<List<String>>();

    private static List<String> allColumns = Arrays
            .asList(new String[] { "id", "first_name", "last_name", "email", "gender", "birth", "city", "zip_code", "country" });

    private static GenerateData generator = new GenerateData();

    @BeforeClass
    public static void prepareData() {
        group.add("city");
        group.add("zip_code");
        group.add("country");

        List<String> column1 = Arrays.asList(new String[] { "id", "first_name" });
        List<String> column2 = Arrays.asList(new String[] { "email" });
        numColumn.add(column1);
        numColumn.add(column2);

    }

    /**
     * Tests by the partitions.<br>
     * <ul>
     * 
     * <li>Partition runs well :
     * <ul>
     * <li>id is in the rage of partition</li>
     * <li>email's original index is in the range of partition</li>
     * <li>the city and state do not move</li>
     * </ul>
     * </li>
     * 
     * <li>Integration of data :
     * <ul>
     * <li>id and the first name remain its original correspondence</li>
     * <li>email exists in the list</li>
     * </ul>
     * </li>
     * 
     * <li>Shuffling quality :
     * <ul>
     * <li>the id group (id and the first) and the email, at least one has changed its original position</li>
     * </ul>
     * </li>
     * 
     * </ul>
     * 
     * @throws InterruptedException
     */
    @Test
    @Ignore
    public void testPartitionOneMillion() throws InterruptedException {
        List<List<Object>> fileData = generator.getTableValue(file1000000);
        int partition = 100000;
        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();

        ShufflingService service = new ShufflingService(numColumn, allColumns, group);

        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(partition);

        long time1 = System.currentTimeMillis();
        service.setRows(fileData);
        Thread.sleep(2000);
        long time2 = System.currentTimeMillis();
        service.setHasFinished(true);
        System.out.println("one million line generation time " + (time2 - time1));

        Assert.assertEquals(fileData.size() / partition, result.size());

        for (int i = 0; i < fileData.size() / partition; i++) {
            List<String> emailsO = new ArrayList<String>();
            List<String> fnsO = new ArrayList<String>();
            List<String> citisO = new ArrayList<String>();
            List<String> statesO = new ArrayList<String>();
            List<Integer> idsO = new ArrayList<Integer>();

            List<String> emailsS = new ArrayList<String>();
            List<String> fnsS = new ArrayList<String>();
            List<Integer> idsS = new ArrayList<Integer>();
            List<String> citisS = new ArrayList<String>();
            List<String> statesS = new ArrayList<String>();

            List<List<Object>> subRows = result.poll();
            Assert.assertEquals(partition, subRows.size());

            for (int row = 0; row < subRows.size(); row++) {
                int idS = Integer.parseInt(subRows.get(row).get(0).toString());
                // Partition runs well: id is in the range of partition
                Assert.assertTrue(idS >= (partition * i + 1));
                Assert.assertTrue(idS < (partition * (i + 1) + 1));

                emailsO.add(fileData.get(row + partition * i).get(3).toString());
                fnsO.add(fileData.get(row + partition * i).get(1).toString());
                citisO.add(fileData.get(row + partition * i).get(6).toString());
                statesO.add(fileData.get(row + partition * i).get(7).toString());
                idsO.add(Integer.parseInt(fileData.get(row + partition * i).get(0).toString()));

                idsS.add(idS);
                fnsS.add(subRows.get(row).get(1).toString());
                emailsS.add(subRows.get(row).get(3).toString());
                citisS.add(subRows.get(row).get(6).toString());
                statesS.add(subRows.get(row).get(7).toString());
            }
            Thread.sleep(1000);

            for (int row = 0; row < subRows.size(); row++) {
                if (row % 50000 == 0)
                    System.out.println("i " + i + " row " + row);
                // Partition runs well: email's original index is in the range of partition && Integration of data :
                // email exists in the list
                Assert.assertTrue(emailsO.contains(emailsS.get(row)));

                int ids = idsS.get(row);
                int idO = ids - i * partition - 1;

                // Partition runs well: the city and state do not move
                String cityS = citisS.get(row);
                String cityO = citisO.get(idO);
                Assert.assertEquals(cityO, cityS);

                String stateS = statesS.get(row);
                String stateO = statesO.get(idO);
                Assert.assertEquals(stateO, stateS);

                // Integration of data : id and the first name remain its original correspondence
                ;
                String fnO = fnsO.get(idO);
                Assert.assertEquals(fnO, fnsS.get(row));

                // Shuffling quality : the id group (id and the first) and the email, at least one has changed its
                // original position
                String emailS = emailsS.get(row);
                String emailO = emailsO.get(row);
                Assert.assertTrue(ids != idO || !emailS.equals(emailO));
            }

        }
    }

}
