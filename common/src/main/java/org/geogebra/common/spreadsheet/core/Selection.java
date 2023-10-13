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
	 * If Rows are selected then the leftmost Column is returned
	 * @return Selection to the left of the selection calling this method if possible
	 */
	public Selection getLeft() {
		if (this.type == SelectionType.ROWS) {
			return this.getSingleColumnOfCells(0);
		}

		int leftColumnIndex = Math.max(this.range.toCol - 1, 0);
		return new Selection(this.type, new TabularRange(
				this.range.fromRow, this.range.toRow, leftColumnIndex, leftColumnIndex));
	}

	/**
	 * If Rows are selected then the second leftmost Column is returned
	 * @param numberOfColumns Number of columns
	 * @return Selection to the right of the selection calling this method if possible
	 */
	public Selection getRight(int numberOfColumns) {
		if (this.type == SelectionType.ROWS) {
			return this.getSingleColumnOfCells(1);
		}

		int rightColumnIndex = Math.min(this.range.toCol + 1, numberOfColumns - 2);
		return new Selection(this.type, new TabularRange(
				this.range.fromRow, this.range.toRow, rightColumnIndex, rightColumnIndex));
	}

	/**
	 * If Columns are selected then the topmost Row is returned
	 * @return Selection on top of the selection calling this method if possible
	 */
	public Selection getTop() {
		// Select topmost row if we currently have Columns selected
		if (this.type == SelectionType.COLUMNS) {
			return this.getSingleRowOfCells(0);
		}

		int aboveRowIndex = Math.max(this.range.toRow - 1, 0);
		return new Selection(this.type, new TabularRange(
				aboveRowIndex, aboveRowIndex, this.range.fromCol, this.range.toCol));
	}

	/**
	 * If Columns are selected then the second topmost Row is returned
	 * @param numberOfRows Number of rows
	 * @return Selection underneath the selection calling this method if possible
	 */
	public Selection getBottom(int numberOfRows) {
		if (this.type == SelectionType.COLUMNS) {
			return this.getSingleRowOfCells(1);
		}

		int underneathRowIndex = Math.min(this.range.toRow + 1, numberOfRows - 2);
		return new Selection(this.type, new TabularRange(
				underneathRowIndex, underneathRowIndex, this.range.fromCol, this.range.toCol));
	}

	/**
	 * Creates an extended Selection by creating a rectangle that contains the current Selection
	 * and the clicked Cell / Row / Column <br>
	 * By default, the new Selection is always of {@link SelectionType#CELLS} - if both Selections
	 * share the same SelectionType, e.g. ROWS & ROWS, this will be applied for the extended
	 * Selection
	 * @param newSelection new Selection
	 * @return Resulting selection
	 */
	public Selection getExtendedSelection(Selection newSelection) {
		SelectionType selectionType = SelectionType.CELLS;
		if (this.type == newSelection.type) {
			selectionType = this.type;
		}
		return new Selection(selectionType, new TabularRange(
				this.range.fromRow, newSelection.range.toRow,
				this.range.fromCol, newSelection.range.toCol));
	}

	/**
	 * @param rowIndex Index
	 * @return A single row with given index from a Selection
	 */
	private Selection getSingleRowOfCells(int rowIndex) {
		return new Selection(SelectionType.CELLS, new TabularRange(
				rowIndex, rowIndex, this.range.fromCol, this.range.toCol));
	}

	/**
	 * @param columnIndex Index
	 * @return A single Column with given index from a Selection
	 */
	private Selection getSingleColumnOfCells(int columnIndex) {
		return new Selection(SelectionType.CELLS, new TabularRange(
				this.range.fromRow, this.range.toRow, columnIndex, columnIndex));
	}
}
