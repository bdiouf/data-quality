package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test {

    @org.junit.Test
    public void testCity() {
        List<String> list = new ArrayList<String>();
        list.add("Rouen");
        list.add("Nice");
        list.add("Saint-Étienne");
        list.add("Thionville");
        list.add("Valence");
        list.add("Évreux");
        list.add("Champigny-sur-Marne");
        list.add("Nancy");
        list.add("Nancy");
        list.add("Bordeaux");
        list.add("Saint-Ouen");
        list.add("Wasquehal");
        list.add("Dijon");
        System.out.println(list);
        Collections.sort(list);
        System.out.println(list);
    }

}
