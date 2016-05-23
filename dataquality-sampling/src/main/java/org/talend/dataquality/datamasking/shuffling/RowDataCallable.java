package org.talend.dataquality.datamasking.shuffling;

import java.util.List;
import java.util.concurrent.Callable;

public class RowDataCallable<V> implements Callable<List<List<Object>>> {

    protected List<List<Object>> rows;

    protected ShuffleColumn shuffleColumn;

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
