package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the method when given one only column<br>
 * DOC qzhao class global comment. Detailled comment
 */
public class ShuffleOneColumne extends ShuffleColumn {

    public ShuffleOneColumne(String key) {
        super(key);
    }

    /**
     * Shuffles the input data.
     * 
     * @param input a list of input data
     * @return the shuffled data
     */
    public List<String> shuffleColumnData(List<String> input) {
        List<Integer> outputOrder = getKeyOrder();

        int columns = outputOrder.size();
        int rows = (int) Math.round(input.size() / columns) + (((input.size() % columns) == 0) ? 0 : 1);

        int[][] grid = new int[rows][columns];
        seperateArray(input.size(), rows, columns, grid);

        ArrayList<Integer> shuffledIndex = shuffleIndexArray(grid, input.size(), rows, columns, outputOrder);
        List<String> result = new ArrayList<String>();
        for (int index : shuffledIndex) {
            result.add(input.get(index));
        }
        return result;
    }

}
