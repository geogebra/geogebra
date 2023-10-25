package org.geogebra.common.spreadsheet.core;

public final class CopyPasteCutTabularDataImpl
		implements CopyPasteCutTabularData {
	private final TabularData tabularData;
	private final ClipboardInterface clipboard;
	private final TabularDataPasteInterface paste;
	private final TabularContent tabularContent;
	private TabularBuffer buffer;

	/**
	 *
	 * @param tabularData {@link TabularData}
	 * @param clipboard {@link ClipboardInterface}
	 */
	public CopyPasteCutTabularDataImpl(TabularData tabularData, ClipboardInterface clipboard) {
		this.tabularData = tabularData;
		this.clipboard = clipboard;
		paste = tabularData.getPaste();
		tabularContent = new TabularContent(tabularData);
	}

	@Override
	public void copy(TabularRange range) {
		clipboard.setContent(tabularContent.toString(range));
	}


	@Override
	public void copyDeep(TabularRange range) {
		copy(range);
		if (buffer == null) {
			buffer = new TabularBuffer<>();
		}
		buffer.copy(tabularData, range);
	}

	@Override
	public void paste(TabularRange range) {
		if (buffer == null || buffer.isEmpty()) {
			// TODO
		} else {
			pasteInternalMultiple(range);
		}
	}

	@Override
	public void paste(int row, int column) {
		if (buffer != null) {
			pasteInternalMultiple(new TabularRange(row, column, row, column));
		} else {
			tabularData.setContent(row, column, clipboard.getContent());
		}
	}

	private void pasteInternalMultiple(TabularRange destination) {
		int columnStep = buffer.numberOfRows();
		int rowStep = buffer.numberOfColumns();

		if (columnStep == 0 || rowStep == 0) {
			return;
		}

		int maxColumn = destination.isSingleton()
				? destination.fromCol + columnStep
				: destination.toCol;
		int maxRow = destination.isSingleton()
				? destination.fromRow + rowStep
				: destination.toRow;

		for (int column = destination.fromCol; column <= destination.toCol ; column += columnStep) {
			for (int row = destination.fromRow; row <= destination.toRow ; row += rowStep) {
				pasteInternal(new TabularRange(row, maxRow, column, maxColumn));
			}
		}
	}

	private void pasteInternal(TabularRange destination) {
		extendDataIfNeeded(destination);
		paste.pasteInternal(tabularData, buffer, destination);
	}

	private void extendDataIfNeeded(TabularRange destination) {
		int maxRows = tabularData.numberOfRows();
		if (maxRows < destination.toCol + 1) {
			for (int i = maxRows; i <= destination.toCol; i++) {
				tabularData.insertColumnAt(maxRows);
			}
		}

		int maxColumns = tabularData.numberOfColumns();
		if (maxColumns < destination.toCol + 1) {
			for (int i = maxColumns; i <= destination.toCol; i++) {
				tabularData.insertColumnAt(maxColumns);
			}
		}
	}

	@Override
	public void cut(TabularRange range) {
		copy(range);
		if (buffer != null) {
			buffer.clear();
		}
		range.forEach((row, column) -> tabularData.setContent(row, column, null));
	}

}
