package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;

/**
 * A tabular clipboard to copy to and paste to.
 * @param <T> type of the content.
 */
public final class TabularClipboard<T> implements HasTabularValues<T> {
	private List<List<T>> data = new ArrayList<>();
	private TabularRange sourceRange;
	private SelectionType type;

	public TabularRange getSourceRange() {
		return sourceRange;
	}

	public SelectionType getType() {
		return type;
	}

	@Override
	public T contentAt(int row, int column) {
		List<T> rowList = data.get(row);
		return rowList != null ? rowList.get(column) : null;
	}

	@Override
	public int numberOfRows() {
		return data.size();
	}

	@Override
	public int numberOfColumns() {
		return isEmpty() ? 0 : data.get(0).size();
	}

	/**
	 *
	 * @return true if clipboard is empty.
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * Clear buffer
	 */
	public void clear() {
		data.clear();
	}

	/**
	 * Copy from tabular data to the buffer.
	 * @param tabularData to copy from.
	 * @param range to copy.
	 * @param type selection type
	 */
	public void copy(TabularData tabularData, TabularRange range, SelectionType type) {
		this.sourceRange = range;
		this.type = type;
		clear();
		for (int row = range.getFromRow(); row < range.getToRow() + 1; row++) {
			List<T> rowData = new ArrayList<>();
			for (int column = range.getFromColumn(); column
					< range.getToColumn() + 1; column++) {
				rowData.add((T) tabularData.contentAt(row, column));
			}
			data.add(rowData);
		}
	}
}