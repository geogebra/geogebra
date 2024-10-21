package org.geogebra.common.gui.view.spreadsheet;

public interface HasTabularValues<T> {
	T contentAt(int row, int column);

	int numberOfRows();

	int numberOfColumns();
}
