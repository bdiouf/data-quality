package org.talend.dataquality.shuffling;

import java.net.URISyntaxException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.datamasking.shuffling.ShuffleOneColumne;

public class ShuffleOneColumnTest {

    private static List<String> data;

    private ShuffleOneColumne shuffle = new ShuffleOneColumne("talend");

    private ShuffleOneColumne shuffleSpecial = new ShuffleOneColumne("abcdefghijk");

    private ShuffleOneColumne shuffleSpecial1 = new ShuffleOneColumne("abcde");

    @BeforeClass
    public static void prepareData() {
        try {
            data = new GenerateData().getData("email");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShuffleOneColumne() {
        List<String> result = shuffle.shuffleColumnData(data);
        Assert.assertEquals(data.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            Assert.assertTrue(!result.get(i).equals(data.get(i)));
        }
    }

    @Test
    public void testShuffleOneColumne1() {
        List<String> result = shuffleSpecial.shuffleColumnData(data);
        Assert.assertEquals(data.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            Assert.assertTrue(!result.get(i).equals(data.get(i)));
        }
    }

    @Test
    public void testShuffleOneColumne2() {
        List<String> result = shuffleSpecial1.shuffleColumnData(data);
        Assert.assertEquals(data.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            Assert.assertTrue(!result.get(i).equals(data.get(i)));
        }
    }

}
