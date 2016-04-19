package org.talend.dataquality.shuffling;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.datamasking.shuffling.ShuffleColumn;

public class ShuffleColumnTest {

    private static List<Integer> data = new ArrayList<Integer>();

    private ShuffleColumn shuffleColumn1 = new ShuffleColumn("Apple");

    private ShuffleColumn shuffleColumn2 = new ShuffleColumn("Talend");

    private ShuffleColumn shuffleColumn3 = new ShuffleColumn("abcde");

    @BeforeClass
    public static void generateData() {
        for (int i = 0; i < 14; i++) {
            data.add(i);
        }
    }

    @Test
    public void testGetKeyOrder1() {
        List<Integer> keyOrder = shuffleColumn1.getKeyOrder();
        int[] expected = new int[] { 0, 3, 4, 2, 1 };

        for (int i = 0; i < keyOrder.size(); i++) {
            Assert.assertEquals(expected[i], keyOrder.get(i).intValue());
        }

    }

    @Test
    public void testGetKeyOrder2() {
        List<Integer> keyOrder = shuffleColumn2.getKeyOrder();
        int[] expected = new int[] { 0, 1, 4, 3, 5, 2 };

        for (int i = 0; i < keyOrder.size(); i++) {
            Assert.assertEquals(expected[i], keyOrder.get(i).intValue());
        }

    }

    @Test
    public void testShuffledIndexArray1() {
        int[][] grid = new int[3][5];
        int i = 0;
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 5; column++) {
                grid[row][column] = i++;
            }
        }

        for (int column = 0; column < 4; column++) {
            grid[2][column] = i++;
        }
        List<Integer> keyOrder = shuffleColumn1.getKeyOrder();
        List<Integer> result = shuffleColumn1.shuffleIndexArray(grid, 12, 3, 5, keyOrder);
        for (int j = 0; j < result.size(); j++) {
            Assert.assertTrue(data.get(j) != result.get(j));
        }

    }

    @Test
    public void testShuffledIndexArray2() {
        int[][] grid = new int[3][5];
        int i = 0;
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 5; column++) {
                grid[row][column] = i++;
            }
        }

        for (int column = 0; column < 4; column++) {
            grid[2][column] = i++;
        }
        List<Integer> keyOrder = shuffleColumn3.getKeyOrder();
        List<Integer> result = shuffleColumn1.shuffleIndexArray(grid, 14, 3, 5, keyOrder);
        for (int j = 0; j < result.size(); j++) {
            Assert.assertTrue(data.get(j) != result.get(j));
        }

    }

}
