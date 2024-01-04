package org.geogebra.common.spreadsheet.core;

interface TabularSelection {

	void clearSelection();

	void selectRow(int row, boolean extend, boolean addSelection);

	/**
	 * @param column column to select
	 * @param extend whether to extend last selection
	 * @param addSelection whether to add separate selection
	 */
	void selectColumn(int column, boolean extend, boolean addSelection);

	boolean select(TabularRange selection, boolean extend, boolean addSelection);

	void selectAll();
}
