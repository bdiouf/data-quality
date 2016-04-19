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

    private String key;

    public ShuffleColumn(String key) {
        super();
        this.key = key;
    }

    /**
     * Gets the output order from the input key
     * 
     * @return an array of integer
     */
    public List<Integer> getKeyOrder() {
        List<Character> keyCharacters = asList(key);
        List<Integer> keyASCIIs = getASCIIValue(keyCharacters);
        List<Integer> keySortOrder = getASCIIValue(keyCharacters);
        List<Integer> outputOrder = new LinkedList<Integer>(keySortOrder);
        Collections.sort(keySortOrder);

        int i = 0;

        do {
            int assic = keySortOrder.get(i);
            if (keySortOrder.indexOf(assic) == keySortOrder.lastIndexOf(assic)) {
                outputOrder.set(keyASCIIs.indexOf(assic), i);
                i++;
            } else {
                for (int j = keyASCIIs.indexOf(assic); j <= keyASCIIs.lastIndexOf(assic); j++) {
                    if (keyASCIIs.get(j) == assic) {
                        outputOrder.set(j, i);
                        i++;
                    }
                }
            }

        } while (i < keyCharacters.size());

        return outputOrder;
    }

    /**
     * Implements the "transposition cipher". The input grid has a sequence of number which indicates the rows' index.
     * It will be read by the order defines by a list vertically. If some index "by chance" has it original position,
     * some perturbation should be added.
     * 
     * @param grid a 2D dimension array with rows' index
     * @param total the total items amount in grid
     * @param rows the row number of grid
     * @param columns the column number of grid
     * @param orders the outpout order
     * @return a list of shuffled index
     */
    public ArrayList<Integer> shuffleIndexArray(int[][] grid, int total, int rows, int columns, List<Integer> orders) {
        ArrayList<Integer> output = new ArrayList<Integer>();
        List<Integer> needPerturbation = new ArrayList<Integer>();

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
                if (grid[j][column] == (output.size() - 1)) {
                    needPerturbation.add(output.size() - 1);
                }
            }
        }
        if (!needPerturbation.isEmpty()) {
            pertubeOutputArray(output, needPerturbation);
        }

        return output;
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
     * Changes the positions who remain its original position. Compares the items around it (item A) find the nearest
     * item (item B) whose position is different event after changing with the item A.
     * 
     * @param output the output result
     * @param needPerturbation the item needs to be change the position once
     */
    private void pertubeOutputArray(ArrayList<Integer> output, List<Integer> needPerturbation) {
        for (int waitingPerturbation : needPerturbation) {
            if (waitingPerturbation == 0) {
                output.set(waitingPerturbation + 1, waitingPerturbation);
                output.set(waitingPerturbation, waitingPerturbation + 1);
            } else {
                output.set(waitingPerturbation - 1, waitingPerturbation);
                output.set(waitingPerturbation, waitingPerturbation - 1);
            }
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

}
