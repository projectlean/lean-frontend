package org.lean.presentation.connector;

public class LeanRow {

    private Object[] row;

    public LeanRow(Object[] row) {
        this.row = row;
    }

    public Object getItem(Integer i) {
        return row[i];
    }

    public Integer getCellCount() {
        return row.length;
    }
}
