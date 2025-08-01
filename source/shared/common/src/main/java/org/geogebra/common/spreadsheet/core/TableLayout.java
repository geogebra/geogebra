package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Stores sizes and coordinates of spreadsheet cells
 *
 * @apiNote This type is not designed to be thread-safe.
 */
final class TableLayout {
	private static final double DEFAULT_CELL_WIDTH = 120;
	private static final double DEFAULT_CELL_HEIGHT = 36;
	private static final double DEFAULT_ROW_HEADER_WIDTH = 52;

	private static final int MIN_CELL_SIZE = 10;
	private double[] columnWidths;
	private double[] rowHeights;
	private double[] cumulativeWidths;
	private double[] cumulativeHeights;
	final double defaultRowHeight;
	final double defaultColumnWidth;
	private double rowHeaderWidth = DEFAULT_ROW_HEADER_WIDTH;
	private double columnHeaderHeight;

	/**
	 * @param rows Number of rows
	 * @param columns Number of columns
	 * @param defaultRowHeight Default row height
	 * @param defaultColumnWidth Default column width
	 */
	TableLayout(int rows, int columns, double defaultRowHeight, double defaultColumnWidth) {
		columnWidths = new double[columns];
		cumulativeWidths = new double[columns + 1];
		rowHeights = new double[rows];
		cumulativeHeights = new double[rows + 1];
		this.defaultRowHeight = defaultRowHeight;
		this.defaultColumnWidth = defaultColumnWidth;
		this.columnHeaderHeight = defaultRowHeight;
		setWidthForColumns(defaultColumnWidth, 0, columns - 1);
		setHeightForRows(defaultRowHeight, 0, rows - 1);
	}

	TableLayout(int rows, int columns) {
		this(rows, columns, DEFAULT_CELL_HEIGHT, DEFAULT_CELL_WIDTH);
	}

	/**
	 * Get width of a column.
	 * @param column column index
	 * @return width in points
	 */
	double getWidth(int column) {
		return columnWidths[column];
	}

	/**
	 * Get height of a row.
	 * @param row row index
	 * @return height in points
	 */
	double getHeight(int row) {
		return rowHeights[row];
	}

	/**
	 * Get the left edge of a column.
	 * @param column column index
	 * @return x-coordinate of column's left edge
	 */
	double getMinX(int column) {
		return cumulativeWidths[column];
	}

	/**
	 * Get the top edge of a row.
	 * @param row row index
	 * @return y-coordinate of row's top edge
	 */
	double getMinY(int row) {
		return cumulativeHeights[row];
	}

	/**
	 * @return number of rows
	 */
	int numberOfRows() {
		return rowHeights.length;
	}

	/**
	 * @return number of columns
	 */
	int numberOfColumns() {
		return columnWidths.length;
	}

	@Nonnull Rectangle getBounds(int row, int column) {
		return new Rectangle(cumulativeWidths[column],
				cumulativeWidths[column] + columnWidths[column],
				cumulativeHeights[row],
				cumulativeHeights[row] + rowHeights[row]);
	}

	/**
	 * Get selection bounds, relative to some viewport.
	 * @param selection A selection.
	 * @param viewport The current viewport.
	 * @return Screen bounds of the selection if it's finite, or {@code null} if it's empty
	 * or unbounded in either direction (e.g. whole column).
	 */
	@CheckForNull Rectangle getBounds(TabularRange selection, Rectangle viewport) {
		if (selection.getMinColumn() < 0 || selection.getMaxColumn() >= numberOfColumns()
				|| selection.getMinRow() < 0 || selection.getMaxRow() >= numberOfRows()) {
			return null;
		}
		double offsetX = -viewport.getMinX() + getRowHeaderWidth();
		double offsetY = -viewport.getMinY() + getColumnHeaderHeight();
		double minX = getMinX(selection.getMinColumn());
		double minY = getMinY(selection.getMinRow());
		double maxX = getMinX(selection.getMaxColumn() + 1);
		double maxY = getMinY(selection.getMaxRow() + 1);
		return new Rectangle(minX + offsetX, maxX + offsetX,
				minY + offsetY, maxY + offsetY);
	}

	@Nonnull Rectangle getRowHeaderBounds(int row) {
		return new Rectangle(0,
				rowHeaderWidth,
				cumulativeHeights[row],
				cumulativeHeights[row] + rowHeights[row]);
	}

	@Nonnull Rectangle getColumnHeaderBounds(int column) {
		return new Rectangle(cumulativeWidths[column],
				cumulativeWidths[column] + columnWidths[column],
				0,
				columnHeaderHeight);
	}

	double getTotalHeight() {
		return cumulativeHeights[cumulativeHeights.length - 1] + getColumnHeaderHeight();
	}

	double getTotalWidth() {
		return cumulativeWidths[cumulativeWidths.length - 1] + getRowHeaderWidth();
	}

	/**
	 * @param x Mouse position relative to viewport, in logical points.
	 * @param y Mouse position relative to viewport, in logical points.
	 * @param viewport The viewport.
	 * @return The {@link DragState} for the given point in the spreadsheet.
	 */
	// TODO find a better method name
	@Nonnull DragState getResizeAction(double x, double y, Rectangle viewport) {
		double xAbs = x + viewport.getMinX();
		double yAbs = y + viewport.getMinY();
		int row = findRow(yAbs);
		int column = findColumn(xAbs);
		if (y < columnHeaderHeight && column >= 0
				&& x > rowHeaderWidth
				&& xAbs > cumulativeWidths[column + 1] + rowHeaderWidth - 5) {
			return new DragState(MouseCursor.RESIZE_X, row, column);
		}
		if (y < columnHeaderHeight && column > 0
				&& x > rowHeaderWidth
				&& xAbs < cumulativeWidths[column] + rowHeaderWidth + 5) {
			return new DragState(MouseCursor.RESIZE_X, row, column - 1);
		}
		if (x < rowHeaderWidth && row >= 0
				&& y > columnHeaderHeight
				&& yAbs > cumulativeHeights[row + 1] + columnHeaderHeight - 5) {
			return new DragState(MouseCursor.RESIZE_Y, row, column);
		}
		if (x < rowHeaderWidth && row > 0
				&& y > columnHeaderHeight
				&& yAbs < cumulativeHeights[row] + columnHeaderHeight + 5) {
			return new DragState(MouseCursor.RESIZE_Y, row - 1, column);
		}
		return new DragState(MouseCursor.DEFAULT, row, column);
	}

	/**
	 * Update number of columns, if columns are added they have 0 width
	 * @param columns number of columns
	 */
	void setNumberOfColumns(int columns) {
		if (columns > columnWidths.length) {
			columnWidths = Arrays.copyOf(columnWidths, columns);
			cumulativeWidths = new double[columns + 1];
			updateCumulativeWidths(0);
		} else if (columns < columnWidths.length) {
			columnWidths = Arrays.copyOf(columnWidths, columns);
			cumulativeWidths = Arrays.copyOf(cumulativeWidths, columns + 1);
		}
	}

	/**
	 * Update number of rows, if rows are added they have 0 height
	 * @param rows number of rows
	 */
	void setNumberOfRows(int rows) {
		if (rows > rowHeights.length) {
			rowHeights = Arrays.copyOf(rowHeights, rows);
			cumulativeHeights = new double[rows + 1];
			updateCumulativeHeights(0);
		} else if (rows < rowHeights.length) {
			rowHeights = Arrays.copyOf(rowHeights, rows);
			cumulativeHeights = Arrays.copyOf(cumulativeHeights, rows + 1);
		}
	}

	void dimensionsDidChange(SpreadsheetDimensions dimensions) {
		setNumberOfColumns(dimensions.getColumns());
		setNumberOfRows(dimensions.getRows());
		for (int i = 0; i < columnWidths.length; i++) {
			columnWidths[i] = dimensions.getColumnWidths().getOrDefault(i, defaultColumnWidth);
		}
		for (int i = 0; i < rowHeights.length; i++) {
			rowHeights[i] = dimensions.getRowHeights().getOrDefault(i, defaultRowHeight);
		}
		updateCumulativeHeights(0);
		updateCumulativeWidths(0);
	}

	/**
	 * Sets the width for a range of columns.
	 * @param width double
	 * @param minColumn Index from where to start setting the width
	 * @param maxColumn Index of where to stop setting the width (inclusive)
	 */
	void setWidthForColumns(double width, int minColumn, int maxColumn) {
		for (int column = minColumn; column <= maxColumn; column++) {
			columnWidths[column] = width;
		}
		updateCumulativeWidths(minColumn);
	}

	private void updateCumulativeWidths(int minColumn) {
		for (int column = minColumn; column < columnWidths.length; column++) {
			cumulativeWidths[column + 1] = cumulativeWidths[column] + columnWidths[column];
		}
	}

	/**
	 * Sets the height for a range of rows.
	 * @param height double
	 * @param minRow Index from where to start setting the height
	 * @param maxRow Index of where to stop setting the width (inclusive)
	 */
	void setHeightForRows(double height, int minRow, int maxRow) {
		for (int row = minRow; row <= maxRow; row++) {
			rowHeights[row] = height;
		}
		updateCumulativeHeights(minRow);
	}

	private void updateCumulativeHeights(int minRow) {
		for (int row = minRow; row < rowHeights.length; row++) {
			cumulativeHeights[row + 1] = cumulativeHeights[row] + rowHeights[row];
		}
	}

	void makeFirstRowSticky(boolean stickyFirstRow) {
		// always sticky?
	}

	void makeFirstColumnSticky(boolean stickyFirstColumn) {
		// always sticky?
	}

	TableLayout.Portion getLayoutIntersecting(Rectangle visibleArea) {
		int firstColumn = Math.max(0, findColumn(visibleArea.getMinX() + rowHeaderWidth));
		int lastColumn = Math.min(columnWidths.length - 1, findColumn(visibleArea.getMaxX()));
		int firstRow = Math.max(0, findRow(visibleArea.getMinY() + columnHeaderHeight));
		int lastRow = Math.min(rowHeights.length - 1, findRow(visibleArea.getMaxY()));
		return new Portion(firstColumn, firstRow, lastColumn, lastRow,
				visibleArea.getMinX(), visibleArea.getMinY());
	}

	/**
	 * @param x position relative to viewport, in logical points
	 * @return hit column index (0 based, hitting left counts), -1 if header is hit
	 */
	int findColumn(double x) {
		return getClosestLowerIndex(cumulativeWidths, x - rowHeaderWidth);
	}

	/**
	 * @param y position relative to viewport, in logical points
	 * @return hit row index (0 based, hitting top border counts), -1 if header is hit
	 */
	int findRow(double y) {
		return getClosestLowerIndex(cumulativeHeights, y - columnHeaderHeight);
	}

	private int getClosestLowerIndex(double[] borders, double value) {
		int searchResult = Arrays.binarySearch(borders, value);
		if (searchResult >= 0) {
			return searchResult;
		} else {
			int closestHigherIndex = -1 - searchResult; // see contract of binarySearch
			return closestHigherIndex - 1;
		}
	}

	double getRowHeaderWidth() {
		return rowHeaderWidth;
	}

	double getColumnHeaderHeight() {
		return columnHeaderHeight;
	}

	/**
	 * Get new column width after resize.
	 * @param col row index
	 * @param x resizing cursor's x-coordinate
	 * @return column height
	 */
	double getWidthForColumnResize(int col, double x) {
		return Math.max(MIN_CELL_SIZE, x - cumulativeWidths[col] - rowHeaderWidth);
	}

	/**
	 * Get new row height after resize.
	 * @param row row index
	 * @param y resizing cursor's y-coordinate
	 * @return row height
	 */
	double getHeightForRowResize(int row, double y) {
		return Math.max(MIN_CELL_SIZE, y - cumulativeHeights[row] - columnHeaderHeight);
	}

	/**
	 * Resets all rows and columns to their default sizes
	 */
	void resetCellSizes() {
		setWidthForColumns(defaultColumnWidth, 0, columnWidths.length - 1);
		setHeightForRows(defaultRowHeight, 0, rowHeights.length - 1);
	}

	/**
	 * After a row has been deleted, the remaining rows (i.e. the ones succeeding the deleted row)
	 * need to be resized. In this scenario, row 10 applies the height of row 11, row 11 applies
	 * the height of row 12, etc.
	 * @param resizeFrom Index of where to start resizing the remaining rows
	 * @param numberOfRows Number of rows
	 */
	void resizeRemainingRowsAscending(int resizeFrom, int numberOfRows) {
		for (int row = resizeFrom; row < numberOfRows - 1; row++) {
			setHeightForRows(getHeight(row + 1), row, row);
		}
		setHeightForRows(defaultRowHeight, numberOfRows - 1, numberOfRows - 1);
	}

	/**
	 * Same as {@link #resizeRemainingRowsAscending(int, int)}, but for columns
	 * @param resizeFrom Index of where to start resizing the remaining columns
	 * @param numberOfColumns Number of columns
	 */
	void resizeRemainingColumnsAscending(int resizeFrom, int numberOfColumns) {
		for (int column = resizeFrom; column < numberOfColumns - 1; column++) {
			setWidthForColumns(getWidth(column + 1), column, column);
		}
		setWidthForColumns(defaultColumnWidth, numberOfColumns - 1, numberOfColumns - 1);
	}

	/**
	 * After a row has been inserted, row 100 applies the size of row 99, row 99 applies the size
	 * of row 98, and so on. This is basically the direct counterpart to the operation that is
	 * performed when a row gets deleted.
	 * @param resizeUntil Until which row index the resizing should be performed
	 * @param numberOfRows Number of rows
	 */
	void resizeRemainingRowsDescending(int resizeUntil, int numberOfRows) {
		for (int row = numberOfRows - 1; row > resizeUntil; row--) {
			setHeightForRows(getHeight(row - 1), row, row);
		}
	}

	/**
	 * Same as {@link #resizeRemainingRowsDescending(int, int)}, but for columns
	 * @param resizeUntil Until which column index the resizing should be performed
	 * @param numberOfColumns Number of columns
	 */
	void resizeRemainingColumnsDescending(int resizeUntil, int numberOfColumns) {
		for (int column = numberOfColumns - 1; column > resizeUntil; column--) {
			setWidthForColumns(getWidth(column - 1), column, column);
		}
	}

	/**
	 * @return A {@code columnIndex => width} map for all columns that are not of
	 * {@code defaultColumnWidth} width.
	 */
	@Nonnull Map<Integer, Double> getCustomColumnWidths() {
		Map<Integer, Double> widths = new HashMap<>();
		for (int i = 0; i < columnWidths.length; i++) {
			if (columnWidths[i] != defaultColumnWidth) {
				widths.put(i, columnWidths[i]);
			}
		}
		return widths;
	}

	/**
	 * @return A {@code rowIndex => height} map for all rows that are not of
	 * {@code defaultRowHeight} height.
	 */
	@Nonnull Map<Integer, Double> getCustomRowHeights() {
		Map<Integer, Double> heights = new HashMap<>();
		for (int i = 0; i < rowHeights.length; i++) {
			if (rowHeights[i] != defaultRowHeight) {
				heights.put(i, rowHeights[i]);
			}
		}
		return heights;
	}

	/**
	 * A (rectangular) portion of the table layout.
	 */
	static final class Portion {

		final int fromColumn;
		final int fromRow;
		final double xOffset; // cumulated column widths left of fromColumn
		final double yOffset;
		final int toRow;
		final int toColumn;

		Portion(int fromColumn, int fromRow, int toColumn, int toRow, double xOffset,
				double yOffset) {
			this.fromColumn = fromColumn;
			this.fromRow = fromRow;
			this.toRow = toRow;
			this.toColumn = toColumn;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}
	}
}
