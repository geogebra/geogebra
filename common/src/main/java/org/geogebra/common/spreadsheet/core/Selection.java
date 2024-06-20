package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckForNull;

/**
 * A contiguous range of cells in a {@link Spreadsheet}.
 *
 * @apiNote this class is immutable
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
	 * Union of two selections if they are represent rectangles with one common edge,
	 * null otherwise.
	 * @param other other selection
	 * @return bigger selection if this could be merged, null otherwise
	 */
	@CheckForNull Selection getRectangularUnion(Selection other) {
		if (type != other.type) {
			return null;
		}
		TabularRange union = range.getRectangularUnion(other.range);
		return union == null ? null : new Selection(union);
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
	 * @return single cell to the left of this
	 */
	Selection getNextCellForMoveLeft() {
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
	 *   or C2:C3 (if anchor is in C)
	 * - for column selection C:D yields either B:D or D:D, depending on anchor
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
	 * Takes the anchor cell of this selection and returns
	 * - the cell to the right of it if possible
	 * - the cell itself if it is in the last column
	 * @see #getNextCellForMoveLeft()
	 * @return single cell to the right of this
	 */
	Selection getNextCellForMoveRight(int numberOfColumns) {
		if (type == SelectionType.ROWS) {
			return getSingleCellSelection(range.getFromRow(), 1);
		} else {
			return getSingleCellSelection(Math.max(range.getFromRow(), 0),
					Math.min(range.getFromColumn() + 1, numberOfColumns - 1));
		}
	}

	/**
	 * Range spanned by this selection's anchor cell and an end cell that was shifted to the right.
	 * Unlike the left extension needs to be aware of table size so that it doesn't exceed it.
	 * @see #getLeftExtension()
	 * @return Selection extended to the left
	 */
	Selection getRightExtension(int numberOfColumns) {
		if (type == SelectionType.ROWS) {
			return this;
		}
		int columnIndex = Math.min(range.getToColumn() + 1, numberOfColumns - 1);
		return new Selection(TabularRange.range(range.getFromRow(), range.getToRow(),
				columnIndex, columnIndex));
	}

	/**
	 * Takes the anchor cell of this selection and returns
	 * - the cell above of it if possible
	 * - the cell itself if it is in the first row
	 * Examples: for rectangular range C2:D3 yields C1, for column selection C:D yields C1,
	 * for row selection 2:3 yields A1
	 * @return single cell to the left of this
	 */
	Selection getNextCellForMoveUp() {
		if (type == SelectionType.COLUMNS) {
			return getSingleCellSelection(0, range.getFromColumn());
		} else {
			return getSingleCellSelection(Math.max(range.getFromRow() - 1, 0),
					Math.max(range.getFromColumn(), 0));
		}
	}

	/**
	 * Range spanned by this selection's anchor cell and an end cell that was shifted up.
	 * - for rectangular range C2:D3 that can be either C1:D3 (if anchor is in 3)
	 *   or C2:D2 (if anchor is in C)
	 * - for column selection C:D yields the selection unchanged
	 * - for row selection 2:3 yields either 1:3 or 2:2, depending on anchor
	 * @return Selection extended to the top
	 */
	Selection getTopExtension() {
		if (type == SelectionType.COLUMNS) {
			return this;
		}
		int aboveRowIndex = Math.max(this.range.getToRow() - 1, 0);
		return new Selection(TabularRange.range(aboveRowIndex, aboveRowIndex,
				range.getFromColumn(), range.getToColumn()));
	}

	/**
	 * Takes the anchor cell of this selection and returns
	 * - the cell below if possible
	 * - the cell itself if it is in the last row
	 * @see #getNextCellForMoveUp()
	 * @return single cell below of this
	 */
	Selection getNextCellForMoveDown(int numberOfRows) {
		if (type == SelectionType.COLUMNS) {
			return getSingleCellSelection(1, range.getFromColumn());
		} else {
			return getSingleCellSelection(Math.min(range.getFromRow() + 1, numberOfRows - 1),
					Math.max(range.getFromColumn(), 0));
		}
	}

	/**
	 * Range spanned by this selection's anchor cell and an end cell that was shifted down.
	 * Unlike the bottom extension needs to be aware of table size so that it doesn't exceed it.
	 * @see #getTopExtension()
	 * @return Selection extended to the bottom
	 */
	Selection getBottomExtension(int numberOfRows) {
		if (type == SelectionType.COLUMNS) {
			return this;
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

	@Override
	public String toString() {
		return range.toString();
	}

}
