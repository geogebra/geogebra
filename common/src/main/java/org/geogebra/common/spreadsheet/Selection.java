package org.geogebra.common.spreadsheet;

/**
 * A contiguous range of cells in a {@link Spreadsheet}.
 *
 * @Note: toRow/toCol may be less than fromRow/fromCol, respectively.
 * If toRow is less than fromRow, the selection handle is on the upper
 * edge of the selection rectangle, and on the lower edge otherwise.
 * Similarly, if toCol is less than fromCol, the selection handle is on
 * the left edge of the selection rangle, and on the right edge otherwise.
 */
final class Selection {

	final int fromRow;
	final int toRow;
	final int fromCol;
	final int toCol;

	Selection(int fromRow, int toRow, int fromCol, int toCol) {
		this.fromRow = fromRow;
		this.toRow = toRow;
		this.fromCol = fromCol;
		this.toCol = toCol;
	}

	boolean isEmpty() {
		return toRow - fromRow <= 0 || toCol - fromCol <= 0;
	}
}
