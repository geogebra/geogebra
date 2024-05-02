package org.geogebra.common.spreadsheet.core;

import javax.annotation.CheckForNull;

/**
 * Utility class designed to handle dragging a selection in order to copy its content to other
 * cells
 */
public class CellDragPasteHandler {

	private final Selection selectionToCopy;
	private int fromRow;
	private int toRow;
	private int fromColumn;
	private int toColumn;

	/**
	 * @param selectionToCopy The selection that should be copied to adjacent cells
	 */
	public CellDragPasteHandler(Selection selectionToCopy) {
		this.selectionToCopy = selectionToCopy;
		resetRowIndexes();
		resetColumnIndexes();
	}

	public @CheckForNull TabularRange getDestinationSelection() {
		if (destinationRowIsWithinOriginalSelection()
				&& destinationColumnIsWithinOriginalSelection()) {
			return null;
		}
		return TabularRange.range(fromRow, toRow, fromColumn, toColumn);
	}

	public void setDestinationForPaste(int destinationRow, int destinationColumn) {
		if (destinationRow < getMinRowFromOrigin() || destinationRow > getMaxRowFromOrigin()) {
			extendDestinationVertically(destinationRow);
		} else {
			extendDestinationHorizontally(destinationColumn);
		}
	}

	public void pasteToDestination() {
		//TODO
		return;
	}

	private int getMinRowFromOrigin() {
		return selectionToCopy.getRange().getMinRow();
	}

	private int getMaxRowFromOrigin() {
		return selectionToCopy.getRange().getMaxRow();
	}

	private int getMinColumnFromOrigin() {
		return selectionToCopy.getRange().getMinColumn();
	}

	private int getMaxColumnFromOrigin() {
		return selectionToCopy.getRange().getMaxColumn();
	}

	private void extendDestinationVertically(int destinationRow) {
		if (destinationRow > getMaxRowFromOrigin()) {
			fromRow = getMaxRowFromOrigin() + 1;
		} else if (destinationRow < getMinRowFromOrigin()) {
			fromRow = getMinRowFromOrigin() - 1;
		}
		toRow = destinationRow;
		resetColumnIndexes();
	}

	private void resetColumnIndexes() {
		fromColumn = getMinColumnFromOrigin();
		toColumn = getMaxColumnFromOrigin();
	}

	private void extendDestinationHorizontally(int destinationColumn) {
		if (destinationColumn > getMaxColumnFromOrigin()) {
			fromColumn = getMaxColumnFromOrigin() + 1;
		} else if (destinationColumn < getMinColumnFromOrigin()) {
			fromColumn = getMinColumnFromOrigin() - 1;
		}
		toColumn = destinationColumn;
		resetRowIndexes();
	}

	private void resetRowIndexes() {
		fromRow = getMinRowFromOrigin();
		toRow = getMaxRowFromOrigin();
	}

	private boolean destinationRowIsWithinOriginalSelection() {
		return toRow >= getMinRowFromOrigin() && toRow <= getMaxRowFromOrigin();
	}

	private boolean destinationColumnIsWithinOriginalSelection() {
		return fromColumn >= getMinColumnFromOrigin()
				&& fromColumn <= getMaxColumnFromOrigin();
	}
}
