package org.geogebra.common.spreadsheet.core;

public interface TabularDataPasteInterface<T> {
void pasteInternal(TabularData<T> tabularData, TabularClipboard<T> clipboard,
		TabularRange destination);
}