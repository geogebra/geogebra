package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

final class CopyPasteCutTabularDataImpl<T>
		implements CopyPasteCutTabularData {
	private final TabularData<T> tabularData;
	private final ClipboardInterface clipboard;
	private final TabularDataPasteInterface<T> paste;
	private final TabularDataFormatter<T> tabularDataFormatter;
	private final TableLayout layout;
	private TabularClipboard<T> internalClipboard;
	private final SpreadsheetSelectionController selectionController;
	private List<Selection> pastedSelections = new ArrayList<>();

	/**
	 * @param tabularData {@link TabularData}
	 * @param clipboard {@link ClipboardInterface}
	 * @param layout Spreadsheet dimensions
	 * @param selectionController {@link SpreadsheetSelectionController}
	 */
	CopyPasteCutTabularDataImpl(TabularData<T> tabularData, ClipboardInterface clipboard,
			TableLayout layout, SpreadsheetSelectionController selectionController) {
		this.tabularData = tabularData;
		this.clipboard = clipboard;
		this.layout = layout;
		this.selectionController = selectionController;
		paste = tabularData.getPaste();
		tabularDataFormatter = new TabularDataFormatter(tabularData);
	}

	TabularClipboard getInternalClipboard() {
		return internalClipboard;
	}

	@Override
	public void copy(TabularRange range) {
		clipboard.setContent(tabularDataFormatter.toString(range));
	}

	@Override
	public void copyDeep(TabularRange source) {
		TabularRange sourceToCopy = source;
		if (source.isColumn()) {
			sourceToCopy = getColumnCopy(source);
		} else if (source.isRow()) {
			sourceToCopy = getRowCopy(source);
		} else if (source.areAllCellsSelected()) {
			sourceToCopy = getAllCellsCopy();
		}
		copy(sourceToCopy);
		if (internalClipboard == null) {
			internalClipboard = new TabularClipboard<>();
		}
		internalClipboard.copy(tabularData, sourceToCopy);
	}

	private TabularRange getColumnCopy(TabularRange source) {
		return TabularRange.range(0, tabularData.numberOfRows() - 1,
				source.getFromColumn(), source.getToColumn());
	}

	private TabularRange getRowCopy(TabularRange source) {
		return TabularRange.range(source.getFromRow(), source.getToRow(),
				0, tabularData.numberOfColumns() - 1);
	}

	private TabularRange getAllCellsCopy() {
		return TabularRange.range(0, tabularData.numberOfRows() - 1,
				0, tabularData.numberOfColumns() - 1);
	}

	@Override
	public void paste(TabularRange destination) {
		if (internalClipboard != null && !internalClipboard.isEmpty()) {
			pasteFromInternalClipboard(destination);
		} else {
			pasteFromExternalClipboard(destination);
		}
	}

	@SuppressWarnings("unused")
	private void pasteFromExternalClipboard(TabularRange destination) {
		// TODO
	}

	@Override
	public void paste(int startRow, int startColumn) {
		if (internalClipboard != null) {
			pasteFromInternalClipboard(new TabularRange(startRow, startColumn,
					startRow, startColumn));
		} else {
			tabularData.setContent(startRow, startColumn, clipboard.getContent());
		}
	}

	@Override
	public void selectPastedContent() {
		selectionController.clearSelections();
		pastedSelections.forEach(selection -> selectionController.select(selection, false, true));
		pastedSelections.clear();
	}

	/**
	 * Paste from internal clipboard
	 *
	 * If the data size on the internal clipboard differs, the following rules are true:<br/>
	 *
	 * - If destination is smaller than or equal to the data, it is pasted once starting the
	 *  first position of the destination.<br/>
	 *
	 * - If destination is bigger than the data, it may be pasted multiple times
	 *   but within desination boundaries.
	 *
	 * @param destination to paste to
	 */
	private void pasteFromInternalClipboard(TabularRange destination) {
		if (internalClipboard.isEmpty()) {
			return;
		}

		if (isSmallerOrEqualThanClipboardData(destination)) {
			int fromRow = destination.getFromRow() < 0 ? 0 : destination.getFromRow();
			int fromColumn = destination.getFromColumn() < 0 ? 0 : destination.getFromColumn();

			TabularRange destinationRangeToPasteTo = TabularRange.range(fromRow,
					fromRow + internalClipboard.numberOfRows() - 1,
					fromColumn,
					fromColumn + internalClipboard.numberOfColumns() - 1);

			pasteInternalOnce(destinationRangeToPasteTo);
		} else {
			pasteInternalMultiple(destination);
		}
	}

	private boolean isSmallerOrEqualThanClipboardData(TabularRange destination) {
		return destination.getWidth() <= internalClipboard.numberOfColumns()
				&& destination.getHeight() <= internalClipboard.numberOfRows();
	}

	/**
	 * Paste content of the internal clipboard once. It also ensures that the data has the required
	 * space to do the paste.
	 *
	 * @param destination to paste to
	 */
	private void pasteInternalOnce(TabularRange destination) {
		tabularData.ensureCapacity(destination.getMaxRow(), destination.getMaxColumn());
		insertRowsAndColumnsIfNeeded();
		destination.forEach(this::resetCell);
		paste.pasteInternal(tabularData, internalClipboard, destination);
		addDestinationToPastedSelections(destination);
	}

	private void insertRowsAndColumnsIfNeeded() {
		Selection lastSelection = selectionController.getLastSelection();
		if (layout != null && lastSelection != null) {
			int currentNumberOfColumns = layout.numberOfColumns();
			int numberOfColumnsNeeded = tabularData.numberOfColumns();
			if (currentNumberOfColumns < numberOfColumnsNeeded) {
				layout.setNumberOfColumns(numberOfColumnsNeeded);
				layout.setWidthForColumns(layout.getWidth(lastSelection.getRange().getMaxColumn()),
						currentNumberOfColumns, numberOfColumnsNeeded - 1);
			}
			int currentNumberOfRows = layout.numberOfRows();
			int numberOfRowsNeeded = tabularData.numberOfRows();
			if (currentNumberOfRows < numberOfRowsNeeded) {
				layout.setNumberOfRows(numberOfRowsNeeded);
				layout.setHeightForRows(layout.getHeight(lastSelection.getRange().getMaxRow()),
						currentNumberOfRows, numberOfRowsNeeded - 1);
			}
		}
	}

	private void addDestinationToPastedSelections(TabularRange destination) {
		TabularRange destinationToSelect = destination;
		if (rangeCoversAllCells(destination)) {
			destinationToSelect = TabularRange.range(-1, -1, -1, -1);
		} else if (rangeCoversWholeRow(destination)) {
			destinationToSelect = TabularRange.range(
					destination.getFromRow(), destination.getToRow(), -1, -1);
		} else if (rangeCoversWholeColumn(destination)) {
			destinationToSelect = TabularRange.range(
					-1, -1, destination.getFromColumn(), destination.getToColumn());
		}
		pastedSelections.add(new Selection(destinationToSelect));
	}

	private boolean rangeCoversWholeRow(TabularRange range) {
		return range.getFromColumn() == 0
				&& range.getToColumn() == tabularData.numberOfColumns() - 1;
	}

	private boolean rangeCoversWholeColumn(TabularRange range) {
		return range.getFromRow() == 0 && range.getToRow() == tabularData.numberOfRows() - 1;
	}

	private boolean rangeCoversAllCells(TabularRange range) {
		return range.equals(TabularRange.range(0, tabularData.numberOfRows() - 1,
				0, tabularData.numberOfColumns() - 1));
	}

	/**
	 * Data is pasted multiple times to destination within its boundaires.
	 * Remaining cells stay untouched.
	 * @param destination to paste to.
	*/
	private void pasteInternalMultiple(TabularRange destination) {
		int columnStep = internalClipboard.numberOfColumns();
		int rowStep = internalClipboard.numberOfRows();
		int columnMultiplier = Math.max(destination.getWidth() / columnStep, 1);
		int rowMultiplier = Math.max(destination.getHeight() / rowStep, 1);
		int maxColumn = columnStep * columnMultiplier;
		int maxRow = rowStep * rowMultiplier;
		int minRow = destination.getMinRow() < 0 ? 0 : destination.getMinRow();
		int minColumn = destination.getMinColumn() < 0 ? 0 : destination.getMinColumn();

		for (int column = minColumn; column < minColumn + maxColumn; column += columnStep) {
			for (int row = minRow; row < minRow + maxRow; row += rowStep) {
				pasteInternalOnce(new TabularRange(row, column,
						row + rowStep - 1, column + columnStep - 1));
			}
		}
	}

	@Override
	public void cut(TabularRange range) {
		copyDeep(range);
		if (range.isRow()) {
			clearRows(range);
		} else if (range.isColumn()) {
			clearColumns(range);
		} else if (range.areAllCellsSelected()) {
			clearAllCells();
		} else {
			range.forEach(this::resetCell);
		}
	}

	private void clearRows(TabularRange range) {
		TabularRange.range(range.getFromRow(), range.getToRow(),
				0, tabularData.numberOfColumns() - 1).forEach(this::resetCell);
	}

	private void clearColumns(TabularRange range) {
		TabularRange.range(0, tabularData.numberOfRows() - 1,
				range.getFromColumn(), range.getToColumn()).forEach(this::resetCell);
	}

	private void clearAllCells() {
		TabularRange.range(0, tabularData.numberOfRows() - 1,
				0, tabularData.numberOfColumns() - 1).forEach(this::resetCell);
	}

	private void resetCell(int row, int column) {
		tabularData.setContent(row, column, null);
	}
}
