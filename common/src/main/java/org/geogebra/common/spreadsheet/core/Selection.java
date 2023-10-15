package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.List;

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

	public SelectionType getType() {
		return type;
	}

	public boolean contains(int row, int column) {
		return (range.fromCol <= column && range.toCol >= column || range.fromCol < 0)
				&& (range.fromRow <= row && range.toRow >= row || range.fromRow < 0);
	}

	/**
	 * If Rows are selected then the leftmost Column with a single cell is selected
	 * @param extendSelection Whether we want to extend the current Selection
	 * @return Selection to the left of the selection calling this method if possible
	 */
	public Selection getLeft(boolean extendSelection) {
		if (this.type == SelectionType.ROWS) {
			return getSingleCellSelection(this.range.toRow, 0);
		}

		int leftColumnIndex = Math.max(this.range.toCol - 1, 0);
		return new Selection(this.type, new TabularRange(
				extendSelection ? this.range.fromRow : this.range.toRow, this.range.toRow,
				leftColumnIndex, leftColumnIndex));
	}

	/**
	 * If Rows are selected then the last Column with a single cell is selected
	 * @param numberOfColumns Number of columns
	 * @param extendSelection Whether we want to extend the current Selection
	 * @return Selection to the right of the selection calling this method if possible
	 */
	public Selection getRight(int numberOfColumns, boolean extendSelection) {
		if (this.type == SelectionType.ROWS) {
			return getSingleCellSelection(this.range.toRow, numberOfColumns - 2);
		}

		int rightColumnIndex = Math.min(this.range.toCol + 1, numberOfColumns - 2);
		return new Selection(this.type, new TabularRange(
				extendSelection ? this.range.fromRow : this.range.toRow, this.range.toRow,
				rightColumnIndex, rightColumnIndex));
	}

	/**
	 * If Columns are selected then the topmost Row with a single cell is selected
	 * @param extendSelection Whether we want to extend the current Selection
	 * @return Selection on top of the selection calling this method if possible
	 */
	public Selection getTop(boolean extendSelection) {
		if (this.type == SelectionType.COLUMNS) {
			return getSingleCellSelection(0, this.range.toCol);
		}

		int aboveRowIndex = Math.max(this.range.toRow - 1, 0);
		return new Selection(this.type, new TabularRange(
				aboveRowIndex, aboveRowIndex,
				extendSelection ? this.range.fromCol : this.range.toCol, this.range.toCol));
	}

	/**
	 * If Columns are selected then the last Row with a single cell is selected
	 * @param numberOfRows Number of rows
	 * @param extendSelection Whether we want to extend the current Selection
	 * @return Selection underneath the selection calling this method if possible
	 */
	public Selection getBottom(int numberOfRows, boolean extendSelection) {
		if (this.type == SelectionType.COLUMNS) {
			return getSingleCellSelection(numberOfRows - 2, this.range.toCol);
		}

		int underneathRowIndex = Math.min(this.range.toRow + 1, numberOfRows - 2);
		return new Selection(this.type, new TabularRange(
				underneathRowIndex, underneathRowIndex,
				extendSelection ? this.range.fromCol : this.range.toCol, this.range.toCol));
	}

	/**
	 * Creates an extended Selection by creating a rectangle that contains the current Selection
	 * and the clicked Cell / Row / Column
	 * @param newSelection new Selection
	 * @return Resulting selection
	 */
	public Selection getExtendedSelection(Selection newSelection) {
		SelectionType selectionType = this.getSelectionTypeForExtension(newSelection);

		if ((selectionType == SelectionType.CELLS && this.type != newSelection.type)
				|| selectionType == SelectionType.ALL) {
			return new Selection(selectionType, new TabularRange(
					Math.min(this.range.fromRow, newSelection.range.fromRow),
					Math.max(this.range.toRow, newSelection.range.toRow),
					Math.min(this.range.fromCol, newSelection.range.fromCol),
					Math.max(this.range.toCol, newSelection.range.toCol)));
		}

		return new Selection(selectionType, new TabularRange(
				this.range.fromRow, newSelection.range.toRow,
				this.range.fromCol, newSelection.range.toCol));
	}

	/**
	 * @param rowIndex Row Index
	 * @param columnIndex Column Index
	 * @return A single cell with given index
	 */
	public static Selection getSingleCellSelection(int rowIndex, int columnIndex) {
		return new Selection(SelectionType.CELLS, new TabularRange(
				rowIndex, rowIndex, columnIndex, columnIndex));
	}

	/**
	 * If a Selection needs to be extended with another then the resulting Selection's
	 * SelectionType might change
	 * <li>If both Selections share the same SelectionType, nothing changes</li>
	 * <li>If one of the Selections' SelectionType equals {@link SelectionType#ALL}, or if both
	 * {@link SelectionType#ROWS} and {@link SelectionType#COLUMNS} occur, then the
	 * resulting Selection should hold {@link SelectionType#ALL}</li>
	 * <li>Otherwise, the extended Selection is of type {@link SelectionType#CELLS}</li>
	 * @param newSelection New Selection
	 * @return Resulting SelectionType
	 */
	private SelectionType getSelectionTypeForExtension(Selection newSelection) {
		List<SelectionType> selectionTypes = Arrays.asList(this.type, newSelection.type);
		if (this.type == newSelection.type) {
			return this.type;
		} else if (selectionTypes.contains(SelectionType.ALL)
				|| (selectionTypes.contains(SelectionType.COLUMNS)
				&& selectionTypes.contains(SelectionType.ROWS))) {
			return SelectionType.ALL;
		} else {
			return SelectionType.CELLS;
		}
	}
}
