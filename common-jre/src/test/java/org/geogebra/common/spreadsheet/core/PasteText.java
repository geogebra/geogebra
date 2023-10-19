package org.geogebra.common.spreadsheet.core;

public class PasteText implements PasteInterface<String> {
	@Override
	public void pasteInternal(TabularData<String> tabularData,
			TabularBuffer<String> buffer, TabularRange destination) {
		for (int row = 0; row < buffer.numberOfRows(); row++) {
			for (int column = 0; column < buffer.numberOfColumns(); column++) {
				tabularData.setContent(destination.fromRow + row,
						destination.fromCol + column,
						buffer.contentAt(row, column));
			}
		}
	}
}
