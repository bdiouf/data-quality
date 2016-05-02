package org.talend.dataquality.datamasking.shuffling;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StandardExectionTimeTest {

    private static List<List<List<Integer>>> getColumnGroue() {
        List<Integer> one = Arrays.asList(new Integer[] { 0 });
        List<Integer> two = Arrays.asList(new Integer[] { 1 });
        List<Integer> three = Arrays.asList(new Integer[] { 2 });
        List<Integer> four = Arrays.asList(new Integer[] { 3 });
        // List<Integer> five = Arrays.asList(new Integer[] { 4 });
        // List<Integer> six = Arrays.asList(new Integer[] { 5 });

        List<List<Integer>> list1 = new ArrayList<>();
        list1.add(one);
        List<List<Integer>> list2 = new ArrayList<>();
        list2.add(one);
        list2.add(two);
        List<List<Integer>> list3 = new ArrayList<>();
        list3.add(one);
        list3.add(two);
        list3.add(three);
        List<List<Integer>> list4 = new ArrayList<>();
        list4.add(one);
        list4.add(two);
        list4.add(three);
        list4.add(four);
        // List<List<Integer>> list5 = new ArrayList<>();
        // list5.add(one);
        // list5.add(two);
        // list5.add(three);
        // list5.add(four);
        // list5.add(five);
        // List<List<Integer>> list6 = new ArrayList<>();
        // list6.add(one);
        // list6.add(two);
        // list6.add(three);
        // list6.add(four);
        // list6.add(five);
        // list6.add(six);

        List<List<List<Integer>>> list = new ArrayList<List<List<Integer>>>();
        list.add(list1);
        list.add(list2);
        list.add(list3);
        list.add(list4);
        // list.add(list5);
        // list.add(list6);
        return list;
    }

    private static List<List<String>> getKeys() {
        List<String> key1 = Arrays.asList(new String[] { "talend" });
        List<String> key2 = Arrays.asList(new String[] { "talend", "computer" });
        List<String> key3 = Arrays.asList(new String[] { "talend", "computer", "computer" });
        List<String> key4 = Arrays.asList(new String[] { "talend", "computer", "computer", "computer" });
        // List<String> key5 = Arrays.asList(new String[] { "talend", "computer", "computer", "computer", "computer" });
        // List<String> key6 = Arrays.asList(new String[] { "talend", "computer", "computer", "computer", "computer",
        // "computer" });
        List<List<String>> keys = new ArrayList<List<String>>();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        keys.add(key4);
        // keys.add(key5);
        // keys.add(key6);
        return keys;
    }

    private static List<List<Long>> initResultList() {
        List<List<Long>> results = new ArrayList<>(4);
        results.add(new ArrayList<Long>());
        results.add(new ArrayList<Long>());
        results.add(new ArrayList<Long>());
        results.add(new ArrayList<Long>());
        // results.add(new ArrayList<Long>());
        // results.add(new ArrayList<Long>());
        return results;
    }

    public static void main(String[] args) {
        String file = "Shuffling_test_data" + "_5000000.csv";
        ShuffleColumn shuffleColumn = new ShuffleColumn();
        List<List<Object>> fileData = new GenerateData().getTableValueBySimbol(file);
        System.out.println("end");
        List<List<List<Integer>>> list = getColumnGroue();
        List<List<String>> keys = getKeys();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("/home/qzhao/talend_data_anony/TDQ-11904-shuffling/execution_time_notes-1.txt");
            writer.println("Rows' number | column's group | execution time");
            for (int end = 10000; end <= fileData.size(); end += 10000) {
                List<List<Object>> subFileData = fileData.subList(0, end - 1);
                for (int c = 0; c < list.size(); c++) {
                    List<List<Integer>> columns = list.get(c);
                    List<String> key = keys.get(c);
                    long temp = 0;
                    long startTime = System.currentTimeMillis();
                    shuffleColumn.shuffleColumnsData(subFileData, columns, key);
                    long endTime = System.currentTimeMillis();
                    temp += (endTime - startTime);
                    writer.println(end + "| " + c + " | " + temp);
                    writer.flush();
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }

        // SwingUtilities.invokeLater(new Runnable() {
        //
        // public void run() {
        //
        // for (int i = 0; i < results.size(); i++) {
        // GraphPanel mainPanel = new GraphPanel(results.get(i));
        // mainPanel.setPreferredSize(new Dimension(800, 600));
        // JFrame frame = new JFrame("DrawGraph");
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.getContentPane().add(mainPanel);
        // frame.pack();
        // frame.setLocationRelativeTo(null);
        // frame.setVisible(true);
        // }
        // }
        // });

    }

}
