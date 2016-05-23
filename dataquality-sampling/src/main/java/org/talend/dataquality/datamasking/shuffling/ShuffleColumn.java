package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * The class ShuffleColumn defines the basic common methods used in the "shuffling" functions. As with shuffling, this
 * technique is effective only on a large data set.<br>
 * DOC qzhao class global comment. Detailled comment
 */
public class ShuffleColumn {

    protected static final int[] PRIME_NUMBERS = { 104395301, 104395303, 122949823, 122949829, 141650939, 141650963, 160481183,
            160481219, 179424673, 179424691, 198491317, 198491329, 217645177, 217645199 };

    private List<List<Integer>> numColumns = new ArrayList<List<Integer>>();

    private List<Integer> partitionColumns = new ArrayList<Integer>();

    private List<String> allInputColumns = new ArrayList<String>();

    private RandomWrapper randomWrapper = new RandomWrapper();

    /**
     * Constructor without the partition choice
     * 
     * @param shuffledColumns the 2D list of shuffled columns
     * @param allInputColumns the list of all input columns name
     * @throws IllegalArgumentException when the some columns in the shuffledColumns do not exist in the allInputColumns
     */
    public ShuffleColumn(List<List<String>> shuffledColumns, List<String> allInputColumns) throws IllegalArgumentException {
        this.allInputColumns = allInputColumns;
        this.numColumns = getNumColumn(shuffledColumns);
    }

    public ShuffleColumn(List<List<String>> shuffledColumns, List<String> allInputColumns, List<String> partitionColumns)
            throws IllegalArgumentException {
        this.allInputColumns = allInputColumns;
        this.numColumns = getNumColumn(shuffledColumns);
        this.partitionColumns = partitionColumns == null ? null : getPartitionIndex(partitionColumns);
    }

    public void shuffle(List<List<Object>> rows) {
        if (partitionColumns == null || partitionColumns.isEmpty()) {
            shuffleTable(rows, numColumns);
        } else {
            shuffleColumnWithPartition(rows, numColumns, partitionColumns);
        }
    }

    private List<List<Integer>> getNumColumn(List<List<String>> shuffledColumns) {

        for (List<String> subList : shuffledColumns) {
            List<Integer> indexes = new ArrayList<Integer>();
            for (int i = 0; i < subList.size(); i++) {
                int index = allInputColumns.indexOf(subList.get(i));
                if (index != -1) {
                    indexes.add(index);
                } else {
                    throw new IllegalArgumentException(
                            "At least one column name in the shuffled columns does not match the input column names");
                }
            }
            numColumns.add(indexes);
        }
        return numColumns;
    }

    private List<Integer> getPartitionIndex(List<String> partitionColumns) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < partitionColumns.size(); i++) {
            if (allInputColumns.contains(partitionColumns.get(i))) {
                list.add(allInputColumns.indexOf(partitionColumns.get(i)));
            } else {
                throw new IllegalArgumentException(
                        "At least one column name in the partition columns does not match the input column names");
            }
        }
        return list;
    }

    /**
     * This methods shuffles the input 2D list by the give columns number.<br>
     * 
     * The row indexes shift back by a random number between 1 and the input 2D list size one column by one column. Then
     * we find a prime number bigger than the row number.
     * 
     * @param rows
     * @param numColumn
     */
    protected void shuffleTable(List<List<Object>> rowList, List<List<Integer>> numColumn) {
        List<Row> rows = generateRows(rowList, null);
        processShuffleTable(rowList, rows, numColumn);
        rows.clear();

    }

    private void processShuffleTable(List<List<Object>> rowList, List<Row> rows, List<List<Integer>> numColumn) {
        List<Integer> replacements = calculateReplacementInteger(rows.size(), gerPrimeNumber());
        List<Integer> shifts = new ArrayList<Integer>();

        for (int group = 0; group < numColumn.size(); group++) {

            int shift = getShift(shifts, rows.size());
            shifts.add(shift);

            for (int row = 0; row < rows.size(); row++) {
                int resultAddDeplacement = row + shift;
                int replacementIndex = (resultAddDeplacement < rows.size()) ? resultAddDeplacement
                        : resultAddDeplacement - rows.size();
                int replacement = replacements.get(replacementIndex) % rows.size();
                for (int column : numColumn.get(group)) {
                    // rowList.get(row).set(column, rows.get(replacement).rItems.get(column));
                    rowList.get(rows.get(row).rIndex).set(column, rows.get(replacement).rItems.get(column));
                }
            }
        }

    }

    /**
     * Gets the shift of row index.
     * 
     * @param shifts
     * @param integer
     * @return
     */
    private int getShift(List<Integer> shifts, int integer) {
        int shift = 0;
        if (shifts.size() >= integer) {
            return randomWrapper.nextInt(integer);
        }
        do {
            shift = randomWrapper.nextInt(integer);
        } while (shifts.contains(shift));
        return shift;
    }

    /**
     * 
     * Shuffles the columns by a given group<br>
     * 
     * @param rowList input table value
     * @param numColumn 2D list of integer containing the shuffled columns' number
     * @param partition a list of column's index as a group
     * @return shuffled rows' data on 2D list
     */
    protected void shuffleColumnWithPartition(List<List<Object>> rowList, List<List<Integer>> numColumn,
            List<Integer> partition) {
        List<Row> rows = generateRows(rowList, partition);
        Collections.sort(rows);
        List<List<Row>> subRows = seperateRowsByGroup(rows);

        // int primeNumber = gerPrimeNumber();

        for (List<Row> subRow : subRows) {
            int subRowSize = subRow.size();
            if (subRowSize != 1) {
                processShuffleTable(rowList, subRow, numColumn);
                // int shift = numColumn.size() == 1 ? 0 : 1;
                // List<Integer> replacements = calculateReplacementInteger(subRowSize, primeNumber);
                // if (numColumn.size() == 1) {
                // checkReplacementsList(replacements);
                // }
                // for (int group = 0; group < numColumn.size(); group++) {
                //
                // for (int row = 0; row < subRowSize; row++) {
                // int replacement = replacements.get((row + shift * (group + 1)) % subRowSize);
                //
                // for (int column : numColumn.get(group)) {
                // rowList.get(subRow.get(row).rIndex).set(column, subRow.get(replacement).rItems.get(column));
                // }
                // }
                // }

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
                if (j == rows.size()) {
                    subRows.add(rows.subList(i, j));
                    i = j - 1;
                }

            } while (i != (j - 1));

        } while (i != (rows.size() - 1));
        return subRows;
    }

    public void setRandomSeed(long seed) {
        this.randomWrapper.setSeed(seed);
    }

    /**
     * Checks whether a position does not change after the modulo calculation
     * 
     * @param replacements
     */
    protected void checkReplacementsList(List<Integer> replacements) {
        for (int i = 0; i < replacements.size(); i++) {
            if (i == replacements.get(i)) {
                if (i < replacements.size() - 1) {
                    int temp = replacements.get(i);
                    replacements.set(i, replacements.get(i + 1));
                    replacements.set(i + 1, temp);
                } else {
                    int temp = replacements.get(i);
                    replacements.set(i, replacements.get(i - 1));
                    replacements.set(i - 1, temp);
                }
            }
        }

    }

    protected int gerPrimeNumber() {
        return PRIME_NUMBERS[randomWrapper.nextInt(PRIME_NUMBERS.length)];
    }

    /**
     * This methods calculates the replaced index.<br>
     * The replaced index is calculated by the equation (original_index * prime_number) modulo (input_size)<br>
     * 
     * @param size the input size
     * @param prime the prime number
     * @return
     */
    protected List<Integer> calculateReplacementInteger(int size, int prime) {
        List<Integer> list = new ArrayList<Integer>();
        for (long i = 0; i < size; i++) {
            list.add((int) (((i + 1) * prime) % size));
        }
        return list;
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
            List<Object> partition = new ArrayList<Object>();
            if (columnsGroup != null) {
                for (int cIndex : columnsGroup) {
                    partition.add(subInput.get(cIndex));
                }
            }
            rows.add(new Row(rIndex, subInput, partition));
            rIndex++;
        }
        return rows;
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
