package org.talend.dataquality.datamasking.shuffling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GenerateData {

    // "Shuffling_test_data.csv"
    protected List<List<Object>> getTableValue(String file) {

        String pathString = "";
        try {
            pathString = GenerateData.class.getResource(file).toURI().getPath();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        List<List<Object>> result = new ArrayList<List<Object>>();

        try {
            br = new BufferedReader(new FileReader(pathString));
            line = br.readLine(); // read the column title
            while ((line = br.readLine()) != null) {
                List<Object> row = new ArrayList<Object>();
                Object[] items = line.split(cvsSplitBy);
                for (Object item : items) {
                    row.add(item);
                }
                result.add(row);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    protected List<List<Object>> getTableValueBySimbol(String file) {

        String pathString = "";
        try {
            pathString = GenerateData.class.getResource(file).toURI().getPath();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        List<List<Object>> result = new ArrayList<List<Object>>();

        try {
            br = new BufferedReader(new FileReader(pathString));
            line = br.readLine(); // read the column title
            while ((line = br.readLine()) != null) {
                List<Object> row = new ArrayList<Object>();
                Object[] items = line.split(cvsSplitBy);
                for (Object item : items) {
                    row.add(item);
                }
                result.add(row);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    protected int getColumnIndex(String column) {
        String pathString = "";
        try {
            pathString = GenerateData.class.getResource("Shuffling_test_data.csv").toURI().getPath();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int position = 0;

        try {
            br = new BufferedReader(new FileReader(pathString));

            if ((line = br.readLine()) != null) {
                List<String> attributes = Arrays.asList(line.split(cvsSplitBy));
                position = attributes.indexOf(column);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return position;
    }

    protected List<String> getData(String file, String column) throws URISyntaxException {
        String pathString = GenerateData.class.getResource(file).toURI().getPath();

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int position = 0;

        List<String> result = new LinkedList<String>();

        try {
            br = new BufferedReader(new FileReader(pathString));

            if ((line = br.readLine()) != null) {
                List<String> attributes = Arrays.asList(line.split(","));
                position = attributes.indexOf(column);
            }

            while ((line = br.readLine()) != null) {
                String[] items = line.split(cvsSplitBy);
                result.add(items[position]);
            }
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected List<List<String>> getMultipleData(String file, String[] columns) throws URISyntaxException {
        List<List<String>> result = new ArrayList<List<String>>();
        for (String column : columns) {
            result.add(getData(file, column));
        }
        return result;
    }

}
