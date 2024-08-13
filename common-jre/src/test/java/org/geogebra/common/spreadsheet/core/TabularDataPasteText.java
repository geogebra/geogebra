package org.geogebra.common.spreadsheet.core;

public class TabularDataPasteText implements TabularDataPasteInterface<String> {
	@Override
	public void pasteInternal(TabularData<String> tabularData,
			TabularClipboard<String> clipboard, TabularRange destination) {
		for (int row = 0; row < clipboard.numberOfRows(); row++) {
			for (int column = 0; column < clipboard.numberOfColumns(); column++) {
				tabularData.setContent(destination.getFromRow() + row,
						destination.getFromColumn() + column,
						clipboard.contentAt(row, column));
			}
		}
	}

	@Override
	public void pasteExternal(TabularData<String> tabularData, String[][] clipboardContent,
			TabularRange destination) {
		throw new UnsupportedOperationException();
	}
}
