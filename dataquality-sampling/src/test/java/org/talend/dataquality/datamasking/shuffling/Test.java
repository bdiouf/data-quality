package org.talend.dataquality.datamasking.shuffling;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("Column", "id");
        map1.put("VALUE", "1");

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("Column", "first_name");
        map2.put("VALUE", "1");

        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("Column", "last_name");
        map3.put("VALUE", "");

        Map<String, String> map4 = new HashMap<String, String>();
        map4.put("Column", "email");
        map4.put("VALUE", "2");

        List<Map<String, String>> modifTableList = Arrays.asList(map1, map2, map3, map4);
        String tmpResultPath = System.getProperty("java.io.tmpdir");
        System.out.println(tmpResultPath);
    }

}
