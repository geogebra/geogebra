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
	 */
	CopyPasteCutTabularDataImpl(TabularData<T> tabularData, ClipboardInterface clipboard,
			TableLayout layout, SpreadsheetSelectionController selectionController) {
		this.tabularData = tabularData;
		this.clipboard = clipboard;
		this.layout = layout;
		this.selectionController = selectionController;
		paste = tabularData.getPaste();
		tabularDataFormatter = new TabularDataFormatter<>(tabularData);
	}

	TabularClipboard<T> getInternalClipboard() {
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
		internalClipboard.copy(tabularData, sourceToCopy, new Selection(source).getType());
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
			pasteInternalMultiple(destination);
		} else {
			pasteExternal(destination, externalContent);
		}
	}

	private void pasteExternal(TabularRange destination, String[][] data) {
		TabularRange tiledRange = CopyPasteCutTabularData.getTiledRange(destination, data);
		if (tiledRange != null) {
			tabularData.getPaste()
					.pasteExternal(tabularData, data, tiledRange);
			addDestinationToPastedSelections(tiledRange, SelectionType.CELLS);
		}
	}

	@Override
	public void paste(int startRow, int startColumn, String[][] externalContent) {
		paste(new TabularRange(startRow, startColumn), externalContent);
	}

	@Override
	public void selectPastedContent() {
		selectionController.clearSelections();
		pastedSelections.forEach(selection -> selectionController.select(selection, false, true));
		pastedSelections.clear();
	}

	@Override
	public void readExternalClipboard(Consumer<String> reader) {
		// pass null if external clipboard contains data copied from this app
		// to make sure internal clipboard is used instead
		clipboard.readContent(rawExternalContent -> {
			String externalContent = rawExternalContent.replace("\r\n", "\n");
			reader.accept(
					Objects.equals(lastCopiedValue, externalContent) ? null : externalContent);
		});
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
		destination.forEach(tabularData::removeContentAt);
		paste.pasteInternal(tabularData, internalClipboard, destination);
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

	private void addDestinationToPastedSelections(TabularRange destination, SelectionType type) {
		TabularRange destinationToSelect = destination;
		if (type == SelectionType.ALL) {
			destinationToSelect = TabularRange.range(-1, -1, -1, -1);
		} else if (type == SelectionType.ROWS) {
			destinationToSelect = TabularRange.range(
					destination.getFromRow(), destination.getToRow(), -1, -1);
		} else if (type == SelectionType.COLUMNS) {
			destinationToSelect = TabularRange.range(
					-1, -1, destination.getFromColumn(), destination.getToColumn());
		}
		pastedSelections.add(new Selection(destinationToSelect));
	}

	/**
	 * Paste from internal clipboard.
	 * If the data size on the internal clipboard differs, the following rules are true
	 * in each dimension:<br/>
	 *
	 * - If destination is smaller than or equal to the data, it is pasted once starting the
	 *  first position of the destination.<br/>
	 *
	 * - If destination is bigger than the data, it may be pasted multiple times
	 *   but within destination boundaries.
	 *
	 * @param destination to paste to
	 */
	private void pasteInternalMultiple(TabularRange destination) {
		if (internalClipboard.isEmpty()) {
			return;
		}
		int columnStep = internalClipboard.numberOfColumns();
		int rowStep = internalClipboard.numberOfRows();
		TabularRange tiledRange = CopyPasteCutTabularData
				.getTiledRange(destination, rowStep, columnStep);

		for (int column = tiledRange.getMinColumn();
			 column <= tiledRange.getMaxColumn(); column += columnStep) {
			for (int row = tiledRange.getMinRow(); row <= tiledRange.getMaxRow(); row += rowStep) {
				pasteInternalOnce(new TabularRange(row, column,
						row + rowStep - 1, column + columnStep - 1));
			}
		}
		addDestinationToPastedSelections(tiledRange, internalClipboard.getType());
	}

	@Override
	public void cut(TabularRange range) {
		copyDeep(range);
		TabularRange visibleRange = range.restrictTo(tabularData.numberOfRows(),
				tabularData.numberOfColumns());
		visibleRange.forEach(tabularData::removeContentAt);
	}
}
