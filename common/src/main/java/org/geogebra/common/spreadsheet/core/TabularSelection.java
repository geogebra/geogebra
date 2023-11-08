package org.geogebra.common.spreadsheet.core;

interface TabularSelection {

	void clearSelection();

	void selectRow(int row, boolean extend, boolean addSelection);

	void selectColumn(int column, boolean extend, boolean addSelection);

	void select(Selection selection, boolean extend, boolean addSelection);

	void selectAll();
}
