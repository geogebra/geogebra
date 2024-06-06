package org.geogebra.common.spreadsheet.core;

public interface TabularDataChangeListener {
	void tabularDataDidChange(int row, int column);

	/**
	 * Called when number or size of rows/columns changes
	 * @param dimensions row/column dimensions
	 */
	void tabularDataSizeDidChange(SpreadsheetDimensions dimensions);
}
