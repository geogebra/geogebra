package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;

import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Stores sizes and coordinates of spreadsheet cells
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class TableLayout implements PersistenceListener {
	public static final int DEFAULT_CELL_WIDTH = 120;
	public static final int DEFAULT_CELL_HEIGHT = 36;
	public static final int DEFAULT_ROW_HEADER_WIDTH = 52;

	private static final int MIN_CELL_SIZE = 10;
	private double[] columnWidths;
	private double[] rowHeights;
	private double[] cumulativeWidths;
	private double[] cumulativeHeights;
	private double rowHeaderWidth = DEFAULT_ROW_HEADER_WIDTH;
	private double columnHeaderHeight = DEFAULT_CELL_HEIGHT;

	public double getWidth(int column) {
		return columnWidths[column];
	}

	public double getHeight(int row) {
		return rowHeights[row];
	}

	public double getX(int column) {
		return cumulativeWidths[column];
	}

	public double getY(int row) {
		return cumulativeHeights[row];
	}

	public int numberOfRows() {
		return rowHeights.length;
	}

	public int numberOfColumns() {
		return columnWidths.length;
	}

	Rectangle getBounds(int row, int column) {
		return new Rectangle(cumulativeWidths[column],
				cumulativeWidths[column] + columnWidths[column],
				cumulativeHeights[row],
				cumulativeHeights[row] + rowHeights[row]);
	}

	Rectangle getBounds(TabularRange selection, Rectangle viewport) {
		double offsetX = -viewport.getMinX() + getRowHeaderWidth();
		double offsetY = -viewport.getMinY() + getColumnHeaderHeight();
		if (selection.getMinColumn() >= 0 && selection.getMinRow() >= 0) {
			int minX = (int) getX(selection.getMinColumn());
			int minY = (int) getY(selection.getMinRow());
			int maxX = (int) getX(selection.getMaxColumn() + 1);
			int maxY = (int) getY(selection.getMaxRow() + 1);
			return new Rectangle(minX + offsetX, maxX + offsetX,
					minY + offsetY, maxY + offsetY);
		}
		return null;
	}

	Rectangle getRowHeaderBounds(int row) {
		return new Rectangle(0,
				rowHeaderWidth,
				cumulativeHeights[row],
				cumulativeHeights[row] + rowHeights[row]);
	}

	Rectangle getColumnHeaderBounds(int column) {
		return new Rectangle(cumulativeWidths[column],
				cumulativeWidths[column] + columnWidths[column],
				0,
				columnHeaderHeight);
	}

	public double getTotalHeight() {
		return cumulativeHeights[cumulativeHeights.length - 1] + getColumnHeaderHeight();
	}

	public double getTotalWidth() {
		return cumulativeWidths[cumulativeWidths.length - 1] + getRowHeaderWidth();
	}

	DragState getResizeAction(double x, double y, Rectangle viewport) {
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
	public void setNumberOfColumns(int columns) {
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
	public void setNumberOfRows(int rows) {
		if (rows > rowHeights.length) {
			rowHeights = Arrays.copyOf(rowHeights, rows);
			cumulativeHeights = new double[rows + 1];
			updateCumulativeHeights(0);
		} else if (rows < rowHeights.length) {
			rowHeights = Arrays.copyOf(rowHeights, rows);
			cumulativeHeights = Arrays.copyOf(cumulativeHeights, rows + 1);
		}
	}

	@Override
	public void persist(SpreadsheetDimensions dimensions) {
		dimensions.getWidthMap().clear();
		for (int i = 0; i < columnWidths.length; i++) {
			if (columnWidths[i] != DEFAULT_CELL_WIDTH) {
				dimensions.getWidthMap().put(i, (int) columnWidths[i]);
			}
		}
		dimensions.getHeightMap().clear();
		for (int i = 0; i < rowHeights.length; i++) {
			if (rowHeights[i] != DEFAULT_CELL_HEIGHT) {
				dimensions.getHeightMap().put(i, (int) rowHeights[i]);
			}
		}
	}

	void dimensionsDidChange(SpreadsheetDimensions dimensions) {
		setNumberOfColumns(dimensions.getColumns());
		setNumberOfRows(dimensions.getRows());
		for (int i = 0; i < columnWidths.length; i++) {
			columnWidths[i] = dimensions.getWidthMap().getOrDefault(i, DEFAULT_CELL_WIDTH);
		}
		for (int i = 0; i < rowHeights.length; i++) {
			rowHeights[i] = dimensions.getHeightMap().getOrDefault(i, DEFAULT_CELL_HEIGHT);
		}
		updateCumulativeHeights(0);
		updateCumulativeWidths(0);
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

	TableLayout(int rows, int columns, float defaultRowHeight, float defaultColumnWidth) {
		columnWidths = new double[columns];
		cumulativeWidths = new double[columns + 1];
		rowHeights = new double[rows];
		cumulativeHeights = new double[rows + 1];
		setWidthForColumns(defaultColumnWidth, 0, columns - 1);
		setHeightForRows(defaultRowHeight, 0, rows - 1);
	}

	void setTableSize(int rows, int columns) {
		// TODO
	}

	/**
	 * Sets the width for a range of columns
	 * @param width double
	 * @param minColumn Index from where to start setting the width
	 * @param maxColumn Index of where to stop setting the width (inclusive)
	 */
	public void setWidthForColumns(double width, int minColumn, int maxColumn) {
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
	 * Sets the height for a range of rows
	 * @param height double
	 * @param minRow Index from where to start setting the height
	 * @param maxRow Index of where to stop setting the width (inclusive)
	 */
	public void setHeightForRows(double height, int minRow, int maxRow) {
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
	 * @param x pixel coordinate within viewport
	 * @return hit column index (0 based, hitting left counts), -1 if header is hit
	 */
	public int findColumn(double x) {
		return getClosestLowerIndex(cumulativeWidths, x - rowHeaderWidth);
	}

	/**
	 * @param y pixel coordinate within viewport
	 * @return hit row index (0 based, hitting top border counts), -1 if header is hit
	 */
	public int findRow(double y) {
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

	public double getWidthForColumnResize(int col, double x) {
		return Math.max(MIN_CELL_SIZE, x - cumulativeWidths[col] - rowHeaderWidth);
	}

	public double getHeightForRowResize(int row, double y) {
		return Math.max(MIN_CELL_SIZE, y - cumulativeHeights[row] - columnHeaderHeight);
	}

	/**
	 * Resets all rows and columns to their default sizes
	 */
	public void resetCellSizes() {
		setWidthForColumns(DEFAULT_CELL_WIDTH, 0, columnWidths.length - 1);
		setHeightForRows(DEFAULT_CELL_HEIGHT, 0, rowHeights.length - 1);
	}

	/**
	 * After a row has been deleted, the reamining rows (i.e. the ones succeeding the deleted row)
	 * need to be resized. In this scenario, row 10 applies the hieght of row 11, row 11 applies
	 * the height of row 12, etc.
	 * @param resizeFrom Index of where to start resizing the remaining rows
	 * @param numberOfRows Number of rows
	 */
	public void resizeRemainingRowsAscending(int resizeFrom, int numberOfRows) {
		for (int row = resizeFrom; row < numberOfRows - 1; row++) {
			setHeightForRows(getHeight(row + 1), row, row);
		}
		setHeightForRows(DEFAULT_CELL_HEIGHT, numberOfRows - 1, numberOfRows - 1);
	}

	/**
	 * Same as {@link #resizeRemainingRowsAscending(int, int)}, but for columns
	 * @param resizeFrom Index of where to start resizing the remaining columns
	 * @param numberOfColumns Number of columns
	 */
	public void resizeRemainingColumnsAscending(int resizeFrom, int numberOfColumns) {
		for (int column = resizeFrom; column < numberOfColumns - 1; column++) {
			setWidthForColumns(getWidth(column + 1), column, column);
		}
		setWidthForColumns(DEFAULT_CELL_WIDTH,
				numberOfColumns - 1, numberOfColumns - 1);
	}

	/**
	 * After a row has been inserted, row 100 applies the size of row 99, row 99 applies the size
	 * of row 98, and so on. This is basically the direct counterpart to the operation that is
	 * performed when a row gets deleted.
	 * @param resizeUntil Until which row index the resizing should be performed
	 * @param numberOfRows Number of rows
	 */
	public void resizeRemainingRowsDescending(int resizeUntil, int numberOfRows) {
		for (int row = numberOfRows - 1; row > resizeUntil; row--) {
			setHeightForRows(getHeight(row - 1), row, row);
		}
	}

	/**
	 * Same as {@link #resizeRemainingRowsDescending(int, int)}, but for columns
	 * @param resizeUntil Until which column index the resizing should be performed
	 * @param numberOfColumns Number of columns
	 */
	public void resizeRemainingColumnsDescending(int resizeUntil, int numberOfColumns) {
		for (int column = numberOfColumns - 1; column > resizeUntil; column--) {
			setWidthForColumns(getWidth(column - 1), column, column);
		}
	}
}
