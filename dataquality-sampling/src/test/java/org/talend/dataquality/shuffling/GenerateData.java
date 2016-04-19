package org.talend.dataquality.shuffling;

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

    protected List<String> getData(String column) throws URISyntaxException {
        String pathString = GenerateData.class.getResource("data/Shuffling_test_data.csv").toURI().getPath();

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

        return null;
    }

    protected List<List<String>> getMultipalData(String[] columns) throws URISyntaxException {
        List<List<String>> result = new ArrayList<List<String>>();
        for (String column : columns) {
            result.add(getData(column));
        }
        return result;
    }

}
