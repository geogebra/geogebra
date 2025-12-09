/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * A finite (bounded along both axes), semi-finite (unbounded along one axis),
 * or infinite (unbounded along both axes) rectangular range.
 * <p>
 * Note: End indexes (for finite ranges) are inclusive!
 * </p>
 */
// TODO This needs more documentation.
//  - What are anchor column/rows?
//  - What are the invariants for all indexes?
//  - What are the possible cases (combinations of -1 and >= 0 values) for min/max rows/columns?
//  - For example, the conditions in isColumn() is not clear:
//    > return (anchorRow == -1 || minRow == -1) && anchorColumn != -1;
//    Why are anchorRow and minRow checked, but not maxRow?
//    Why is anchorColumn checked, but neither minColumn nor maxColumn?
public final class TabularRange {
	private final int anchorColumn;
	private final int anchorRow;
	private final int minColumn;
	private final int minRow;
	private final int maxColumn;
	private final int maxRow;

	/**
	 * @param anchorRow anchor row
	 * @param anchorColumn anchor column
	 * @param minRow lowest row
	 * @param minColumn lowest column
	 * @param maxRow highest row
	 * @param maxColumn highest column
	 */
	public TabularRange(int anchorRow, int anchorColumn, int minRow, int minColumn,
			int maxRow, int maxColumn) {
		this.anchorColumn = anchorColumn;
		this.anchorRow = anchorRow;
		this.minColumn = minColumn;
		this.minRow = minRow;
		this.maxColumn = maxColumn;
		this.maxRow = maxRow;
	}

	/**
	 * @param anchorRow anchor row
	 * @param anchorColumn anchor column
	 * @param endRow end row
	 * @param endCol end column
	 */
	public TabularRange(int anchorRow, int anchorColumn, int endRow, int endCol) {
		minColumn = Math.min(anchorColumn, endCol);
		maxColumn = Math.max(anchorColumn, endCol);
		minRow = Math.min(anchorRow, endRow);
		maxRow = Math.max(anchorRow, endRow);

		this.anchorColumn = anchorColumn;
		this.anchorRow = anchorRow;
	}

	public TabularRange(int anchorRow, int anchorColumn) {
		this(anchorRow, anchorColumn, anchorRow, anchorColumn);
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

	/**
	 * @return whether this range consists of one or more columns
	 */
	public boolean isContiguousColumns() {
		return (anchorRow == -1 || minRow == -1) && anchorColumn != -1;
	}

	/**
	 * @return whether this range consists of one or more rows
	 */
	public boolean isContiguousRows() {
		return (anchorColumn == -1 || minColumn == -1) && anchorRow != -1;
	}

	/**
	 * @return whether this range contains all spreadsheet cells
	 */
	public boolean areAllCellsSelected() {
		return minRow == -1 && minColumn == -1;
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
		return (maxColumn == minColumn) && (maxRow == minRow) && minRow != -1 && minColumn != -1;
	}

	/**
	 * @return Whether this contains a single row
	 */
	public boolean isSingleRow() {
		return minRow == maxRow && isContiguousRows();
	}

	/**
	 * @return Whether this contains a single column
	 */
	public boolean isSingleColumn() {
		return minColumn == maxColumn && isContiguousColumns();
	}

	/**
	 * @return true if cell range is part of a row, but bigger than one cell
	 */
	public boolean isPartialRow() {
		return !isSingleCell() && !isContiguousRows() && (maxRow - minRow == 0);
	}

	/**
	 * @return true if cell range is part of a column, but bigger than one cell
	 */
	public boolean isPartialColumn() {
		return !isSingleCell() && !isContiguousColumns() && (maxColumn - minColumn == 0);
	}

	/**
	 * @return true if cell range is entire column/s
	 */
	public boolean isEntireColumn() {
		return minRow == -1 && maxRow == -1;
	}

	/**
	 * @return true if cell range is entire row/s
	 */
	public boolean isEntireRow() {
		return minColumn == -1 && maxColumn == -1;
	}

	/**
	 * Creates a copy of this range.
	 * @return copy of this range
	 */
	public TabularRange duplicate() {
		return new TabularRange(anchorRow, anchorColumn, minRow, minColumn, maxRow, maxColumn);
	}

	/**
	 * row/column pairs
	 * @param location point (column, row)
	 * @return whether given point is part of this range
	 */
	public boolean contains(SpreadsheetCoords location) {
		if (location != null
				&& location.column < Spreadsheet.MAX_COLUMNS
				&& location.row < Spreadsheet.MAX_ROWS) {
			return contains(location.row, location.column);
		}
		return false;
	}

	/**
	 * @param row wow
	 * @param column column
	 * @return Whether this range contains given row and column
	 */
	public boolean contains(int row, int column) {
		return intersectsRow(row) && intersectsColumn(column);
	}

	/**
	 * Check intersection with a column, always true for row ranges.
	 * @param column column index
	 * @return whether this range intersects a given column.
	 */
	public boolean intersectsColumn(int column) {
		return column >= minColumn && column <= maxColumn || minColumn == -1;
	}

	/**
	 * Check intersection with a row, always true for column ranges.
	 * @param row row index
	 * @return whether this range intersects a given row
	 */
	public boolean intersectsRow(int row) {
		return row >= minRow && row <= maxRow || minRow == -1;
	}

	/**
	 * ArrayList of all cells found in the cell range
	 *
	 * @param scanByColumn
	 *            whether to sort by column
	 * @return list of all coords in the range
	 */
	public ArrayList<SpreadsheetCoords> toCellList(boolean scanByColumn) {

		ArrayList<SpreadsheetCoords> list = new ArrayList<>();
		if (scanByColumn) {
			for (int col = minColumn; col <= maxColumn; ++col) {
				for (int row = minRow; row <= maxRow; ++row) {
					list.add(new SpreadsheetCoords(row, col));
				}
			}
		} else {
			for (int row = minRow; row <= maxRow; ++row) {
				for (int col = minColumn; col <= maxColumn; ++col) {
					list.add(new SpreadsheetCoords(row, col));
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
		return (minRow >= -1 && minRow < Spreadsheet.MAX_ROWS)
				&& (maxRow >= -1
				&& maxRow < Spreadsheet.MAX_ROWS)
				&& (minColumn >= -1
				&& minColumn < Spreadsheet.MAX_COLUMNS)
				&& (maxColumn >= -1
				&& maxColumn < Spreadsheet.MAX_COLUMNS);
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

		if (isContiguousColumns()) {
			for (int col = minColumn; col <= maxColumn; col++) {
				TabularRange tr = new TabularRange(-1, col, 0, col, maxRow, col);
				list.add(tr);
			}
		} else {
			for (int col = minColumn; col <= maxColumn; col++) {
				list.add(new TabularRange(minRow, col, maxRow, col));
			}
		}

		return list;
	}

	/**
	 * @return list of single row ranges that cover this range
	 */
	public ArrayList<TabularRange> toPartialRowList() {
		ArrayList<TabularRange> list = new ArrayList<>();

		if (isContiguousRows()) {
			for (int row = minRow; row <= maxRow; row++) {
				list.add(new TabularRange(row, 0, row, -1, row, maxColumn));
			}
		} else {
			for (int row = minRow; row <= maxRow; row++) {
				list.add(new TabularRange(row, minColumn, row, maxColumn));
			}
		}
		return list;
	}

	/**
	 * Factory method, same as constructor up to the order of arguments.
	 * @return range with given bounds
	 * @deprecated use constructor instead
	 */
	@Deprecated
	public static TabularRange range(int fromRow, int toRow, int fromCol, int toCol) {
		return new TabularRange(fromRow, fromCol, toRow, toCol);
	}

	/**
	 * Merge two ranges into one if their union forms a rectangle
	 * (i.e. they overlap or share an edge)
	 * @param range other range
	 * @return new range if this and the other range could be merged, null otherwise
	 */
	public @CheckForNull TabularRange getRectangularUnion(TabularRange range) {
		if (minColumn == range.minColumn && maxColumn == range.maxColumn) {
			if ((range.minRow >= minRow && range.minRow <= maxRow + 1)
					|| (minRow >= range.minRow && minRow <= range.maxRow + 1)) {
				return TabularRange.range(Math.min(minRow, range.minRow),
						Math.max(maxRow, range.maxRow), minColumn, maxColumn);
			}
		}
		if (minRow == range.minRow && maxRow == range.maxRow) {
			if ((range.minColumn >= minColumn && range.minColumn <= maxColumn + 1)
					|| (minColumn >= range.minColumn && minColumn <= range.maxColumn + 1)) {
				return TabularRange.range(minRow, maxRow, Math.min(minColumn, range.minColumn),
						Math.max(maxColumn, range.maxColumn));
			}
		}
		return null;
	}

	/**
	 * Run action for each (row, column) pair of the range.
	 * @param action to run for each (row, column).
	 */
	public void forEach(@Nonnull TabularRangeAction action) {
		for (int row = getMinRow(); row <= getMaxRow() ; row++) {
			for (int column = getMinColumn(); column <= getMaxColumn(); column++) {
				action.run(row, column);
			}
		}
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
		TabularRange ret = this;

		if (ret.getMinRow() == -1) {
			ret = new TabularRange(0, ret.getMinColumn(),
					rowCount - 1, ret.getMaxColumn());
		}

		if (ret.getMinColumn() == -1) {
			ret = new TabularRange(ret.getMinRow(), 0,
					ret.getMaxRow(), columnCount - 1);
		}
		return ret;
	}

	@Override
	public String toString() {
		return "(" + minRow + "," + minColumn + ") to (" + maxRow + "," + maxColumn + ")";
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new int[]{minColumn, minRow, maxColumn, maxRow});
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TabularRange)) {
			return false;
		}
		TabularRange other = (TabularRange) obj;
		return minColumn == other.minColumn
				&& maxColumn == other.maxColumn
				&& minRow == other.minRow
				&& maxRow == other.maxRow;
	}
}
