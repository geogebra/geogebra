package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.List;

/**
 * A contiguous range of cells in a {@link Spreadsheet}.
 *
 * @apiNote this class is immutable
 */
// TODO testing: Since this contains a lot of tricky logic, this should be directly unit-tested
//  (not just indirectly via SpreadsheetController)
//  - see also my comment on SpreadsheetSelectionController.
//
final class Selection {

	private final TabularRange range;
	private final SelectionType type;

	Selection(TabularRange range) {
		this.range = range;
		if (range.getMinRow() == -1 && range.getMinColumn() == -1) {
			this.type = SelectionType.ALL;
		} else if (range.getMinRow() == -1) {
			this.type = SelectionType.COLUMNS;
		} else if (range.getMinColumn() == -1) {
			this.type = SelectionType.ROWS;
		} else {
			this.type = SelectionType.CELLS;
		}
	}

	/**
	 * @param rowIndex Row Index
	 * @param columnIndex Column Index
	 * @return A single cell with given index
	 */
	static Selection getSingleCellSelection(int rowIndex, int columnIndex) {
		return new Selection(TabularRange.range(
				rowIndex, rowIndex, columnIndex, columnIndex));
	}

	boolean isEmpty() {
		return range.isEmptyRange();
	}

	/**
	 * // TODO documentation: Please add a method description. It's not clear what
	 * //  "merge" exactly means here, and when this merge may return null.
	 * @param other other selection
	 * @return bigger selection if this could be merged, null otherwise
	 */
	// TODO naming: `selection.merge(other)` sounds like it would modify either
	//  this (the current selection) or other.
	//  Maybe `selection.mergedWith(other)`?
	Selection merge(Selection other) {
		if (type != other.type) {
			return null;
		}
		TabularRange mergedRange = range.merge(other.range);
		return mergedRange == null ? null : new Selection(mergedRange);
	}

	TabularRange getRange() {
		return range;
	}

	SelectionType getType() {
		return type;
	}

	boolean contains(int row, int column) {
		return range.contains(row, column);
	}

	/**
	 * Takes the anchor cell of this selection and returns
	 * - the cell to the left of it if possible
	 * - the cell itself if it is in the first column
	 * Examples: for rectangular range C2:D3 yields B2, for column selection C:D yields B1,
	 * for row selection 2:3 yields A2
	 * @return Selection to the left of this
	 */
	Selection getLeftNeighborCell() {
		if (type == SelectionType.ROWS) {
			return getSingleCellSelection(range.getFromRow(), 0);
		} else {
			return getSingleCellSelection(Math.max(range.getFromRow(), 0),
					Math.max(range.getFromColumn() - 1, 0));
		}
	}

	/**
	 * Range spanned by this selection's anchor cell and an end cell that was shifted to the left.
	 * - for rectangular range C2:D3 that can be either B2:D3 (if anchor is in D)
	 *   or C2:C2 (if anchor is in C)
	 * - for column selection C:D yields either B:D or D:D
	 * - for row selection 2:3 yields the selection unchanged
	 * @return Selection extended to the left
	 */
	Selection getLeftExtension() {
		if (type == SelectionType.ROWS) {
			return this;
		}
		int leftColumnIndex = Math.max(range.getToColumn() - 1, 0);
		return new Selection(TabularRange.range(range.getFromRow(), range.getToRow(),
				leftColumnIndex, leftColumnIndex));
	}

	/**
	 * If Rows are selected then, returns the second cell from the left in the first selected row.
	 * @param numberOfColumns Number of columns
	 * @param extendSelection Whether we want to extend the current Selection
	 * @return Selection to the right of the selection calling this method if possible
	 */
	Selection getRight(int numberOfColumns, boolean extendSelection) {
		if (type == SelectionType.ROWS) {
			return getSingleCellSelection(range.getFromRow(), 1);
		} else if (!extendSelection) {
			return getSingleCellSelection(range.getFromRow(),
					Math.min(range.getFromColumn() + 1, numberOfColumns - 1));
		}
		int columnIndex = Math.min(range.getToColumn() + 1, numberOfColumns - 1);
		return new Selection(TabularRange.range(range.getFromRow(), range.getToRow(),
				columnIndex, columnIndex));
	}

	/**
	 * If Columns are selected then the topmost Row with a single cell is selected
	 * @param extendSelection Whether we want to extend the current Selection
	 * @return Selection on top of the selection calling this method if possible
	 */
	Selection getTop(boolean extendSelection) {
		if (type == SelectionType.COLUMNS) {
			return getSingleCellSelection(0, range.getFromColumn());
		} else if (!extendSelection) {
			return getSingleCellSelection(Math.max(range.getFromRow() - 1, 0),
					range.getFromColumn());
		}

		int aboveRowIndex = Math.max(this.range.getToRow() - 1, 0);
		return new Selection(TabularRange.range(aboveRowIndex, aboveRowIndex,
				range.getFromColumn(), range.getToColumn()));
	}

	/**
	 * If Columns are selected then the second topmost Row with a single cell is selected
	 * @param numberOfRows Number of rows
	 * @param extendSelection Whether we want to extend the current Selection
	 * @return Selection underneath the selection calling this method if possible
	 */
	Selection getBottom(int numberOfRows, boolean extendSelection) {
		if (type == SelectionType.COLUMNS) {
			return getSingleCellSelection(1, range.getFromColumn());
		} else if (!extendSelection) {
			return getSingleCellSelection(Math.min(range.getFromRow() + 1, numberOfRows - 1),
					range.getFromColumn());
		}

		int underneathRowIndex = Math.min(range.getToRow() + 1, numberOfRows - 1);
		return new Selection(TabularRange.range(underneathRowIndex, underneathRowIndex,
				range.getFromColumn(), range.getToColumn()));
	}

	/**
	 * Creates an extended Selection by creating a rectangle that contains the current Selection
	 * and the clicked Cell / Row / Column
	 * @param other new Selection
	 * @return Resulting selection
	 */
	Selection getExtendedSelection(Selection other) {
		SelectionType selectionType = this.getSelectionTypeForExtendingWith(other);

		if ((selectionType == SelectionType.CELLS && this.type != other.type)
				|| selectionType == SelectionType.ALL) {
			return new Selection(new TabularRange(
					Math.min(this.range.getMinRow(), other.range.getMinRow()),
					Math.max(this.range.getMaxRow(), other.range.getMaxRow()),
					Math.min(this.range.getMinColumn(), other.range.getMinColumn()),
					Math.max(this.range.getMaxColumn(), other.range.getMaxColumn())));
		}

		return new Selection(TabularRange.range(
				this.range.getFromRow(), other.range.getToRow(),
				this.range.getFromColumn(), other.range.getToColumn()));
	}

	/**
	 * If a Selection needs to be extended with another then the resulting Selection's
	 * SelectionType might change
	 * <li>If both Selections share the same SelectionType, nothing changes</li>
	 * <li>If one of the Selections' SelectionType equals {@link SelectionType#ALL}, then the
	 * resulting Selection should hold {@link SelectionType#ALL}</li>
	 * <li>Otherwise, the extended Selection is of type {@link SelectionType#CELLS}</li>
	 * @param newSelection New Selection
	 * @return Resulting SelectionType
	 */
	// TODO naming: maybe `selectionTypeForExtendingWith(other)`
	private SelectionType getSelectionTypeForExtendingWith(Selection newSelection) {
		List<SelectionType> selectionTypes = Arrays.asList(this.type, newSelection.type);
		if (this.type == newSelection.type) {
			return this.type;
		} else if (selectionTypes.contains(SelectionType.ALL)) {
			return SelectionType.ALL;
		}
		return SelectionType.CELLS;
	}

	@Override
	public int hashCode() {
		return 31 * type.hashCode() + range.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Selection)) {
			return false;
		}
		Selection other = (Selection) obj;
		return type == other.type && range.equals(other.range);
	}

}
