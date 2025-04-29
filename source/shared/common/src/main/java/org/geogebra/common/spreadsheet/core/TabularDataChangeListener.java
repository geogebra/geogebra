package org.geogebra.common.spreadsheet.core;

/**
 * @apiNote All indices (row, column) are 0-based.
 */
public interface TabularDataChangeListener {

	/**
	 * Called when the data at (row, column) was updated or deleted.
	 * @param row Row index.
	 * @param column Column index.
	 */
	void tabularDataDidChange(int row, int column);

	/**
	 * Called when number or size of rows and/or columns changes.
	 * @param dimensions New row/column dimensions.
	 */
	void tabularDataSizeDidChange(SpreadsheetDimensions dimensions);
}
