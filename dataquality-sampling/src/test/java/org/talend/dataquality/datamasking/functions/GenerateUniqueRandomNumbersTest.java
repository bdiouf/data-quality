package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class GenerateUniqueRandomNumbersTest {

    private List<Long> widthsList;

    private List<Long> listToMask;

    private int key = 454594;

    @Test
    public void testgenerateUniqueString() {
        // string to mask
        List<String> strs = new ArrayList<String>(Arrays.asList("U", "KI", "453", "12"));

        // pattern linked to the string
        List<Field> fields = new ArrayList<Field>();
        List<String> enums = new ArrayList<String>(Arrays.asList("O", "P", "G", "U", "M", "S"));
        fields.add(new FieldEnum(enums));
        enums = new ArrayList<String>(Arrays.asList("SF", "KI", "QG", "DU"));
        fields.add(new FieldEnum(enums));
        fields.add(new FieldInterval(0, 500));
        fields.add(new FieldInterval(5, 20));

        StringBuilder result = GenerateUniqueRandomNumbers.generateUniqueString(strs, fields, key);
        assertEquals(result.toString(), "SKI17214");
    }

    @Test
    public void testGetUniqueRandomNumber() {
        widthsList = new ArrayList<Long>();
        listToMask = new ArrayList<Long>();
        widthsList.add(2L);
        listToMask.add(1L);
        widthsList.add(100L);
        listToMask.add(85L);
        widthsList.add(3000L);
        listToMask.add(2681L);

        List<Long> uniqueMaskedNumberList = GenerateUniqueRandomNumbers.getUniqueRandomNumber(listToMask, widthsList, key);
        assertEquals(uniqueMaskedNumberList, new ArrayList<Long>(Arrays.asList(1L, 87L, 2471L)));
    }

    @Test
    public void testOutLimit() {
        List<Long> widthsList = new ArrayList<Long>();
        List<Long> listToMask = new ArrayList<Long>();
        widthsList.add(2L);
        // here the number is greater than the width
        listToMask.add(8L);
        widthsList.add(100L);
        listToMask.add(85L);

        List<Long> uniqueMaskedNumberList = GenerateUniqueRandomNumbers.getUniqueRandomNumber(listToMask, widthsList, key);
        assertEquals(uniqueMaskedNumberList, null);
    }

    @Test
    public void testUnique() {
        Set<List<Long>> uniqueSetTocheck = new HashSet<List<Long>>();
        widthsList = new ArrayList<Long>();
        widthsList.add(2L);
        widthsList.add(20L);
        widthsList.add(12L);
        for (long i = 0; i < widthsList.get(0); i++)
            for (long j = 0; j < widthsList.get(1); j++)
                for (long k = 0; k < widthsList.get(2); k++) {
                    listToMask = new ArrayList<Long>();
                    listToMask.add(i);
                    listToMask.add(j);
                    listToMask.add(k);
                    List<Long> uniqueMaskedNumberList = GenerateUniqueRandomNumbers.getUniqueRandomNumber(listToMask, widthsList,
                            145354);
                    assertFalse(" we found twice the uniqueMaskedNumberList " + uniqueMaskedNumberList,
                            uniqueSetTocheck.contains(uniqueMaskedNumberList));
                    uniqueSetTocheck.add(uniqueMaskedNumberList);
                }
    }
}
