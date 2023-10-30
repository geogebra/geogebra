package org.geogebra.common.spreadsheet.core;

public final class CopyPasteCutTabularDataImpl
		implements CopyPasteCutTabularData {
	private final TabularData tabularData;
	private final ClipboardInterface clipboard;
	private final TabularDataPasteInterface paste;
	private final TabularContent tabularContent;
	private TabularClipboard internalClipboard;

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
	public void copyDeep(TabularRange source) {
		copy(source);
		if (internalClipboard == null) {
			internalClipboard = new TabularClipboard<>();
		}
		internalClipboard.copy(tabularData, source);
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

	/**
	 * Paste from internal clipboard
	 *
	 *  Note that if the source range is smaller, than the destination, the source is pasted
	 *  multiple times to fill (and may ovelap) the destination.
	 *
	 *
	 * @param destination to paste to
	 */
	private void pasteFromInternalClipboard(TabularRange destination) {
		int columnStep = internalClipboard.numberOfRows();
		int rowStep = internalClipboard.numberOfColumns();

		if (columnStep == 0 || rowStep == 0) {
			return;
		}

		int maxColumn = destination.isEmpty()
				? destination.fromCol + columnStep
				: destination.toCol;
		int maxRow = destination.isEmpty()
				? destination.fromRow + rowStep
				: destination.toRow;

		for (int column = destination.fromCol; column <= destination.toCol ; column += columnStep) {
			for (int row = destination.fromRow; row <= destination.toRow ; row += rowStep) {
				pasteInternal(new TabularRange(row, maxRow, column, maxColumn));
			}
		}
	}

	/**
	 * Paste content of the internal clipboard once. It also ensures that the data has the required
	 * space to do the paste.
	 *
	 * @param destination to paste to
	 */
	private void pasteInternal(TabularRange destination) {
		tabularData.ensureCapacity(destination.toRow, destination.toCol);
		paste.pasteInternal(tabularData, internalClipboard, destination);
	}

	@Override
	public void cut(TabularRange range) {
		copy(range);
		if (internalClipboard != null) {
			internalClipboard.clear();
		}
		range.forEach((row, column) -> tabularData.setContent(row, column, null));
	}
}
