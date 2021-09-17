package org.geogebra.common.gui.view.table;

/**
 * Has information about table cell dimensions.
 */
public interface TableValuesDimensions {
	/** Header height */
	public static final int HEADER_HEIGHT = 48;
	/** Row height */
	public static final int ROW_HEIGHT = 40;
	/** Minimum column width */
	public static final int MIN_COLUMN_WIDTH = 120;
	/** Maximum column width */
	public static final int MAX_COLUMN_WIDTH = 180;
	/** Left margin of the cell */
	public static final int CELL_LEFT_MARGIN = 16;
	/** Right margin of the cell */
	public static final int CELL_RIGHT_MARGIN = 28;

	/**
	 * Returns the column width.
	 * @param column column
	 * @return the width of the colum
	 */
	int getColumnWidth(int column);

	/**
	 * Calculates the minimum width for the currently edited cell
	 *
	 * @param column column of the currently edited cell
	 * @param row row of the currently edited cell
	 * @return the width of the column excluding the currently edited cell
	 */
	int getMinEditingColumnWidth(int row, int column);
}
