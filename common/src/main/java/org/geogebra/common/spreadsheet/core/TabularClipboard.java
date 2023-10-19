package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;
import org.geogebra.common.spreadsheet.kernel.HasPaste;

public class TabularClipboard<T> {
	private final TabularData tabularData;
	private TabularBuffer buffer = new TabularBuffer<T>();
	private PasteInterface paste;

	public TabularClipboard(TabularData tabularData) {
		this.tabularData = tabularData;
		if (tabularData instanceof HasPaste) {
			paste = ((HasPaste) tabularData).getPaste();
		}
	}

	public void copy(TabularRange range) {
		buffer.setSource(range);
		clear();
		for (int row = range.fromRow; row < range.toRow + 1; row++) {
			List<T> rowData = new ArrayList<>();
			for (int column = range.fromCol; column < range.toCol + 1; column++) {
			 	rowData.add((T) tabularData.contentAt(row, column));
			}
			buffer.add(rowData);
		}
	}

	public void clear() {
		buffer.clear();
	}

	public boolean isEmpty() {
		return buffer.isEmpty();
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
}
