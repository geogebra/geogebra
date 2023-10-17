package org.geogebra.common.spreadsheet;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.spreadsheet.core.PasteInterface;
import org.geogebra.common.spreadsheet.core.PasteText;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularDataChangeListener;
import org.geogebra.common.spreadsheet.kernel.HasPaste;

public class TestTabularData implements TabularData<Object>, HasPaste {

	List<List<Object>> data = new ArrayList<>();

	/**
	 * Simple tabular data (initial size 100 x 100).
	 */
	public TestTabularData() {
		for (int i = 0; i < 100; i++) {
			data.add(buildRow(100));
		}
	}

	private List<Object> buildRow(int i) {
		ArrayList<Object> row = new ArrayList<>(100);
		for (int j = 0; j < i; j++) {
			row.add(null);
		}
		return row;
	}

	@Override
	public void reset(int rows, int columns) {
		// not needed in test
	}

	@Override
	public int numberOfRows() {
		return data.size();
	}

	@Override
	public int numberOfColumns() {
		return data.get(0).size();
	}

	@Override
	public void insertRowAt(int row) {
		data.add(row, buildRow(numberOfColumns()));
	}

	@Override
	public void deleteRowAt(int row) {
		data.remove(row);
	}

	@Override
	public void insertColumnAt(int column) {
		for (List<Object> row: data) {
			row.add(column, null);
		}
	}

	@Override
	public void deleteColumnAt(int column) {
		for (List<Object> row: data) {
			row.remove(column);
		}
	}

	@Override
	public void setContent(int row, int column, Object content) {
		data.get(row).set(column, content);
	}

	@Override
	public Object contentAt(int row, int column) {
		return data.get(row).get(column);
	}

	@Override
	public String getColumnName(int column) {
		return "col" + column;
	}

	@Override
	public void addChangeListener(TabularDataChangeListener listener) {
		// not needed in test
	}

	@Override
	public String getEditableString(int row, int column) {
		return String.valueOf(contentAt(row, column));
	}

	@Override
	public PasteInterface getPaste() {
		return new PasteText();
	}
}
