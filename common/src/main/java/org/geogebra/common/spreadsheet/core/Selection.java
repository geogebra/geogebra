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

	boolean isEmpty() {
		return range.isEmptyRange();
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

	/**
	 * If Rows are selected then the leftmost Column with a single cell is selected
	 * @param extendSelection Whether we want to extend the current Selection
	 * @return Selection to the left of the selection calling this method if possible
	 */
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
	 * If Rows are selected then the second leftmost Column with a single cell is selected
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

		int rightColumnIndex = Math.min(range.getToColumn() + 1, numberOfColumns - 1);
		return new Selection(TabularRange.range(range.getFromRow(), range.getToRow(),
				rightColumnIndex, rightColumnIndex));
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
	 * @param newSelection new Selection
	 * @return Resulting selection
	 */
	public Selection getExtendedSelection(Selection newSelection) {
		SelectionType selectionType = this.getSelectionTypeForExtension(newSelection);

		if ((selectionType != SelectionType.CELLS && this.type != newSelection.type)
				|| selectionType == SelectionType.ALL) {
			return new Selection(new TabularRange(
					Math.min(this.range.getMinRow(), newSelection.range.getMinRow()),
					Math.max(this.range.getMaxRow(), newSelection.range.getMaxRow()),
					Math.min(this.range.getMinColumn(), newSelection.range.getMinColumn()),
					Math.max(this.range.getMaxColumn(), newSelection.range.getMaxColumn())));
		}

		return new Selection(TabularRange.range(
				this.range.getFromRow(), newSelection.range.getToRow(),
				this.range.getFromColumn(), newSelection.range.getToColumn()));
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
