package org.talend.dataquality.datamasking.shuffling;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class GenerateDataTest {

    @Test
    public void testGetAllTable() {
        List<List<Object>> table = new GenerateData().getTableValue();
        // System.out.println(new GenerateData().getData("email").size());
        Assert.assertEquals(1000, table.size());
    }

    @Test
    public void testColumnIndex() {
        int id = new GenerateData().getColumnIndex("id");
        Assert.assertEquals(0, id);

        int email = new GenerateData().getColumnIndex("email");
        Assert.assertEquals(3, email);
    }
}
