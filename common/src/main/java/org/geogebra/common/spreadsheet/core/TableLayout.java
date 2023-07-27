package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Stores sizes and coordinates of spreadsheet cells
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class TableLayout {

	private double[] columnWidths;
	private double[] rowHeights;
	private double[] cumulativeWidths;
	private double[] cumulativeHeights;

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

	public Rectangle getBounds(int row, int column) {
		return new Rectangle(cumulativeWidths[column],
				cumulativeWidths[column] + columnWidths[column],
				cumulativeHeights[row],
				cumulativeHeights[row] + rowHeights[row]);
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

		Portion(int fromColumn, int fromRow, int numberOfColumns, int numberOfRows, double xOffset,
				double yOffset) {
			this.fromColumn = fromColumn;
			this.fromRow = fromRow;
			this.numberOfColumns = numberOfColumns;
			this.numberOfRows = numberOfRows;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}
	}

	TableLayout(int rows, int columns, float defaultRowHeight, float defaultColumnWidth) {
		columnWidths = new double[columns];
		cumulativeWidths = new double[columns];
		rowHeights = new double[rows];
		cumulativeHeights = new double[rows];
		setWidthForColumns(defaultColumnWidth, IntStream.range(0, columns - 1).toArray());
		setHeightForRows(defaultRowHeight, IntStream.range(0, rows - 1).toArray());
	}

	void setTableSize(int rows, int columns) {

	}

	public void setWidthForColumns(double width, int... columnIndices) {
		for (int column: columnIndices) {
			columnWidths[column] = width;
		}
		for (int column = columnIndices[0]; column < columnWidths.length - 1; column++) {
			cumulativeWidths[column + 1] = cumulativeWidths[column] + columnWidths[column];
		}
	}

	public void setHeightForRows(double height, int... rowIndices) {
		for (int row: rowIndices) {
			rowHeights[row] = height;
		}
		for (int row = rowIndices[0]; row < cumulativeHeights.length - 1; row++) {
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
		return closest(Arrays.binarySearch(cumulativeWidths, x));
	}

	public int findRow(double y) {
		return closest(Arrays.binarySearch(cumulativeHeights, y));
	}

	private int closest(int searchResult) {
		return searchResult > 0 ? searchResult : -2 - searchResult;
	}
}
