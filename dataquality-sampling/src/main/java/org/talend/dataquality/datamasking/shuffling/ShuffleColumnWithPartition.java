package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class shuffled the items by the given group. An only key is needed when it initializes. <br>
 * 
 * It remains DOC qzhao class global comment. Detailled comment
 */
public class ShuffleColumnWithPartition extends ShuffleColumn {

    /**
     * 
     * Shuffles the columns by a given group<br>
     * 
     * @param rows input table value
     * @param numColumn 2D list of integer containing the shuffled columns' number
     * @param keysa a list of String
     * @param group a list of column's index as a group
     * @return shuffled rows' data on 2D list
     */
    public void shuffleColumnByGroup(List<List<Object>> rows, List<List<Integer>> numColumn, List<String> keys,
            List<Integer> group) {
        List<Row> rowList = generateRows(rows, group);
        Collections.sort(rowList);

        List<List<Row>> subRows = seperateRowsByGroup(rowList);

        for (List<Row> subRow : subRows) {
            int subRowSize = subRow.size();
            if (subRowSize != 1) {
                List<List<Integer>> allIndexes = new ArrayList<List<Integer>>();
                initOriginalIndex(subRowSize, allIndexes);

                for (int column = 0; column < numColumn.size(); column++) {
                    String key = keys.get(column);
                    if (!key.isEmpty()) {
                        List<Integer> orders = getOrderFromKey(key);
                        int gColumns = orders.size();
                        int gRows = (int) Math.round(subRowSize / gColumns) + (((subRowSize % gColumns) == 0) ? 0 : 1);
                        int[][] grid = new int[gRows][gColumns];
                        seperateArray(subRowSize, gRows, gColumns, grid);

                        ArrayList<Integer> shuffledIndex = shuffleIndexArray(grid, subRowSize, gRows, gColumns, orders,
                                allIndexes);
                        allIndexes.add(shuffledIndex);

                    }
                }
                shuffleOrder(rows, subRow, allIndexes, numColumn);
            }
        }
    }

    /**
     * Separates the list of Row object by the same group.
     * 
     * @param rows the list of rows to be separated
     * @return a list of separated list
     */
    private List<List<Row>> seperateRowsByGroup(List<Row> rows) {
        List<List<Row>> subRows = new ArrayList<List<Row>>();
        int i = 0;
        int j = 1;
        do {
            List<Object> compared = rows.get(i).rGroup;
            do {
                List<Object> comparing = rows.get(j).rGroup;
                for (int k = 0; k < compared.size(); k++) {
                    if (!compared.get(k).equals(comparing.get(k))) {
                        subRows.add(rows.subList(i, j));
                        i = j;
                        break;
                    }
                }
                j++;
                // In the end
                if (j == rows.size()) {
                    subRows.add(rows.subList(i, j));
                    i = j - 1;
                }

            } while (i != (j - 1));

        } while (i != (rows.size() - 1));
        return subRows;
    }

}
