package org.geogebra.common.spreadsheet.core;

public class TabularDataPasteText implements TabularDataPasteInterface<String> {
	@Override
	public void pasteInternal(TabularData<String> tabularData,
			TabularClipboard<String> clipboard, TabularRange destination) {
		for (int row = 0; row < clipboard.numberOfRows(); row++) {
			for (int column = 0; column < clipboard.numberOfColumns(); column++) {
				tabularData.setContent(destination.fromRow + row,
						destination.fromCol + column,
						clipboard.contentAt(row, column));
			}
		}
	}
}
