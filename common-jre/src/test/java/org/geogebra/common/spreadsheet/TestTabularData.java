package org.geogebra.common.spreadsheet;

import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularDataChangeListener;

public class TestTabularData implements TabularData {

	Object[][] data = new String[100][100];

	@Override
	public void reset(int rows, int columns) {

	}

	@Override
	public int numberOfRows() {
		return data.length;
	}

	@Override
	public int numberOfColumns() {
		return data[0].length;
	}

	@Override
	public void appendRows(int rows) {

	}

	@Override
	public void insertRowAt(int row) {

	}

	@Override
	public void deleteRowAt(int row) {

	}

	@Override
	public void appendColumns(int columns) {

	}

	@Override
	public void insertColumnAt(int column) {

	}

	@Override
	public void deleteColumnAt(int column) {

	}

	@Override
	public void setContent(int row, int column, Object content) {
		data[row][column] = content;
	}

	@Override
	public Object contentAt(int row, int column) {
		return data[row][column];
	}

	@Override
	public String getColumnName(int column) {
		return "col" + column;
	}

	@Override
	public void addChangeListener(TabularDataChangeListener listener) {

	}
}
