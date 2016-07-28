package org.talend.dataquality.datamasking.shuffling;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class implements the Callable interface which returns a shuffled table.
 */
public class RowDataCallable<V> implements Callable<List<List<Object>>> {

    protected List<List<Object>> rows;

    protected ShuffleColumn shuffleColumn;

    /**
     * Constructor
     * 
     * @param shuffleColumn ShuffleColumn object
     * @param rows a table
     */
    public RowDataCallable(ShuffleColumn shuffleColumn, List<List<Object>> rows) {
        super();
        this.shuffleColumn = shuffleColumn;
        this.rows = rows;
    }

    @Override
    public List<List<Object>> call() throws Exception {
        shuffleColumn.shuffle(rows);
        return rows;
    }

}
