package org.geogebra.common.spreadsheet;

import org.geogebra.common.util.shape.Rectangle;

/**
 * @Note: This type is not designed to be thread-safe.
 */
final class TableLayout {

	/**
	 * A (rectangular) portion of the table layout.
	 */
	static class Portion {

		final int fromColumn;
		final int fromRow;
		final int numberOfColumns;
		final int numberOfRows;
		final float xOffset; // cumulated column widths left of fromColumn
		final float yOffset;
		final float[] columnWidths;
		final float[] rowHeights;

		Portion(int fromColumn, int fromRow, int numberOfColumns, int numberOfRows, float xOffset, float yOffset, float[] columnWidths, float[] rowHeights) {
			this.fromColumn = fromColumn;
			this.fromRow = fromRow;
			this.numberOfColumns = numberOfColumns;
			this.numberOfRows = numberOfRows;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.columnWidths = columnWidths;
			this.rowHeights = rowHeights;
		}
	}

	TableLayout(int rows, int columns, float defaultRowHeight, float defaultColumnWidth) {
	}

	void setTableSize(int rows, int columns) { }

	void setWidthForColumns(float width, int... columnIndices) { }

	void setHeightForRows(float height, int... rowIndices) { }

	void makeFirstRowSticky(boolean stickyFirstRow) { }

	void makeFirstColumnSticky(boolean stickyFirstColumn) { }

	TableLayout.Portion getLayoutIntersecting(Rectangle visibleArea) { return null; }
}
