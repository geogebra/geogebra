package org.geogebra.keyboard.base.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.Row;

public class KeyboardModelImpl implements KeyboardModel {

    private List<Row> rows = new ArrayList<>();

	/**
	 * @return new row
	 */
    public RowImpl nextRow() {
        RowImpl row = new RowImpl();
        rows.add(row);
        return row;
    }

    @Override
    public List<Row> getRows() {
        return rows;
    }
}
