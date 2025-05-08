package org.geogebra.common.spreadsheet.core;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * A contiguous range of cells in a {@link Spreadsheet}.
 * @apiNote Row and column indices are 0-based.
 * @apiNote This class is immutable.
 */
final class Selection {

	private final @Nonnull TabularRange range;
	private final @Nonnull SelectionType type;

	Selection(@Nonnull TabularRange range) {
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

	Selection(int rowIndex, int columnIndex) {
		this(TabularRange.range(rowIndex, rowIndex, columnIndex, columnIndex));
	}

	@Nonnull TabularRange getRange() {
		return range;
	}

	@Nonnull SelectionType getType() {
		return type;
	}

	boolean contains(int row, int column) {
		return range.contains(row, column);
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

	/**
	 * Takes the anchor cell of this selection and returns
	 * - the cell to the left of it if possible
	 * - the cell itself if it is in the first column
	 * Examples: for rectangular range C2:D3 yields B2, for column selection C:D yields B1,
	 * for row selection 2:3 yields A2
	 * @return single cell to the left of this
	 */
	@Nonnull Selection getNextCellForMoveLeft() {
		if (type == SelectionType.ROWS) {
			return new Selection(range.getFromRow(), 0);
		} else {
			return new Selection(Math.max(range.getFromRow(), 0),
					Math.max(range.getFromColumn() - 1, 0));
		}
	}

	/**
	 * Range spanned by this selection's anchor cell and an end cell that was shifted to the left.
	 * - for rectangular range C2:D3 that can be either B2:D3 (if anchor is in D)
	 * or C2:C3 (if anchor is in C)
	 * - for column selection C:D yields either B:D or D:D, depending on anchor
	 * - for row selection 2:3 yields the selection unchanged
	 * @return Selection extended to the left
	 */
	@Nonnull Selection getLeftExtension() {
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
	 * @return single cell to the right of this
	 * @see #getNextCellForMoveLeft()
	 */
	@Nonnull Selection getNextCellForMoveRight(int numberOfColumns) {
		if (type == SelectionType.ROWS) {
			return new Selection(range.getFromRow(), 1);
		} else {
			return new Selection(Math.max(range.getFromRow(), 0),
					Math.min(range.getFromColumn() + 1, numberOfColumns - 1));
		}
	}

	/**
	 * Range spanned by this selection's anchor cell and an end cell that was shifted to the right.
	 * Unlike the left extension needs to be aware of table size so that it doesn't exceed it.
	 * @return Selection extended to the left
	 * @see #getLeftExtension()
	 */
	@Nonnull Selection getRightExtension(int numberOfColumns) {
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
	@Nonnull Selection getNextCellForMoveUp() {
		if (type == SelectionType.COLUMNS) {
			return new Selection(0, range.getFromColumn());
		} else {
			return new Selection(Math.max(range.getFromRow() - 1, 0),
					Math.max(range.getFromColumn(), 0));
		}
	}

	/**
	 * Range spanned by this selection's anchor cell and an end cell that was shifted up.
	 * - for rectangular range C2:D3 that can be either C1:D3 (if anchor is in 3)
	 * or C2:D2 (if anchor is in C)
	 * - for column selection C:D yields the selection unchanged
	 * - for row selection 2:3 yields either 1:3 or 2:2, depending on anchor
	 * @return Selection extended to the top
	 */
	@Nonnull Selection getTopExtension() {
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
	 * @return single cell below of this
	 * @see #getNextCellForMoveUp()
	 */
	@Nonnull Selection getNextCellForMoveDown(int numberOfRows) {
		if (type == SelectionType.COLUMNS) {
			return new Selection(1, range.getFromColumn());
		} else {
			return new Selection(Math.min(range.getFromRow() + 1, numberOfRows - 1),
					Math.max(range.getFromColumn(), 0));
		}
	}

	/**
	 * Range spanned by this selection's anchor cell and an end cell that was shifted down.
	 * Unlike the bottom extension needs to be aware of table size so that it doesn't exceed it.
	 * @return Selection extended to the bottom
	 * @see #getTopExtension()
	 */
	@Nonnull Selection getBottomExtension(int numberOfRows) {
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
	@Nonnull Selection getExtendedSelection(Selection other) {
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
	@Nonnull private SelectionType getSelectionTypeForExtendingWith(Selection newSelection) {
		List<SelectionType> selectionTypes = List.of(this.type, newSelection.type);
		if (this.type == newSelection.type) {
			return this.type;
		} else if (selectionTypes.contains(SelectionType.ALL)) {
			return SelectionType.ALL;
		}
		return SelectionType.CELLS;
	}

	/**
	 * @param tabularData data layer
	 * @return Name of this selection, depending on naming of columns in the data.
	 */
	@Nonnull String getName(TabularData<?> tabularData) {
		String startCell = tabularData.getCellName(range.getMinRow(), range.getMinColumn());
		if (range.isSingleCell()) {
			return startCell;
		}
		return startCell + ":" + tabularData.getCellName(range.getMaxRow(), range.getMaxColumn());
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
