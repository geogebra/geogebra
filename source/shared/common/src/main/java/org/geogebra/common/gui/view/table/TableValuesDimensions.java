package org.geogebra.common.gui.view.table;

/**
 * Has information about table cell dimensions.
 */
public interface TableValuesDimensions {
	/** Header height */
	int HEADER_HEIGHT = 48;
	/** Row height */
	int ROW_HEIGHT = 40;
	/** Minimum column width */
	int MIN_COLUMN_WIDTH = 120;
	/** Maximum column width */
	int MAX_COLUMN_WIDTH = 180;
	/** Width of the column for fade */
	int FADE_COLUMN_WIDTH = 56;
	/** Left margin of the cell */
	int CELL_LEFT_MARGIN = 16;
	/** Right margin of the cell */
	int CELL_RIGHT_MARGIN = 28;

	/**
	 * Returns the column width.
	 * @param column column
	 * @return the width of the column
	 */
	int getColumnWidth(int column);

	/**
	 * Calculating the column width excluding a row
	 *
	 * @param column column
	 * @param exceptRow row which should be excluded
	 * @return the width of the column excluding the exceptRow
	 */
	int getColumnWidth(int column, int exceptRow);
}
