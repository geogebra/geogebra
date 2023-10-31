package org.geogebra.common.spreadsheet.core;

/**
 * Represents a row x column range of tabular data with absolute and inclusive end cells.
 * For example TabularRange(2, 1, 5, 7) means an 3x6 sized area
 * begins from row 2 column 1 and ends at row 5 column 7
 */
public final class TabularRange {

	final int fromRow;
	final int toRow;
	final int fromCol;
	final int toCol;

	/**
	 * @param fromRow first row
	 * @param fromCol first column
	 * @param toRow last row
	 * @param toCol last column
	 */
	public TabularRange(int fromRow, int fromCol, int toRow, int toCol) {
		this.fromRow = fromRow;
		this.toRow = toRow;
		this.fromCol = fromCol;
		this.toCol = toCol;
	}

	public boolean isEmpty() {
		return (toRow - fromRow <= 0 && toRow >= 0) || (toCol - fromCol <= 0 && toCol >= 0);
	}

	/**
	 * Merges a range to this if they are not disjunct.
	 *
	 * @param range to merge
	 * @return the merged range if it exists, null otherwise
	 */
	TabularRange merge(TabularRange range) {
		if (fromCol == range.fromCol && toCol == range.toCol) {
			if ((range.fromRow >= fromRow && range.fromRow <= toRow + 1)
					|| (fromRow >= range.fromRow && fromRow <= range.toRow + 1)) {
				return new TabularRange(Math.min(fromRow, range.fromRow),
						fromCol, Math.max(toRow, range.toRow), toCol);
			}
		}
		return null;
	}

	/**
	 * Run action for each (row, column) pair of the range.
	 * @param action to run for each (row, column).
	 */
	public void forEach(RangeAction action) {
		for (int row = fromRow; row <= toRow; row++) {
			for (int column = fromCol; column <= toCol; column++) {
				action.run(row, column);
			}
		}
	}
}
