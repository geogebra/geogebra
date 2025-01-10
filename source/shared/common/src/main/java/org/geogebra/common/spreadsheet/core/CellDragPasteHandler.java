package org.geogebra.common.spreadsheet.core;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.io.XMLParseException;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.util.debug.Log;

/**
 * Utility class designed to handle dragging a selection in order to copy its content to adjacent
 * cells
 */
public class CellDragPasteHandler<T> {

	private enum PasteDirection { UP, RIGHT, DOWN, LEFT }

	private TabularRange rangeToCopy;
	private final TabularData<T> tabularData;
	private final Kernel kernel;
	private final RelativeCopy relativeCopy;
	private int fromRow;
	private int toRow;
	private int fromColumn;
	private int toColumn;
	private PasteDirection pasteDirection;

	/**
	 * @param tabularData {@link TabularData}
	 * @param kernel {@link Kernel} - Needed for {@link RelativeCopy}
	 */
	public CellDragPasteHandler(TabularData<T> tabularData, Kernel kernel) {
		this.tabularData = tabularData;
		this.kernel = kernel;
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
	 * @return The selected range used to copy a selection to
	 */
	public @CheckForNull TabularRange getDragPasteDestinationRange() {
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
		if (getDragPasteDestinationRange() == null || rangeToCopy == null) {
			return;
		}
		setPasteDirection();
		try {
			kernel.getConstruction().startCollectingRedefineCalls();
			pasteToCorrectDirection();
			kernel.getConstruction().processCollectedRedefineCalls();
		} catch (CircularDefinitionException | ParseException | XMLParseException e) {
			Log.error(e);
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

	private void setPasteDirection() {
		if (destinationShouldExtendVertically(toRow)) {
			if (destinationIsRightOrDown()) {
				pasteDirection = PasteDirection.DOWN;
			} else {
				pasteDirection = PasteDirection.UP;
			}
		} else {
			if (destinationIsRightOrDown()) {
				pasteDirection = PasteDirection.RIGHT;
			} else {
				pasteDirection = PasteDirection.LEFT;
			}
		}
	}

	private boolean destinationIsRightOrDown() {
		return fromRow > getMaxRowIndexFromOrigin() || fromColumn > getMaxColumnIndexFromOrigin();
	}

	private void pasteToCorrectDirection() throws CircularDefinitionException, ParseException {
		TabularRange destinationRange = getDragPasteDestinationRange();
		if (destinationRange == null) {
			return;
		}
		unfixDestinationRange(destinationRange);
		switch (pasteDirection) {
		case UP:
		case DOWN:
			pasteVertical(destinationRange, getMinRowIndexFromOrigin(), getMaxRowIndexFromOrigin(),
					getMinColumnIndexFromOrigin(), getMaxColumnIndexFromOrigin());
			break;
		case RIGHT:
		case LEFT:
			pasteHorizontal(destinationRange, getMinRowIndexFromOrigin(),
					getMaxRowIndexFromOrigin(), getMinColumnIndexFromOrigin(),
					getMaxColumnIndexFromOrigin());
			break;
		}
		setDestinationRangeToNonEmptySpreadsheetCells(destinationRange);
	}

	private void unfixDestinationRange(TabularRange destinationRange) {
		destinationRange.forEach((row, column) -> {
			GeoElement geo = (GeoElement) tabularData.contentAt(row, column);
			if (geo != null && geo.isLocked()) {
				geo.setFixed(false);
			}
		});
	}

	private void pasteVertical(TabularRange destinationRange, int minOriginRow, int maxOriginRow,
			int minOriginColumn, int maxOriginColumn)
			throws CircularDefinitionException, ParseException {

		if (shouldCopySingleRowOnly()) {
			pasteSingleRow(minOriginColumn, maxOriginColumn,
					pasteDirection == PasteDirection.UP ? maxOriginRow : minOriginRow,
					fromRow, toRow);
			return;
		}

		if (shouldPasteLinearPattern()) {
			if (pasteDirection == PasteDirection.UP) {
				relativeCopy.pasteLinearPatternUpwards(minOriginRow, maxOriginRow,
						minOriginColumn, maxOriginColumn, fromRow, toRow);
			} else {
				relativeCopy.pasteLinearPatternDownwards(minOriginRow, maxOriginRow,
						minOriginColumn, maxOriginColumn, fromRow, toRow);
			}
			return;
		}

		int column = 0;
		do {
			for (int row = 0; row < destinationRange.getHeight(); row++) {
				if (pasteDirection == PasteDirection.UP) {
					pasteRowOrColumn(maxOriginRow - row, toRow - row,
							maxOriginColumn - column, toColumn - column);
				} else {
					pasteRowOrColumn(minOriginRow + row, fromRow + row,
							minOriginColumn + column, fromColumn + column);
				}
			}
			column++;
		} while (column < destinationRange.getWidth());
	}

	private void pasteHorizontal(TabularRange destinationRange, int minOriginRow, int maxOriginRow,
			int minOriginColumn, int maxOriginColumn)
			throws CircularDefinitionException, ParseException {

		if (shouldCopySingleColumnOnly()) {
			pasteSingleColumn(minOriginRow, maxOriginRow,
					pasteDirection == PasteDirection.RIGHT ? minOriginColumn : maxOriginColumn,
					fromColumn, toColumn);
			return;
		}

		if (shouldPasteLinearPattern()) {
			if (pasteDirection == PasteDirection.RIGHT) {
				relativeCopy.pasteLinearPatternRightwards(minOriginRow, maxOriginRow,
						minOriginColumn, maxOriginColumn, fromColumn, toColumn);
			} else {
				relativeCopy.pasteLinearPatternLeftwards(minOriginRow, maxOriginRow,
						minOriginColumn, maxOriginColumn, fromColumn, toColumn);
			}
			return;
		}

		int row = 0;
		do {
			for (int column = 0; column < destinationRange.getWidth(); column++) {
				if (pasteDirection == PasteDirection.RIGHT) {
					pasteRowOrColumn(minOriginRow + row, fromRow + row,
							minOriginColumn + column, fromColumn + column);
				} else {
					pasteRowOrColumn(maxOriginRow - row, toRow - row,
							maxOriginColumn - column, toColumn - column);
				}
			}
			row++;
		} while (row < destinationRange.getHeight());
	}

	private void setDestinationRangeToNonEmptySpreadsheetCells(TabularRange destinationRange) {
		destinationRange.forEach((row, column) -> {
			tabularData.markNonEmpty(row, column);
		});
	}

	private boolean shouldCopySingleRowOnly() {
		return rangeToCopy.getHeight() == 1;
	}

	private boolean shouldCopySingleColumnOnly() {
		return rangeToCopy.getWidth() == 1;
	}

	private void pasteSingleRow(int sourceMinColumn, int sourceMaxColumn, int sourceRow,
			int destinationMinRow, int destinationMaxRow)
			throws CircularDefinitionException, ParseException {
		relativeCopy.doCopyVerticalNoStoringUndoInfo1(sourceMinColumn, sourceMaxColumn,
				sourceRow, destinationMinRow, destinationMaxRow);
	}

	private void pasteSingleColumn(int sourceMinRow, int sourceMaxRow, int sourceColumn,
			int destinationMinColumn, int destinationMaxColumn)
			throws CircularDefinitionException, ParseException {
		relativeCopy.doCopyHorizontalNoStoringUndoInfo1(sourceMinRow, sourceMaxRow,
				sourceColumn, destinationMinColumn, destinationMaxColumn);
	}

	private void pasteRowOrColumn(int sourceRow, int destinationRow,
			int sourceColumn, int destinationColumn)
			throws CircularDefinitionException, ParseException {
		if (isVerticalPasteDirection()) {
			relativeCopy.doCopyVerticalNoStoringUndoInfo1(sourceColumn, sourceColumn,
					sourceRow, destinationRow, destinationRow);
		} else {
			relativeCopy.doCopyHorizontalNoStoringUndoInfo1(sourceRow, sourceRow,
					sourceColumn, destinationColumn, destinationColumn);
		}
	}

	private boolean shouldPasteLinearPattern() {
		if (isVerticalPasteDirection()) {
			return rangeToCopy.getHeight() == 2
					&& RelativeCopy.isPatternSource(rangeToCopy, kernel.getApplication());
		} else {
			return rangeToCopy.getWidth() == 2
					&& RelativeCopy.isPatternSource(rangeToCopy, kernel.getApplication());
		}
	}

	private boolean isVerticalPasteDirection() {
		return pasteDirection == PasteDirection.UP || pasteDirection == PasteDirection.DOWN;
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
