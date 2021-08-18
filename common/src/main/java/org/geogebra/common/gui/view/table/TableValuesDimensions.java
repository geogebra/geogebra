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
}
