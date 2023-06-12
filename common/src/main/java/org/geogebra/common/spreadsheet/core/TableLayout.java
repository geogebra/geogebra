package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.geogebra.common.util.shape.Rectangle;

/**
 * @Note: This type is not designed to be thread-safe.
 */
public final class TableLayout {

	private double[] columnWidths;
	private double[] rowHeights;
	private double[] cumulativeWidths;
	private double[] cumulativeHeights;

	public double getWidth(int i) {
		return columnWidths[i];
	}

	public double getHeight(int row) {
		return rowHeights[row];
	}

	public double getX(int i) {
		return cumulativeWidths[i];
	}
	public double getY(int i) {
		return cumulativeHeights[i];
	}

	public int numberOfRows() {
		return rowHeights.length;
	}

	public int numberOfColumns() {
		return columnWidths.length;
	}

	public Rectangle getBounds(int x, int y) {
		return new Rectangle(cumulativeWidths[x], cumulativeHeights[x],
				columnWidths[x], rowHeights[0]);
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

	public void setWidthForColumns(float width, int... columnIndices) {
		for (int k: columnIndices) {
			columnWidths[k] = width;
		}
		for (int i = columnIndices[0]; i < columnWidths.length - 1; i++) {
			cumulativeWidths[i + 1] = cumulativeWidths[i] + columnWidths[i];
		}
	}

	public void setHeightForRows(float height, int... rowIndices) {
		for (int k: rowIndices) {
			rowHeights[k] = height;
		}
		for (int i = rowIndices[0]; i < cumulativeHeights.length - 1; i++) {
			cumulativeHeights[i + 1] = cumulativeHeights[i] + rowHeights[i];
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
