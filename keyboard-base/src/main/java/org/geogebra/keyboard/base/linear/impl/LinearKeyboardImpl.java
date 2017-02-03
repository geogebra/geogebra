package org.geogebra.keyboard.base.linear.impl;

import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.Row;
import org.geogebra.keyboard.base.linear.impl.RowImpl;

import java.util.ArrayList;
import java.util.List;

public class LinearKeyboardImpl implements LinearKeyboard {

    private List<Row> rows = new ArrayList<>();

    public RowImpl nextRow(float rowWeightSum) {
        RowImpl row = new RowImpl(rowWeightSum);
        rows.add(row);
        return row;
    }

    @Override
    public List<Row> getRows() {
        return rows;
    }
}
