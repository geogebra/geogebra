package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Kernel;

public class TabularRange {
	private final int anchorColumn;
	private final int anchorRow;
	private final int minColumn;
	private final int minRow;
	private final int maxColumn;
	private final int maxRow;

	/**
	 * @param anchorColumn anchor column
	 * @param anchorRow anchor row
	 * @param minColumn lowest column
	 * @param minRow lowest row
	 * @param maxColumn highest colu,m
	 * @param maxRow highest row
	 */
	public TabularRange(int anchorColumn, int anchorRow, int minColumn,
			int minRow, int maxColumn, int maxRow) {
		this.anchorColumn = anchorColumn;
		this.anchorRow = anchorRow;
		this.minColumn = minColumn;
		this.minRow = minRow;
		this.maxColumn = maxColumn;
		this.maxRow = maxRow;
	}

	/**
	 * @param anchorColumn anchor column
	 * @param anchorRow anchor row
	 * @param col2 end column
	 * @param row2 end row
	 */
	public TabularRange(int anchorColumn, int anchorRow, int col2,
			int row2) {
		minColumn = Math.min(anchorColumn, col2);
		maxColumn = Math.max(anchorColumn, col2);
		minRow = Math.min(anchorRow, row2);
		maxRow = Math.max(anchorRow, row2);

		this.anchorColumn = anchorColumn;
		this.anchorRow = anchorRow;
	}

	public TabularRange(int anchorColumn, int anchorRow) {
		this(anchorColumn, anchorRow, anchorColumn, anchorRow);
	}

	public int getMinRow() {
		return minRow;
	}

	public int getMaxRow() {
		return maxRow;
	}

	public int getMinColumn() {
		return minColumn;
	}

	public int getMaxColumn() {
		return maxColumn;
	}

	public boolean isColumn() {
		return anchorRow == -1;
	}

	public boolean isRow() {
		return anchorColumn == -1;
	}

	public int getWidth() {
		return maxColumn - minColumn + 1;
	}

	public int getHeight() {
		return maxRow - minRow + 1;
	}

	/**
	 * @return true if cell range is 2xn or nx2
	 */
	public boolean is2D() {
		return (maxColumn - minColumn == 1) || (maxRow - minRow == 1);
	}

	/**
	 * @return true if cell range is 3xn or nx3
	 */
	public boolean is3D() {
		return (maxColumn - minColumn == 2) || (maxRow - minRow == 2);
	}

	/**
	 * @return true if cell range is 1xn, nx1, a row or a column
	 */
	public boolean is1D() {
		return (maxColumn - minColumn == 0) || (maxRow - minRow == 0);
	}

	/**
	 * @return whether this contains a single cell
	 */
	public boolean isSingleCell() {
		return (maxColumn == minColumn) && (maxRow == minRow);
	}

	/**
	 * @return true if cell range is part of a row, but bigger than one cell
	 */
	public boolean isPartialRow() {
		return !isSingleCell() && !isRow() && (maxRow - minRow == 0);
	}

	/**
	 * @return true if cell range is part of a column, but bigger than one cell
	 */
	public boolean isPartialColumn() {
		return !isSingleCell() && !isColumn() && (maxColumn - minColumn == 0);
	}

	public TabularRange duplicate() {
		return new TabularRange(anchorColumn, anchorRow, minColumn, minRow, maxColumn, maxRow);
	}

	/**
	 * @param location point (column, row)
	 * @return whether given point is part of this range
	 */
	public boolean contains(GPoint location) {
		if (location != null
				&& location.x < Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP
				&& location.y < Kernel.MAX_SPREADSHEET_ROWS_DESKTOP) {
			return contains(location.y, location.x);
		}
		return false;
	}

	/**
	 * @param row wow
	 * @param column column
	 * @return Whether this range contains given row and column
	 */
	public boolean contains(int row, int column) {
		return (row >= minRow && row <= maxRow || minRow == -1)
				&& (column >= minColumn && column <= maxColumn || minColumn == -1);
	}

	/**
	 * ArrayList of all cells found in the cell range
	 *
	 * @param scanByColumn
	 *            whether to sort by column
	 * @return list of all coords in the range
	 */
	public ArrayList<GPoint> toCellList(boolean scanByColumn) {

		ArrayList<GPoint> list = new ArrayList<>();
		if (scanByColumn) {
			for (int col = minColumn; col <= maxColumn; ++col) {
				for (int row = minRow; row <= maxRow; ++row) {
					list.add(new GPoint(col, row));
				}
			}
		} else {
			for (int row = minRow; row <= maxRow; ++row) {
				for (int col = minColumn; col <= maxColumn; ++col) {
					list.add(new GPoint(col, row));
				}
			}
		}

		return list;
	}

	/** @return true if this range contains no cells */
	public boolean isEmptyRange() {
		return minColumn == -1 && maxColumn == -1 && minRow == -1
				&& maxRow == -1;
	}

	/**
	 * @return true if the cell range has valid coordinates for this table
	 */
	public boolean isValid() {
		return (minRow >= -1 && minRow < Kernel.MAX_SPREADSHEET_ROWS_DESKTOP)
				&& (maxRow >= -1
				&& maxRow < Kernel.MAX_SPREADSHEET_ROWS_DESKTOP)
				&& (minColumn >= -1
				&& minColumn < Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP)
				&& (maxColumn >= -1
				&& maxColumn < Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP);
	}

	/**
	 * @param cr
	 *            other range
	 * @return whether this has same anchor coords as other range
	 */
	public boolean hasSameAnchor(TabularRange cr) {
		return (cr.anchorRow == anchorRow) && (cr.anchorColumn == anchorColumn);
	}

	/**
	 * @return list of single column ranges that cover this range
	 */
	public ArrayList<TabularRange> toPartialColumnList() {
		ArrayList<TabularRange> list = new ArrayList<>();

		if (isColumn()) {
			for (int col = minColumn; col <= maxColumn; col++) {
				TabularRange tr = new TabularRange(col, -1, col, 0, col, maxRow);
				list.add(tr);
			}
		} else {
			for (int col = minColumn; col <= maxColumn; col++) {
				list.add(new TabularRange(col, minRow, col, maxRow));
			}
		}

		return list;
	}

	/**
	 * @return list of single row ranges that cover this range
	 */
	public ArrayList<TabularRange> toPartialRowList() {
		ArrayList<TabularRange> list = new ArrayList<>();

		if (isRow()) {
			for (int row = minRow; row <= maxRow; row++) {
				list.add(new TabularRange(0, row, -1, row, maxColumn, row));
			}
		} else {
			for (int row = minRow; row <= maxRow; row++) {
				list.add(new TabularRange(minColumn, row, maxColumn, row));
			}
		}
		return list;
	}

	public static TabularRange range(int fromRow, int toRow, int fromCol, int toCol) {
		return new TabularRange(fromCol, fromRow, toCol, toRow);
	}

	/**
	 * @param range other range
	 * @return new range if this and the other range could be merged, null otherwise
	 */
	public @CheckForNull TabularRange merge(TabularRange range) {
		if (minColumn == range.minColumn && maxColumn == range.maxColumn) {
			if ((range.minRow >= minRow && range.minRow <= maxRow + 1)
					|| (minRow >= range.minRow && minRow <= range.maxRow + 1)) {
				return TabularRange.range(Math.min(minRow, range.minRow),
						Math.max(maxRow, range.maxRow), minColumn, maxColumn);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "(" + minRow + "," + minColumn + ") to (" + maxRow + "," + maxColumn + ")";
	}

	public int getFromRow() {
		return anchorRow;
	}

	public int getFromColumn() {
		return anchorColumn;
	}

	public int getToRow() {
		return anchorRow == minRow ? maxRow : minRow;
	}

	public int getToColumn() {
		return anchorColumn == minColumn ? maxColumn : minColumn;
	}

	/**
	 * For finite ranges returns self. For infinite ranges returns
	 * a range restricted to given number of rows/columns.
	 * @param rowCount maximum row
	 * @param columnCount maximum column
	 * @return restricted range
	 */
	public TabularRange restrictTo(int rowCount, int columnCount) {
		if (this.getMinRow() == -1 && this.getMaxRow() == -1
				&& this.getMinColumn() == -1 && this.getMaxColumn() == -1) {
			return this;
		}

		if (this.getMinRow() == -1) {
			return new TabularRange(this.getMinColumn(), 0,
					this.getMaxColumn(), rowCount - 1);
		}

		if (this.getMinColumn() == -1) {
			return new TabularRange(0, this.getMinRow(),
					columnCount - 1, this.getMaxRow());
		}
		return this;
	}
}
