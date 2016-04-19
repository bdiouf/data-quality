package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.List;

/**
 * This class offers the funcitons to do the "group shuffling" which means a group of columns are shuffled together.
 * <br>
 * DOC qzhao class global comment. Detailled comment
 */
public class ShuffleMultipleColumns extends ShuffleColumn {

    public ShuffleMultipleColumns(String key) {
        super(key);
    }

    public List<List<String>> shuffleColumnsData(List<List<String>> input) {
        List<List<String>> output = new ArrayList<List<String>>();

        List<Integer> outputOrder = getKeyOrder();

        // get the longest length from the input
        int inputSize = 0;
        for (List<String> list : input) {
            inputSize = (list.size() > inputSize) ? list.size() : inputSize;
        }
        int columns = outputOrder.size();
        int rows = (int) Math.round(inputSize / columns) + (((inputSize % columns) == 0) ? 0 : 1);
        int[][] grid = new int[rows][columns];
        seperateArray(inputSize, rows, columns, grid);

        ArrayList<Integer> shuffledIndex = shuffleIndexArray(grid, inputSize, rows, columns, outputOrder);
        for (int i = 0; i < input.size(); i++) {
            List<String> iColumn = input.get(i);
            List<String> iSublist = new ArrayList<String>();
            for (int index : shuffledIndex) {
                // this is a column
                iSublist.add(iColumn.get(index));
            }
            output.add(iSublist);
        }
        return output;
    }

}
