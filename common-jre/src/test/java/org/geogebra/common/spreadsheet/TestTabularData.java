package org.geogebra.common.spreadsheet;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.spreadsheet.core.CellDragPasteHandler;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularDataChangeListener;
import org.geogebra.common.spreadsheet.core.TabularDataPasteInterface;
import org.geogebra.common.spreadsheet.core.TabularDataPasteText;
import org.geogebra.common.spreadsheet.style.CellFormat;

public class TestTabularData implements TabularData<String> {

	List<List<String>> data = new ArrayList<>();

	/**
	 * Simple tabular data (initial size 100 x 100).
	 */
	public TestTabularData() {
		for (int i = 0; i < 100; i++) {
			data.add(buildRow(100));
		}
	}

	private List<String> buildRow(int i) {
		ArrayList<String> row = new ArrayList<>(100);
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
		for (List<String> row: data) {
			row.add(column, null);
		}
	}

	@Override
	public void deleteColumnAt(int column) {
		for (List<String> row: data) {
			row.remove(column);
		}
	}

	@Override
	public void setContent(int row, int column, Object content) {
		data.get(row).set(column, (String) content);
	}

	@Override
	public void removeContentAt(int row, int column) {
		data.get(row).set(column, null);
	}

	@Override
	public String contentAt(int row, int column) {
		return data.get(row).get(column);
	}

	@Override
	public String serializeContentAt(int row, int column) {
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
	public TabularDataPasteInterface<String> getPaste() {
		return new TabularDataPasteText();
	}

	@Override
	public CellFormat getFormat() {
		return new CellFormat(null);
	}

	@Override
	public int getAlignment(int row, int column) {
		return CellFormat.ALIGN_RIGHT;
	}

	@Override
	public boolean hasError(int row, int column) {
		return false;
	}

	@Override
	public String getErrorString() {
		return "";
	}

	@Override
	public CellDragPasteHandler getCellDragPasteHandler() {
		return null;
	}
}