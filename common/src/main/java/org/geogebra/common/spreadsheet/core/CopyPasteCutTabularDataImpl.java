package org.geogebra.common.spreadsheet.core;


public class CopyPasteCutTabularDataImpl
		implements CopyPasteCutTabularData {
	private final TabularData tabularData;
	private final ClipboardInterface clipboard;
	private StringBuilder stringBuilder;
	private TabularClipboard internalClipboard = null;

	public CopyPasteCutTabularDataImpl(TabularData tabularData, ClipboardInterface clipboard) {
		this.tabularData = tabularData;
		this.clipboard = clipboard;
	}

	@Override
	public void copy(TabularRange range, String content) {
		StringBuilder sb = getStringBuilder();
		copyToBuffer(range, sb);
		clipboard.setContent(sb.toString());
		// TODO geo copy somewhere
	}

	@Override
	public <T> void copyDeep(TabularRange range, String content) {
		copy(range, content);
		if (internalClipboard == null) {
			internalClipboard = new TabularClipboard<T>(tabularData);
		}
		internalClipboard.copy(range);
	}


	@Override
	public void paste(TabularRange range, String content) {
		if (internalClipboard.isEmpty()) {
			// TODO
		} else {
			internalClipboard.pasteInternalMultiple(range);
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
		if (internalClipboard != null) {
			internalClipboard.pasteInternalMultiple(new TabularRange(row, column, row, column));
		} else {
			tabularData.setContent(row, column, clipboard.getContent());
		}
	}


	@Override
	public void cut(TabularRange range, String content) {
		copy(range, content);
		if (internalClipboard != null) {
			internalClipboard.clear();
		}
		deleteRange(range);
	}

	private void deleteRange(TabularRange range) {
		range.forEach(((row, column) -> tabularData.setContent(row, column, null)));
	}
}
