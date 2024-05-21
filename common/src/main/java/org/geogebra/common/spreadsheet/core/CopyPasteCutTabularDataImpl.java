package org.geogebra.common.spreadsheet.core;

final class CopyPasteCutTabularDataImpl<T>
		implements CopyPasteCutTabularData {
	private final TabularData<T> tabularData;
	private final ClipboardInterface clipboard;
	private final TabularDataPasteInterface<T> paste;
	private final TabularContent tabularContent;
	private final TableLayout layout;
	private TabularClipboard<T> internalClipboard;

	/**
	 *
	 * @param tabularData {@link TabularData}
	 * @param clipboard {@link ClipboardInterface}
	 */
	CopyPasteCutTabularDataImpl(TabularData<T> tabularData, ClipboardInterface clipboard,
			TableLayout layout) {
		this.tabularData = tabularData;
		this.clipboard = clipboard;
		this.layout = layout;
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
			pasteFromInternalClipboard(new TabularRange(startRow, startColumn, startRow, startColumn
			));
		} else {
			tabularData.setContent(startRow, startColumn, clipboard.getContent());
		}
	}

	/**
	 * Paste from internal clipboard
	 *
	 *  If the data size on the internal clipboard differs, the following rules are true:
	 *
	 *  - if destination is smaller than the data, it is pasted once starting the first position
	 *    of the destination.
	 *
	 *  - if destination is bigger, then data, it may be pasted multiple times
	 *    but within desination boundaries.
	 *
	 * @param destination to paste to
	 */
	private void pasteFromInternalClipboard(TabularRange destination) {
		if (internalClipboard.isEmpty()) {
			return;
		}

		if (destination.isSingleCell() || isSmallerOrEqualThanClipboardData(destination)) {
			pasteInternalOnce(destination);
		} else {
			pasteInternalMultiple(destination);
		}
	}

	private boolean isSmallerOrEqualThanClipboardData(TabularRange destination) {
		return destination.getWidth() <= internalClipboard.numberOfColumns()
				|| destination.getHeight() <= internalClipboard.numberOfRows();
	}

	/**
	 * Paste content of the internal clipboard once. It also ensures that the data has the required
	 * space to do the paste.
	 *
	 * @param destination to paste to
	 */
	private void pasteInternalOnce(TabularRange destination) {
		tabularData.ensureCapacity(destination.getMaxRow(), destination.getMaxColumn());
		if (layout != null) {
			layout.setNumberOfColumns(tabularData.numberOfColumns());
			layout.setNumberOfRows(tabularData.numberOfRows());
		}
		paste.pasteInternal(tabularData, internalClipboard, destination);
	}

	/**
	 * Data is pasted multiple times to destination within its boundaires.
	 * Remaining cells stay untouched.
	 * @param destination to paste to.
	*/
	private void pasteInternalMultiple(TabularRange destination) {
		int columnStep = internalClipboard.numberOfRows();
		int rowStep = internalClipboard.numberOfColumns();
		int columnMultiplier = Math.max(destination.getWidth() / columnStep, 1);
		int rowMultiplier = Math.max(destination.getHeight() / rowStep, 1);
		int maxColumn = columnStep * columnMultiplier ;
		int maxRow = rowStep * rowMultiplier;

		for (int column = destination.getFromColumn();
			 column < destination.getFromColumn() + maxColumn; column += columnStep) {
			for (int row = destination.getFromRow(); row < destination.getFromRow() + maxRow;
				 row += rowStep) {
				pasteInternalOnce(new TabularRange(row, column,
						row + columnStep, column + rowStep));
			}
		}
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
