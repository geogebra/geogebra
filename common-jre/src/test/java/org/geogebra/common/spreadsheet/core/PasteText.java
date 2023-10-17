package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;

public class PasteText implements PasteInterface {
	@Override
	public void pasteInternal(TabularData tabularData, HasTabularValues buffer, TabularRange destination) {
		for (int row = 0; row < buffer.numberOfRows(); row++) {
			for (int column = 0; column < buffer.numberOfColumns(); column++) {
				tabularData.setContent(destination.fromRow + row,
						destination.fromCol + column,
						buffer.contentAt(row, column));
			}
		}
	}
}
