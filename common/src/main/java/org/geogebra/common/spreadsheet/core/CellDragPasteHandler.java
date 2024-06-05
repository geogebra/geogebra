package org.geogebra.common.spreadsheet.core;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.util.debug.Log;

/**
 * Utility class designed to handle dragging a selection in order to copy its content to adjacent
 * cells
 */
public class CellDragPasteHandler {

	private @CheckForNull TabularRange rangeToCopy;
	private final TabularData<?> tabularData;
	private int fromRow;
	private int toRow;
	private int fromColumn;
	private int toColumn;
	private final RelativeCopy relativeCopy;

	/**
	 * @param tabularData {@link TabularData}
	 * @param kernel {@link Kernel} - Needed for {@link RelativeCopy}
	 */
	public CellDragPasteHandler(TabularData tabularData, Kernel kernel) {
		this.tabularData = tabularData;
		this.relativeCopy = new RelativeCopy(kernel);
	}

	/**
	 * Specifies the range that should be copied
	 * @param rangeToCopy {@link TabularRange}
	 */
	public void setRangeToCopy(TabularRange rangeToCopy) {
		this.rangeToCopy = rangeToCopy;
		if (rangeToCopy != null) {
			resetRowIndexes();
			resetColumnIndexes();
		}
	}

	/**
	 * @return Whether a range that should be copied is set currently
	 */
	public boolean hasSelectedRange() {
		return this.rangeToCopy != null;
	}

	/**
	 * @return The selected range used to copy a selection to
	 */
	public @CheckForNull TabularRange getDestinationRange() {
		if (rangeToCopy == null || (destinationRowIsWithinOriginalSelection()
				&& destinationColumnIsWithinOriginalSelection())) {
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
		if (rangeToCopy == null) {
			return;
		}
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
		pasteToCorrectDirection();
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

	private void pasteToCorrectDirection() {
		if (destinationShouldExtendVertically(toRow)) {
			if (destinationIsRightOrDown()) {
				pasteDownwards(getDestinationRange(),
						getMinRowIndexFromOrigin(), getMinColumnIndexFromOrigin());
			} else {
				pasteUpwards(getDestinationRange(),
						getMaxRowIndexFromOrigin(), getMaxColumnIndexFromOrigin());
			}
		} else {
			if (destinationIsRightOrDown()) {
				pasteRightwards(getDestinationRange(),
						getMinRowIndexFromOrigin(), getMinColumnIndexFromOrigin());
			} else {
				pasteLeftwards(getDestinationRange(),
						getMaxRowIndexFromOrigin(), getMaxColumnIndexFromOrigin());
			}
		}
	}

	private boolean destinationIsRightOrDown() {
		return fromRow > getMaxRowIndexFromOrigin() || fromColumn > getMaxColumnIndexFromOrigin();
	}

	private void pasteUpwards(TabularRange destinationRange,
			int maxOriginRow, int maxOriginColumn) {
		if (destinationRange == null) {
			return;
		}
		int column = 0;
		do {
			for (int row = 0; row < destinationRange.getHeight(); row++) {
				pasteSingleRowOrColumn(maxOriginRow - row, toRow - row,
						maxOriginColumn - column, toColumn - column);
			}
			column++;
		} while (column < destinationRange.getWidth());
	}

	private void pasteRightwards(TabularRange destinationRange,
			int minOriginRow, int minOriginColumn) {
		if (destinationRange == null) {
			return;
		}
		int row = 0;
		do {
			for (int column = 0; column < destinationRange.getWidth(); column++) {
				pasteSingleRowOrColumn(minOriginRow + row, fromRow + row,
						minOriginColumn + column, fromColumn + column);
			}
			row++;
		} while (row < destinationRange.getHeight());
	}

	private void pasteDownwards(TabularRange destinationRange,
			int minOriginRow, int minOriginColumn) {
		if (destinationRange == null) {
			return;
		}
		int column = 0;
		do {
			for (int row = 0; row < destinationRange.getHeight(); row++) {
				pasteSingleRowOrColumn(minOriginRow + row, fromRow + row,
						minOriginColumn + column, fromColumn + column);
			}
			column++;
		} while (column < destinationRange.getWidth());
	}

	private void pasteLeftwards(TabularRange destinationRange,
			int maxOriginRow, int maxOriginColumn) {
		if (destinationRange == null) {
			return;
		}
		int row = 0;
		do {
			for (int column = 0; column < destinationRange.getWidth(); column++) {
				pasteSingleRowOrColumn(maxOriginRow - row, toRow - row,
						maxOriginColumn - column, toColumn - column);
			}
			row++;
		} while (row < destinationRange.getHeight());
	}

	private void pasteSingleRowOrColumn(int sourceRow, int destinationRow,
			int sourceColumn, int destinationColumn) {
		try {
			if (destinationShouldExtendVertically(toRow)) {
				relativeCopy.doCopyVerticalNoStoringUndoInfo1(sourceColumn, sourceColumn,
						sourceRow, destinationRow, destinationRow);
			} else {
				relativeCopy.doCopyHorizontalNoStoringUndoInfo1(sourceRow, sourceRow,
						sourceColumn, destinationColumn, destinationColumn);
			}
		} catch (CircularDefinitionException | ParseException e) {
			Log.error(e);
		}
	}

	private int getMinRowIndexFromOrigin() {
		if (rangeToCopy == null) {
			return -1;
		}
		if (rangeToCopy.isColumn()) {
			return 0;
		}
		return rangeToCopy.getMinRow();
	}

	private int getMaxRowIndexFromOrigin() {
		if (rangeToCopy == null) {
			return -1;
		}
		if (rangeToCopy.isColumn()) {
			return tabularData.numberOfRows() - 1;
		}
		return rangeToCopy.getMaxRow();
	}

	private int getMinColumnIndexFromOrigin() {
		if (rangeToCopy == null) {
			return -1;
		}
		if (rangeToCopy.isRow()) {
			return 0;
		}
		return rangeToCopy.getMinColumn();
	}

	private int getMaxColumnIndexFromOrigin() {
		if (rangeToCopy == null) {
			return -1;
		}
		if (rangeToCopy.isRow()) {
			return tabularData.numberOfColumns() - 1;
		}
		return rangeToCopy.getMaxColumn();
	}

	private void extendDestinationVertically(int destinationRow) {
		boolean destinationIsUnderneath = destinationRow > getMaxRowIndexFromOrigin();
		fromRow = destinationIsUnderneath ? getMaxRowIndexFromOrigin() + 1 : destinationRow;
		toRow = destinationIsUnderneath ? destinationRow : getMinRowIndexFromOrigin() - 1;
		resetColumnIndexes();
	}

	private void resetColumnIndexes() {
		fromColumn = getMinColumnIndexFromOrigin();
		toColumn = getMaxColumnIndexFromOrigin();
	}

	private void extendDestinationHorizontally(int destinationColumn) {
		boolean destinationIsRight = destinationColumn > getMaxColumnIndexFromOrigin();
		fromColumn = destinationIsRight ? getMaxColumnIndexFromOrigin() + 1 : destinationColumn;
		toColumn = destinationIsRight ? destinationColumn : getMinColumnIndexFromOrigin() - 1;
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
