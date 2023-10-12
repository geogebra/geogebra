package org.geogebra.common.spreadsheet.core;


public class CopyPasteCutTabularDataImpl
		implements CopyPasteCutTabularData {
	private final TabularData tabularData;
	private final ClipboardInterface clipboard;
	private StringBuilder stringBuilder;

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
	public void paste(TabularRange range, String content) {

	}

	@Override
	public void cut(TabularRange range, String content) {
		copy(range, content);
		deleteRange(range);
	}

	private void deleteRange(TabularRange range) {
		for (int row = range.fromRow; row < range.toRow + 1; row++) {
			for (int column = range.fromCol; column < range.toCol + 1; column++) {
				tabularData.setContent(row, column, null);
			}
		}
	}
}
