package org.geogebra.common.spreadsheet;

interface TabularSelection {

	void clearSelection();
	void selectRow(int row);
	void selectColumn(int column);
	void select(Selection selection);
	void selectAll();
}
