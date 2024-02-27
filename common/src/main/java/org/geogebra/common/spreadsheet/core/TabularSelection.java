package org.geogebra.common.spreadsheet.core;

// TODO design: Surprisingly, this is implemented by SpreadsheetController, but not SpreadsheetSelectionController.
// TODO naming: This type does not represent a tabular selection.
//  But I don't think we need this interface at all, so I'd remove it.
interface TabularSelection {

	void clearSelection();

	void selectRow(int row, boolean extend, boolean addSelection);

	/**
	 * @param column column to select
	 * @param extend whether to extend last selection
	 * @param addSelection whether to add separate selection
	 */
	void selectColumn(int column, boolean extend, boolean addSelection);

	void select(TabularRange selection, boolean extend, boolean addSelection);

	void selectAll();
}
