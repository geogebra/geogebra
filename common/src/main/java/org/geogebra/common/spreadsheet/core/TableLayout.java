package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Stores sizes and coordinates of spreadsheet cells
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class TableLayout {

	private static final int MIN_CELL_SIZE = 10;
	private double[] columnWidths;
	private double[] rowHeights;
	private double[] cumulativeWidths;
	private double[] cumulativeHeights;
	private double rowHeaderWidth = 42;
	private double columnHeaderHeight = 20;

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

	MouseCursor getCursor(double x, double y, GPoint out) {
		int row = findRow(y);
		int column = findColumn(x);
		out.setLocation(column, row);

		if (row < 1 && column >= 0 && x > cumulativeWidths[column + 1] + rowHeaderWidth - 5) {
			return MouseCursor.RESIZE_X;
		}
		if (row < 1 && column > 0 && x < cumulativeWidths[column] + rowHeaderWidth + 5) {
			out.x--;
			return MouseCursor.RESIZE_X;
		}
		if (column < 1 && row >= 0 &&  y > cumulativeHeights[row + 1] + columnHeaderHeight - 5) {
			return MouseCursor.RESIZE_Y;
		}
		if (column < 1 && row > 0 && y < cumulativeHeights[row] + columnHeaderHeight + 5) {
			out.y--;
			return MouseCursor.RESIZE_Y;
		}
		return MouseCursor.DEFAULT;
	}

	/**
	 * A (rectangular) portion of the table layout.
	 */
	static class Portion {

		final int fromColumn;
		final int fromRow;
		final int numberOfColumns;
		final int numberOfRows;
		final double xOffset; // cumulated column widths left of fromColumn
		final double yOffset;
		final int toRow;
		final int toColumn;

		Portion(int fromColumn, int fromRow, int numberOfColumns, int numberOfRows, double xOffset,
				double yOffset) {
			this.fromColumn = fromColumn;
			this.fromRow = fromRow;
			this.numberOfColumns = numberOfColumns;
			this.numberOfRows = numberOfRows;
			this.toRow = fromRow + numberOfRows - 1;
			this.toColumn = fromColumn + numberOfColumns - 1;
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

	void setWidthForColumns(double width, int minColumn, int maxColumn) {
		for (int column = minColumn; column <= maxColumn; column++) {
			columnWidths[column] = width;
		}
		for (int column = minColumn; column < columnWidths.length - 1; column++) {
			cumulativeWidths[column + 1] = cumulativeWidths[column] + columnWidths[column];
		}
	}

	void setHeightForRows(double height, int minRow, int maxRow) {
		for (int row = minRow; row <= maxRow; row++) {
			rowHeights[row] = height;
		}
		for (int row = minRow; row < cumulativeHeights.length - 1; row++) {
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
		int firstColumn = Math.max(0, findColumn(visibleArea.getMinX()));
		int lastColumn = Math.min(columnWidths.length - 1, findColumn(visibleArea.getMaxX()) + 1);
		int firstRow = Math.max(0, findRow(visibleArea.getMinY()));
		int lastRow = Math.min(rowHeights.length - 1, findRow(visibleArea.getMaxY()) + 1);
		return new Portion(firstColumn, firstRow, lastColumn - firstColumn, lastRow - firstRow,
				visibleArea.getMinX(), visibleArea.getMinY());
	}

	public int findColumn(double x) {
		return closest(Arrays.binarySearch(cumulativeWidths, x - rowHeaderWidth));
	}

	public int findRow(double y) {
		return closest(Arrays.binarySearch(cumulativeHeights, y - columnHeaderHeight));
	}

	private int closest(int searchResult) {
		return searchResult > 0 ? searchResult : -2 - searchResult;
	}

	double getRowHeaderWidth() {
		return rowHeaderWidth;
	}

	double getColumnHeaderHeight() {
		return columnHeaderHeight;
	}

	public double resizeColumn(int col, int x) {
		return Math.max(MIN_CELL_SIZE, x - cumulativeWidths[col] - rowHeaderWidth);
	}

	public double resizeRow(int row, int y) {
		return Math.max(MIN_CELL_SIZE, y - cumulativeHeights[row] - columnHeaderHeight);
	}
}
