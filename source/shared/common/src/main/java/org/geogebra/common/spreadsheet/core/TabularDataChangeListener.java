package org.geogebra.common.spreadsheet.core;

/**
 * An abstraction (only implemented by {@link Spreadsheet}) for listening to changes to
 * {@code KernelTabularDataAdapter}'s data, data dimensions, or cell sizes.
 *
 * @apiNote All indices (row, column) are 0-based.
 */
public interface TabularDataChangeListener {

	/**
	 * Called when the data at (row, column) was updated or deleted.
	 * @param row Row index, or -1 if all rows are affected.
	 * @param column Column index, or -1 if all columns are affected.
	 */
	void tabularDataDidChange(int row, int column);

	/**
	 * Called when the number or size of rows and/or columns has changed.
	 * @param dimensions New spreadsheet dimensions.
	 */
	void tabularDataDimensionsDidChange(SpreadsheetDimensions dimensions);
}
