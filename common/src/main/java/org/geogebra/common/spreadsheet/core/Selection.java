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
// TODO: remove all public method modifiers (the class itself is package private, so members cannot
//  be more visible than the class)
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
	public static Selection getSingleCellSelection(int rowIndex, int columnIndex) {
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
	public Selection merge(Selection other) {
		if (type != other.type) {
			return null;
		}
		TabularRange mergedRange = range.merge(other.range);
		return mergedRange == null ? null : new Selection(mergedRange);
	}

	public TabularRange getRange() {
		return range;
	}

	public SelectionType getType() {
		return type;
	}

	public boolean contains(int row, int column) {
		return range.contains(row, column);
	}

	// TODO documentation: This set of four methods (getLeft, etc) is very hard to understand
	//  from the documentation, and even from the code.
	//  I failed ot build a mental model even after looking at the code for a longer time.
	//  We should explain what this is in more detail. Maybe by adding pictures (ASCII art),
	//  or referencing Google sheets (e.g., a list of steps that a reader can perform to understand
	//  what this does).
	/**
	 * If Rows are selected then the leftmost Column with a single cell is selected
	 //  A picture would be very helpful. I wouldn't be able to fix a bug in here (can't build
	 //  a mental model of this, even after looking at the code for a while).
	 * @param extendSelection Whether we want to extend the current Selection
	 * // TODO extendSelection is ignored in some cases
	 * @return Selection to the left of the selection calling this method if possible
	 */
	// TODO naming: It's not clear what `selection.getLeft()` could mean.
	//  But even after looking at the implementation for a while, I don't understand
	//  what it does, so I can't suggest a better name...
	public Selection getLeft(boolean extendSelection) {
		if (type == SelectionType.ROWS) {
			return getSingleCellSelection(range.getFromRow(), 0);
		} else if (!extendSelection) {
			return getSingleCellSelection(range.getFromRow(),
					Math.max(range.getFromColumn() - 1, 0));
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
	public Selection getRight(int numberOfColumns, boolean extendSelection) {
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
	public Selection getTop(boolean extendSelection) {
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
	public Selection getBottom(int numberOfRows, boolean extendSelection) {
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
	// TODO naming: The naming is very asymmetrical compared to `merge(selection)`.
	//  Maybe `selection.extendedWith(other)`?
	public Selection getExtendedSelection(Selection other) {
		SelectionType selectionType = this.getSelectionTypeForExtension(other);

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
	private SelectionType getSelectionTypeForExtension(Selection newSelection) {
		List<SelectionType> selectionTypes = Arrays.asList(this.type, newSelection.type);
		if (this.type == newSelection.type) {
			return this.type;
		} else if (selectionTypes.contains(SelectionType.ALL)) {
			return SelectionType.ALL;
		}
		return SelectionType.CELLS;
	}


	public boolean isRowOnly() {
		return SelectionType.ROWS.equals(type);
	}

	public boolean isColumnOnly() {
		return SelectionType.COLUMNS.equals(type);
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
