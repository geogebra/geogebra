package org.geogebra.common.spreadsheet.core;


import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.spreadsheet.kernel.HasPaste;

public class CopyPasteCutTabularDataImpl
		implements CopyPasteCutTabularData {
	private final TabularData tabularData;
	private final ClipboardInterface clipboard;
	private PasteInterface paste = null;
	private StringBuilder stringBuilder;
	private TabularBuffer buffer;

	public CopyPasteCutTabularDataImpl(TabularData tabularData, ClipboardInterface clipboard) {
		this.tabularData = tabularData;
		this.clipboard = clipboard;
		if (tabularData instanceof HasPaste) {
			paste = ((HasPaste) tabularData).getPaste();
		}
	}

	@Override
	public void copy(TabularRange range, String content) {
		StringBuilder sb = getStringBuilder();
		copyToBuffer(range, sb);
		clipboard.setContent(sb.toString());
	}

	@Override
	public <T> void copyDeep(TabularRange range, String content) {
		copy(range, content);
		if (buffer == null) {
			buffer = new TabularBuffer<T>();
		}

		buffer.setSource(range);
		buffer.clear();
		for (int row = range.fromRow; row < range.toRow + 1; row++) {
			List<T> rowData = new ArrayList<>();
			for (int column = range.fromCol; column < range.toCol + 1; column++) {
				rowData.add((T) tabularData.contentAt(row, column));
			}
			buffer.add(rowData);
		}
	}

	@Override
	public void paste(TabularRange range, String content) {
		if (buffer == null || buffer.isEmpty()) {
			// TODO
		} else {
			pasteInternalMultiple(range);
		}
	}

	private void pasteExternalMultiple(TabularRange range, String content) {

	}

	private void copyToBuffer(TabularRange range, StringBuilder sb) {
		for (int row = range.fromRow; row < range.toRow + 1; row++) {
			for (int column = range.fromCol; column < range.toCol + 1; column++) {
				Object value = tabularData.contentAt(row, column);
				sb.append(value);
				if (column != range.toCol) {
					sb.append('\t');
				}
			}
			if (row != range.toRow) {
				sb.append('\t');
			}
		}
	}

	private StringBuilder getStringBuilder() {
		if (stringBuilder == null) {
			stringBuilder = new StringBuilder();
		} else {
			stringBuilder.setLength(0);
		}
		return stringBuilder;
	}

	@Override
	public void paste(int row, int column, String content) {
		if (buffer != null) {
			pasteInternalMultiple(new TabularRange(row, column, row, column));
		} else {
			tabularData.setContent(row, column, clipboard.getContent());
		}
	}


	public void pasteInternalMultiple(TabularRange destination) {
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
	public void cut(TabularRange range, String content) {
		copy(range, content);
		if (buffer != null) {
			buffer.clear();
		}
		deleteRange(range);
	}

	private void deleteRange(TabularRange range) {
		range.forEach(((row, column) -> tabularData.setContent(row, column, null)));
	}
}
