package org.talend.dataquality.datamasking.shuffling;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The class ShuffleColumn defines the basic common methods used in the "shuffling" functions. As with shuffling, this
 * technique is effective only on a large data set.<br>
 * DOC qzhao class global comment. Detailled comment
 */
public class ShuffleColumn {

    public ShuffleColumn() {
        super();
    }

    /**
     * This method shuffles the input 2D list by the given columns number. Each column has its own shuffled keys. If the
     * key is empty, this column will not be shuffled.<br>
     * 
     * @param rows 2D list including all the rows' data to be shuffled
     * @param numColumn 2D list of integer containing the shuffled columns' number
     * @param keys a list of String
     */
    public void shuffleColumnsData(List<List<Object>> rows, List<List<Integer>> numColumn, List<String> keys) {
        int sizeRow = rows.size();
        List<Row> rowList = generateRows(rows, null);

        List<List<Integer>> allIndexes = new ArrayList<List<Integer>>();
        initOriginalIndex(rows.size(), allIndexes);

        for (int column = 0; column < numColumn.size(); column++) {
            String key = keys.get(column);
            if (!key.isEmpty()) {
                // Shuffles the rows when the key is not empty
                List<Integer> orders = getOrderFromKey(key);

                // Initializes a table with rows' index
                int gColumns = orders.size();
                int gRows = (int) Math.round(sizeRow / gColumns) + (((sizeRow % gColumns) == 0) ? 0 : 1);
                int[][] grid = new int[gRows][gColumns];
                seperateArray(sizeRow, gRows, gColumns, grid);

                // Shuffles the index by the given order
                ArrayList<Integer> shuffledIndex = shuffleIndexArray(grid, sizeRow, gRows, gColumns, orders, allIndexes);
                allIndexes.add(shuffledIndex);
            }
        }

        shuffleOrder(rows, rowList, allIndexes, numColumn);

    }

    protected void shuffleOrder(List<List<Object>> rows, List<Row> rowList, List<List<Integer>> allIndex,
            List<List<Integer>> numColumn) {
        List<Integer> inputOrder = allIndex.get(0);
        for (int i = 1; i < allIndex.size(); i++) {
            List<Integer> columnGroup = numColumn.get(i - 1);
            List<Integer> outputOrder = allIndex.get(i);
            for (int j = 0; j < inputOrder.size(); j++) {
                int oRow = rowList.get(j).rIndex;
                for (int column : columnGroup) {
                    rows.get(oRow).set(column, rowList.get(outputOrder.get(j)).getItem(column));
                }
            }
        }
    }

    /**
     * Generates a list of Row with input row values and saves the information of group.
     * 
     * @param input the 2D arrays to be cloned
     * @param columnsGroup a list of grouped columns' indexes
     * @return a list of Row object
     */
    protected List<Row> generateRows(List<List<Object>> input, List<Integer> columnsGroup) {
        List<Row> rows = new ArrayList<Row>();
        int rIndex = 0;
        for (List<Object> subInput : input) {
            List<Object> rGroup = new ArrayList<Object>();
            if (columnsGroup != null) {
                for (int cIndex : columnsGroup) {
                    rGroup.add(subInput.get(cIndex));
                }
            }
            rows.add(new Row(rIndex, subInput, rGroup));
            rIndex++;
        }

        return rows;
    }

    /**
     * Initializes the original index list from 0 to the (talbe size - 1).
     * 
     * @param sizeRows rows' size
     * @param aIndexes 2D list of index who should be avoided
     */
    protected void initOriginalIndex(int sizeRows, List<List<Integer>> aIndexes) {
        List<Integer> oIndexes = new ArrayList<Integer>();
        for (int i = 0; i < sizeRows; i++) {
            oIndexes.add(i);
        }
        aIndexes.add(oIndexes);
    }

    /**
     * Implements the "transposition cipher". The input grid has a sequence of number which indicates the rows' index.
     * It will be read by the order defines by a list vertically. If some index "by chance" has it original position or
     * other shuffled result index, some perturbation should be added.
     * 
     * @param grid a 2D dimension array with rows' index
     * @param total the total items amount in grid
     * @param rows the row number of grid
     * @param columns the column number of grid
     * @param orders the output order
     * @param allIndexes all the index that should be avoided
     * @return a list of shuffled index
     */
    protected ArrayList<Integer> shuffleIndexArray(int[][] grid, int total, int rows, int columns, List<Integer> orders,
            List<List<Integer>> allIndexes) {
        ArrayList<Integer> output = new ArrayList<Integer>();

        if (grid.length != rows || grid[0].length != columns) {
            throw new IllegalArgumentException("input 2D dimension arrays does not match other parameters");
        }

        int rest = total % columns;
        for (int i = 0; i < orders.size(); i++) {
            int column = orders.indexOf(i);
            int rowsInColumn = 0;
            if (rest == 0 || column < rest) {
                rowsInColumn = rows;
            } else {
                rowsInColumn = rows - 1;
            }

            for (int j = 0; j < rowsInColumn; j++) {
                output.add(grid[j][column]);
            }
        }

        scrambleOutputArray(output, allIndexes);

        return output;
    }

    /**
     * Changes the positions who remain its original position or other shuffled items. Compares the items around it
     * (item A) find the nearest item (item B) whose position is different event after changing with the item A.
     * 
     * @param output the output result
     * @param allIndexes all the indexes should be avoided
     */
    protected void scrambleOutputArray(List<Integer> output, List<List<Integer>> allIndexes) {
        int pointer = 0;

        do {
            List<Integer> cmpIndex = extractValuesFrom2DArraysByPosition(pointer, allIndexes); // compared list of index
            int dPointer = getNearestDifferentPosition(pointer, cmpIndex, output);

            if (dPointer > pointer) {
                changePosition(pointer, dPointer, output);

            } else if (dPointer == -1) {
                dPointer = pointer - 1;
                do {
                    dPointer = getNearestDifferentPositionUpward(dPointer, cmpIndex, output);
                    if (dPointer == -1) {
                        break;
                    }
                } while (!extractValuesFrom2DArraysByPosition(dPointer, allIndexes).contains(output.get(pointer)));
                changePosition(pointer, dPointer, output);
            }
            pointer++;
        } while (pointer < output.size());

    }

    /**
     * Changes the value at two different positions.
     * 
     * @param fPosition first position
     * @param sPosition second position
     * @param list list to change position value
     */
    private void changePosition(int fPosition, int sPosition, List<Integer> list) {
        if (fPosition >= 0 && sPosition >= 0) {
            int temp = list.get(fPosition);
            list.set(fPosition, list.get(sPosition));
            list.set(sPosition, temp);
        }
    }

    /**
     * Extracts all the values in a list from a two dimensions arrays by the given position
     * 
     * @param position the given position
     * @param twoDArrays two dimensions arrays
     * @return a list of value from twoDArrays at the given position
     */
    private List<Integer> extractValuesFrom2DArraysByPosition(int position, List<List<Integer>> twoDArrays) {
        List<Integer> list = new ArrayList<Integer>(); // compared list of index
        for (List<Integer> aIndexes : twoDArrays) {
            list.add(aIndexes.get(position));
        }
        return list;
    }

    /**
     * Get the nearest position which is has different number.<br>
     * <ul>
     * <li>return input parameter position if the avoided list does not contain the value</li>
     * <li>return the nearest downward position whose value cannot be found in the avoided list</li>
     * <li>return -1 when no appropriate position downward</li>
     * </ul>
     * 
     * @param position given position
     * @param avoided a list of number to be avoided
     * @param output all the possible output number
     * @return a new position
     */
    private int getNearestDifferentPosition(int position, List<Integer> avoided, List<Integer> output) {
        while (position != output.size() && avoided.contains(output.get(position))) {
            position++;
        }

        return position == output.size() ? -1 : position;
    }

    /**
     * Get the nearest upward position which is has different number. This method implements a search upward which meas
     * a downward search has been implemented, so if we cannot find some upward position fulfill the require, return -1.
     * 
     * @param position given position
     * @param avoided a list of number to be avoided
     * @param output all the possible output number
     * @return return the nearest upward position whose value cannot be found in the avoided list
     */
    private int getNearestDifferentPositionUpward(int position, List<Integer> avoided, List<Integer> output) {
        while (position >= 0 && avoided.contains(output.get(position))) {
            position--;
        }
        return position;
    }

    /**
     * Gets the character order from a string by its ASCII
     * 
     * @param key a string to be ordered
     * @return a list of integer with order
     */
    protected List<Integer> getOrderFromKey(String key) {
        List<Character> keyCharacters = asList(key);
        List<Integer> keyASCIIs = getASCIIValue(keyCharacters);
        List<Integer> keySortOrder = getASCIIValue(keyCharacters);
        List<Integer> keyOrder = new LinkedList<Integer>(keySortOrder);
        Collections.sort(keySortOrder);

        int i = 0;

        do {
            int assic = keySortOrder.get(i);
            if (keySortOrder.indexOf(assic) == keySortOrder.lastIndexOf(assic)) {
                keyOrder.set(keyASCIIs.indexOf(assic), i);
                i++;
            } else {
                for (int j = keyASCIIs.indexOf(assic); j <= keyASCIIs.lastIndexOf(assic); j++) {
                    if (keyASCIIs.get(j) == assic) {
                        keyOrder.set(j, i);
                        i++;
                    }
                }
            }

        } while (i < keyCharacters.size());

        return keyOrder;
    }

    protected void seperateArray(int length, int rows, int columns, int[][] grid) {
        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = j + i * columns;
            }
        }

        int rest = (length % columns);
        rest = (rest == 0) ? columns : rest;
        for (int i = 0; i < rest; i++) {
            grid[rows - 1][i] = i + (rows - 1) * columns;
        }
    }

    /**
     * Gets the ASSIC values from a list of characters
     * 
     * @param keyCharacters the list of characters
     * @return the list of all the ASSIC values with case sensitive
     */
    private List<Integer> getASCIIValue(List<Character> keyCharacters) {
        List<Integer> keyASCIIs = new ArrayList<Integer>();
        for (Character c : keyCharacters) {
            keyASCIIs.add((int) c);
        }
        return keyASCIIs;
    }

    /**
     * Cover a string to list
     * 
     * @param string input string
     * @return a list of character from the string
     */
    private List<Character> asList(final String string) {
        return new AbstractList<Character>() {

            @Override
            public Character get(int index) {
                return string.charAt(index);
            }

            @Override
            public int size() {
                return string.length();
            }
        };
    }

    /**
     * This class abstracts a Row with its index and the group items. It implements {@link Comparable} interface to
     * compare the value by the group items.<br>
     * DOC qzhao ShuffleColumnWithPartition class global comment. Detailled comment
     */
    class Row implements Comparable<Row> {

        int rIndex;

        List<Object> rGroup = new ArrayList<Object>();

        List<Object> rItems = new ArrayList<Object>();

        public Row(int rIndex, List<Object> rItems, List<Object> rGroup) {
            super();
            this.rIndex = rIndex;
            for (Object o : rItems) {
                this.rItems.add(o);
            }

            if (rGroup == null) {
                this.rGroup = null;
            } else {
                for (Object o : rGroup) {
                    this.rGroup.add(o);
                }
            }

        }

        @Override
        public String toString() {
            return "( " + rIndex + " " + " rItems " + rItems + " rGroup " + rGroup + " )";
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Row)) {
                return false;
            }
            Row r = (Row) o;
            if (r.rIndex != rIndex || r.rGroup.size() != rGroup.size()) {
                return false;
            }
            boolean equal = true;
            for (int i = 0; i < rGroup.size(); i++) {
                if (!rGroup.get(i).equals(r.rGroup.get(i))) {
                    equal = false;
                }
            }

            return equal;
        }

        @Override
        public int compareTo(Row r) {
            int max = (rGroup.size() <= r.rGroup.size()) ? rGroup.size() : r.rGroup.size();
            int cmp = -1;
            for (int i = 0; i < max; i++) {
                cmp = ((String) rGroup.get(i)).compareTo(((String) r.rGroup.get(i)));
                if (cmp != 0) {
                    return cmp;
                }
            }

            return cmp;
        }

        Object getItem(int index) {
            return rItems.get(index);
        }

    }

}
