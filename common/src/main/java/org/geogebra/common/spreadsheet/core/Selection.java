package org.geogebra.common.spreadsheet.core;

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

	private final TabularRange range;
	private final SelectionType type;

	Selection(SelectionType type, TabularRange range) {
		this.range = range;
		this.type = type;
	}

	boolean isEmpty() {
		return range.isEmpty();
	}

	/**
	 * @param selection other selection
	 * @return bigger selection if this could be merged, null otherwise
	 */
	public Selection merge(Selection selection) {
		if (type != selection.type) {
			return null;
		}
		TabularRange mergedRange = range.merge(selection.range);
		return mergedRange == null ? null : new Selection(type, mergedRange);
	}

	public TabularRange getRange() {
		return range;
	}

	public boolean contains(int row, int column) {
		return (range.fromCol <= column && range.toCol >= column || range.fromCol < 0)
				&& (range.fromRow <= row && range.toRow >= row || range.fromRow < 0);
	}

	/**
	 * @return Selection to the left of the selection calling this method if possible
	 */
	public Selection getLeft() {
		int leftColumnIndex = Math.max(this.range.toCol - 1, 0);
		return new Selection(SelectionType.CELLS, new TabularRange(
				this.range.fromRow, this.range.toRow, leftColumnIndex, leftColumnIndex));
	}

	/**
	 * @param numberOfColumns Number of columns
	 * @return Selection to the right of the selection calling this method if possible
	 */
	public Selection getRight(int numberOfColumns) {
		int rightColumnIndex = Math.min(this.range.toCol + 1, numberOfColumns);
		return new Selection(SelectionType.CELLS, new TabularRange(
				this.range.fromRow, this.range.toRow, rightColumnIndex, rightColumnIndex));
	}

	/**
	 * @return Selection on top of the selection calling this method if possible
	 */
	public Selection getTop() {
		int aboveRowIndex = Math.max(this.range.toRow - 1, 0);
		return new Selection(SelectionType.CELLS, new TabularRange(
				aboveRowIndex, aboveRowIndex, this.range.fromCol, this.range.toCol));
	}

	/**
	 * @param numberOfRows Number of rows
	 * @return Selection underneath the selection calling this method if possible
	 */
	public Selection getBottom(int numberOfRows) {
		int underneathRowIndex = Math.min(this.range.toRow + 1, numberOfRows);
		return new Selection(SelectionType.CELLS, new TabularRange(
				underneathRowIndex, underneathRowIndex, this.range.fromCol, this.range.toCol));
	}

	/**
	 * Creates an extended selection by creating a rectangle that contains the current selection and
	 * the clicked cell / row / column
	 * @param newSelection new Selection
	 * @return Resulting selection
	 */
	public Selection getExtendedSelection(Selection newSelection) {
		return new Selection(SelectionType.CELLS, new TabularRange(
				this.range.fromRow, newSelection.range.toRow,
				this.range.fromCol, newSelection.range.toCol));
	}
}
