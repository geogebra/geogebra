package org.geogebra.common.spreadsheet.core;

interface TabularSelection {

	void clearSelection();

	void selectRow(int row);

	void selectColumn(int column);

	void select(Selection selection, boolean extend);

	void selectAll();
}
