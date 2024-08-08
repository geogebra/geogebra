package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

final class CopyPasteCutTabularDataImpl<T>
		implements CopyPasteCutTabularData {
	private final TabularData<T> tabularData;
	private final ClipboardInterface clipboard;
	private final TabularDataPasteInterface<T> paste;
	private final TabularDataFormatter<T> tabularDataFormatter;
	private final TableLayout layout;
	private TabularClipboard<T> internalClipboard;
	private final SpreadsheetSelectionController selectionController;
	private final List<Selection> pastedSelections = new ArrayList<>();
	private String lastCopiedValue;

	/**
	 * @param tabularData {@link TabularData}
	 * @param clipboard {@link ClipboardInterface}
	 * @param layout Spreadsheet dimensions
	 * @param selectionController {@link SpreadsheetSelectionController}
	 * @param serializer for formatting strings for clipboard
	 */
	CopyPasteCutTabularDataImpl(TabularData<T> tabularData, ClipboardInterface clipboard,
			TableLayout layout, SpreadsheetSelectionController selectionController,
			SpreadsheetCellDataSerializer serializer) {
		this.tabularData = tabularData;
		this.clipboard = clipboard;
		this.layout = layout;
		this.selectionController = selectionController;
		paste = tabularData.getPaste();
		tabularDataFormatter = new TabularDataFormatter(tabularData, serializer);
	}

	TabularClipboard getInternalClipboard() {
		return internalClipboard;
	}

	@Override
	public void copy(TabularRange range) {
		lastCopiedValue = tabularDataFormatter.toString(range);
		clipboard.setContent(lastCopiedValue);
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
	public void paste(TabularRange destination, String[][] externalContent) {
		if (externalContent == null || externalContent.length == 0) {
			pasteFromInternalClipboard(destination);
		} else {
			pasteExternal(destination, externalContent);
		}
	}

	private void pasteExternal(TabularRange destination, String[][] externalContent) {
		tabularData.getPaste().pasteExternal(tabularData, externalContent, destination);
		addDestinationToPastedSelections(destination);
	}

	@Override
	public void paste(int startRow, int startColumn, String[][] externalContent) {
		if (externalContent == null || externalContent.length == 0) {
			pasteFromInternalClipboard(new TabularRange(startRow, startColumn,
					startRow, startColumn));
		} else {
			pasteExternal(new TabularRange(startRow, startColumn), externalContent);
		}
	}

	@Override
	public void selectPastedContent() {
		selectionController.clearSelections();
		pastedSelections.forEach(selection -> selectionController.select(selection, false, true));
		pastedSelections.clear();
	}

	@Override
	public void readExternalClipboard(Consumer<String> reader) {
		clipboard.readContent(content -> reader.accept(
				Objects.equals(lastCopiedValue, content) ? null : content));
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
			int fromRow = Math.max(destination.getFromRow(), 0);
			int fromColumn = Math.max(destination.getFromColumn(), 0);

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
		int minRow = Math.max(destination.getMinRow(), 0);
		int minColumn = Math.max(destination.getMinColumn(), 0);

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
