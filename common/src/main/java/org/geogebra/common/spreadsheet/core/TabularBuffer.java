package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;

public class TabularBuffer<T> implements HasTabularValues<T> {
	private List<List<T>> data = new ArrayList<>();
	private TabularRange source;

	public TabularRange getSource() {
		return source;
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

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public void setSource(TabularRange source) {
		this.source = source;
	}

	public void add(List<T> value) {
		data.add(value);
	}

	public void clear() {
		data.clear();
	}
}
