package org.geogebra.common.spreadsheet.core;

import javax.annotation.CheckForNull;

/**
 * Utility class designed to handle dragging a selection in order to copy its content to adjacent
 * cells
 */
public class CellDragPasteHandler {

	private final TabularRange rangeToCopy;
	private final TabularData<?> tabularData;
	private final SpreadsheetCellEditor editor;
	private int fromRow;
	private int toRow;
	private int fromColumn;
	private int toColumn;

	/**
	 * @param rangeToCopy The original range that should be copied to adjacent cells
	 * @param tabularData {@link TabularData}
	 * @param editor {@link SpreadsheetCellEditor}
	 */
	public CellDragPasteHandler(TabularRange rangeToCopy, TabularData tabularData,
			SpreadsheetCellEditor editor) {
		this.rangeToCopy = rangeToCopy;
		this.tabularData = tabularData;
		this.editor = editor;
		resetRowIndexes();
		resetColumnIndexes();
	}

	/**
	 * @return The selected range used to copy a selection to
	 */
	public @CheckForNull TabularRange getDestinationRange() {
		if (destinationRowIsWithinOriginalSelection()
				&& destinationColumnIsWithinOriginalSelection()) {
			return null;
		}
		return TabularRange.range(fromRow, toRow, fromColumn, toColumn);
	}

	/**
	 * Sets the destination where the original selection should be copied to
	 * @param destinationRow Row index
	 * @param destinationColumn Column index
	 */
	public void setDestinationForPaste(int destinationRow, int destinationColumn) {
		if (rangeToCopy.contains(destinationRow, destinationColumn)) {
			resetRowIndexes();
			resetColumnIndexes();
			return;
		}
		if (destinationShouldExtendVertically(destinationRow)) {
			extendDestinationVertically(destinationRow);
		} else {
			extendDestinationHorizontally(destinationColumn);
		}
	}

	/**
	 * Pastes the selected range to the chosen destination
	 */
	public void pasteToDestination() {
		if (getDestinationRange() == null) {
			return;
		}
		if (destinationIsRightOrDown()) {
			pasteRightwardsOrDownwards();
		} else {
			pasteLeftwardsOrUpwards();
		}
	}

	/**
	 * @param destinationRow Destination row index
	 * @return True if the destination paste range should be extended vertically, false else
	 * (horizontally)
	 */
	public boolean destinationShouldExtendVertically(int destinationRow) {
		return destinationRow < getMinRowIndexFromOrigin()
				|| destinationRow > getMaxRowIndexFromOrigin();
	}

	private boolean destinationIsRightOrDown() {
		return fromRow > getMaxRowIndexFromOrigin() || fromColumn > getMaxColumnIndexFromOrigin();
	}

	private void pasteRightwardsOrDownwards() {
		TabularRange destinationRange = getDestinationRange();
		int minOriginRow = getMinRowIndexFromOrigin();
		int minOriginColumn = getMinColumnIndexFromOrigin();

		for (int row = 0; row < destinationRange.getHeight(); row++) {
			for (int column = 0; column < destinationRange.getWidth(); column++) {
				pasteSingleCell(minOriginRow + row, fromRow + row,
						minOriginColumn + column, fromColumn + column);
			}
		}
	}

	private void pasteLeftwardsOrUpwards() {
		TabularRange destinationRange = getDestinationRange();
		int maxOriginRow = getMaxRowIndexFromOrigin();
		int maxOriginColumn = getMaxColumnIndexFromOrigin();

		for (int row = 0; row < destinationRange.getHeight(); row++) {
			for (int column = 0; column < destinationRange.getWidth(); column++) {
				pasteSingleCell(maxOriginRow - row, toRow - row,
						maxOriginColumn - column, toColumn - column);

			}
		}
	}

	private void pasteSingleCell(int sourceRow, int destinationRow,
			int sourceColumn, int destinationColumn) {
		editor.setTargetCell(destinationRow, destinationColumn);
		editor.setContent(tabularData.contentAt(sourceRow, sourceColumn));
		editor.onEnter();
	}

	private int getMinRowIndexFromOrigin() {
		if (rangeToCopy.isColumn()) {
			return 0;
		}
		return rangeToCopy.getMinRow();
	}

	private int getMaxRowIndexFromOrigin() {
		if (rangeToCopy.isColumn()) {
			return tabularData.numberOfRows() - 1;
		}
		return rangeToCopy.getMaxRow();
	}

	private int getMinColumnIndexFromOrigin() {
		if (rangeToCopy.isRow()) {
			return 0;
		}
		return rangeToCopy.getMinColumn();
	}

	private int getMaxColumnIndexFromOrigin() {
		if (rangeToCopy.isRow()) {
			return tabularData.numberOfColumns() - 1;
		}
		return rangeToCopy.getMaxColumn();
	}

	private void extendDestinationVertically(int destinationRow) {
		if (destinationRow > getMaxRowIndexFromOrigin()) {
			fromRow = getMaxRowIndexFromOrigin() + 1;
			toRow = destinationRow;
		} else if (destinationRow < getMinRowIndexFromOrigin()) {
			fromRow = destinationRow;
			toRow = getMinRowIndexFromOrigin() - 1;
		}
		resetColumnIndexes();
	}

	private void resetColumnIndexes() {
		fromColumn = getMinColumnIndexFromOrigin();
		toColumn = getMaxColumnIndexFromOrigin();
	}

	private void extendDestinationHorizontally(int destinationColumn) {
		if (destinationColumn > getMaxColumnIndexFromOrigin()) {
			fromColumn = getMaxColumnIndexFromOrigin() + 1;
			toColumn = destinationColumn;
		} else if (destinationColumn < getMinColumnIndexFromOrigin()) {
			fromColumn = destinationColumn;
			toColumn = getMinColumnIndexFromOrigin() - 1;
		}
		resetRowIndexes();
	}

	private void resetRowIndexes() {
		fromRow = getMinRowIndexFromOrigin();
		toRow = getMaxRowIndexFromOrigin();
	}

	private boolean destinationRowIsWithinOriginalSelection() {
		return toRow >= getMinRowIndexFromOrigin() && toRow <= getMaxRowIndexFromOrigin();
	}

	private boolean destinationColumnIsWithinOriginalSelection() {
		return fromColumn >= getMinColumnIndexFromOrigin()
				&& fromColumn <= getMaxColumnIndexFromOrigin();
	}
}
